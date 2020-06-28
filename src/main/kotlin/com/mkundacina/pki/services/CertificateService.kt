package com.mkundacina.pki.services

import com.mkundacina.pki.data.IssuerData
import com.mkundacina.pki.data.SubjectData
import com.mkundacina.pki.logger
import com.mkundacina.pki.model.entities.Certificate
import com.mkundacina.pki.repository.CertificateRepository
import com.mkundacina.pki.utils.CertificateGenerator
import com.mkundacina.pki.utils.keystores.KeyStoreReader
import com.mkundacina.pki.utils.keystores.KeyStoreWriter
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*

private const val caStore = "cert_authority.jks"
private const val endEStore = "end_entity.jks"

@Service
class CertificateService {

    @Autowired
    lateinit var certificateRepository: CertificateRepository

    private val keyStorePass = "password"

    fun findCertificateBySerial(serial: String) = certificateRepository.findBySerialNumberSubject(serial)

    private fun getKeyPair(): KeyPair =
            KeyPairGenerator.getInstance("RSA").apply {
                initialize(2048, SecureRandom.getInstance("SHA1PRNG", "SUN"))
            }.generateKeyPair()

    fun createSelfSignedCertificateCA(certificate: Certificate): Boolean {
        certificateRepository.save(certificate)

        val selfKey = getKeyPair()
        val subjectData: SubjectData = getSubjectData(certificate, selfKey.public)

        val builder = X500NameBuilder(BCStyle.INSTANCE).apply {
            addRDN(BCStyle.O, certificate.organization)
            addRDN(BCStyle.C, certificate.country)
            addRDN(BCStyle.E, certificate.email)
        }

        val issuerData = IssuerData(builder.build(), selfKey.private)
        val certX509: X509Certificate = CertificateGenerator.generateCertificate(subjectData, issuerData, true)!!
        val keyStoreFile = caStore
        val kw = KeyStoreWriter

        kw.loadKeyStore(keyStoreFile, keyStorePass.toCharArray())
        kw.write(subjectData.serialNumber, selfKey.private, keyStorePass.toCharArray(), arrayOf(certX509))
        kw.saveKeyStore(keyStoreFile, keyStorePass.toCharArray())

        return true
    }

    fun createSignedCertificates(certificate: Certificate): Boolean {
        certificateRepository.save(certificate)

        val issuerData: IssuerData = KeyStoreReader.readIssuerFromStore(caStore, certificate.serialNumberIssuer, keyStorePass.toCharArray(), keyStorePass.toCharArray())!!
        val subjectKey = getKeyPair()
        val subjectData = getSubjectData(certificate, subjectKey.public)

        val certX509: X509Certificate = CertificateGenerator.generateCertificate(subjectData, issuerData, false)!!

        val keyStoreFile = if (certificate.isCA) caStore else endEStore

        val kw = KeyStoreWriter
        val chain: Array<java.security.cert.Certificate> = addToChain(keyStoreFile, certificate.isCA, kw, certificate.serialNumberIssuer, certX509)!!
        kw.write(subjectData.serialNumber, subjectKey.private, keyStorePass.toCharArray(), chain)
        kw.saveKeyStore(keyStoreFile, keyStorePass.toCharArray())

        return true
    }

    private fun findFromFile(serialNumber: String, isCA: Boolean): java.security.cert.Certificate? {
        val keyStoreFile = if (isCA) caStore else endEStore
        return KeyStoreReader.readCertificate(keyStoreFile, keyStorePass, serialNumber)
    }

    private fun addToChain(keyStoreFile: String, isCA: Boolean, kw: KeyStoreWriter, serialNumber: String, newCert: X509Certificate): Array<java.security.cert.Certificate>? {
        kw.loadKeyStore(keyStoreFile, keyStorePass.toCharArray())
        return if (isCA) {
            val chain: Array<java.security.cert.Certificate?> = kw.getChain(serialNumber)!! // issuer's chain
            val convertedArray = ArrayList(Arrays.asList(*chain))
            convertedArray.add(newCert)
            convertedArray.toArray(chain)
        } else arrayOf(newCert)
    }

    private fun getSubjectData(certificate: Certificate, pk: PublicKey): SubjectData {
        val startDate: Date = Date()
        val endDate: Date = Date()
        val serialNumber: String = certificate.serialNumberSubject

        val builder = X500NameBuilder(BCStyle.INSTANCE)
        builder.addRDN(BCStyle.O, certificate.organization)
        builder.addRDN(BCStyle.C, certificate.country)
        builder.addRDN(BCStyle.E, certificate.email)
        return SubjectData(pk, builder.build(), serialNumber, startDate, endDate)
    }

    fun revokeCertificate(certificate: Certificate): Boolean {
        val baseCertificate: Certificate = revokeFromDB(certificate.serialNumberSubject)
        logger.get().info("Certificate with serialNum ${certificate.serialNumberSubject} revoked from DB")

        if (baseCertificate.isCA) {
            val allCACertificates: ArrayList<java.security.cert.Certificate> = revokeDownTheChain(baseCertificate.serialNumberSubject)
            val allEECertificates: ArrayList<java.security.cert.Certificate> = KeyStoreReader.readAllCertificates(endEStore, keyStorePass)
                    ?: arrayListOf()

            allEECertificates.forEach { endEntity ->
                allCACertificates.forEach { ca ->
                    if ((endEntity as X509Certificate).issuerX500Principal.name == (ca as X509Certificate).subjectX500Principal.name)
                        revokeFromDB(endEntity.serialNumber.toString())
                }
            }
            for (ca in allCACertificates) {
                revokeFromDB((ca as X509Certificate).serialNumber.toString())
            }
        }
        return true
    }

    private fun revokeFromDB(serialNumber: String): Certificate =
            certificateRepository.findBySerialNumberSubject(serialNumber).apply {
                isRevoked = true
                revocationReason = "random reason"
                revocationTimestamp = Date().toString()
                certificateRepository.save(this)
            }

    private fun revokeDownTheChain(serialNumber: String): ArrayList<java.security.cert.Certificate> {
        val certs = arrayListOf<java.security.cert.Certificate>()
        try {
            val ks = KeyStore.getInstance("JKS", "SUN")
            val stream = BufferedInputStream(FileInputStream(caStore))
            ks.load(stream, keyStorePass.toCharArray())
            val es = ks.aliases()
            var alias = ""
            while (es.hasMoreElements()) {
                alias = es.nextElement() as String
                val chain = ks.getCertificateChain(alias)
                val c = chain[chain.size - 1]
                for (i in chain.indices) {
                    if ((chain[i] as X509Certificate).serialNumber.toString() == serialNumber) {
                        certs.add(c)
                        break
                    }
                }
            }
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return certs
    }

}