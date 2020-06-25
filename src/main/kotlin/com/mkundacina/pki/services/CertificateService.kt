package com.mkundacina.pki.services

import com.mkundacina.pki.certificates.CertificateGenerator
import com.mkundacina.pki.keystores.KeyStoreReader
import com.mkundacina.pki.keystores.KeyStoreWriter
import com.mkundacina.pki.model.Certificate
import com.mkundacina.pki.repository.CertificateRepository
import data.IssuerData
import data.SubjectData
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.*
import java.security.cert.X509Certificate
import java.util.*

@Service
class CertificateService {

    @Autowired
    lateinit var certificateRepository: CertificateRepository

    fun createCertificate(certificate: Certificate) {
        certificateRepository.save(certificate)
    }

    fun findCertificateBySerial(serial: String) = certificateRepository.findBySerialNumberSubject(serial)

    fun findCaCertificates() = certificateRepository.findByIsCATrue()


    private fun getKeyPair(): KeyPair? {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
            keyGen.initialize(2048, random)
            return keyGen.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
        return null
    }

    fun createRootCertificate(certificate: Certificate, type: String): Boolean {
//        return if (validation(certificate)) {
//            if (type == "Root") {
//                val cer = certificateRepository.save(certificate)
//                val selfKey = getKeyPair()
//                val subjectData = getSubjectData(cer, selfKey!!.public)
//                System.out.println(certificate.startDate)
//                System.out.println(certificate.startDate)
//                val builder = X500NameBuilder(BCStyle.INSTANCE)
//                builder.addRDN(BCStyle.CN, certificate.getName().toString() + " " + certificate.getSurname())
//                builder.addRDN(BCStyle.SURNAME, certificate.getName())
//                builder.addRDN(BCStyle.GIVENNAME, certificate.getSurname())
//                builder.addRDN(BCStyle.E, certificate.getEmail())
//                val issuerData = IssuerData(selfKey.private, builder.build())
//                val certGenerator = CertificateGenerator
//                val certX509: X509Certificate = certGenerator.generateCertificate(subjectData!!, issuerData)!!
//                val keyStoreFile = "first.jks"
//                val kw = KeyStoreWriter
//                kw.loadKeyStore(keyStoreFile, "bsep20".toCharArray())
//                kw.write(subjectData!!.serialNumber, selfKey.private, "bsep20".toCharArray(), arrayOf<java.security.cert.Certificate>(certX509))
//                println("Korenski sertifikat " + certX509.serialNumber)
//                kw.saveKeyStore(keyStoreFile, "bsep20".toCharArray())
//                true
//            } else if (type == "Intermediate") {
//                val cer = certificateRepository.save(certificate)
//                val issuerData: IssuerData = KeyStoreReader.readIssuerFromStore("first.jks", certificate.getIssuer(), "bsep20".toCharArray(), "bsep20".toCharArray())!!
//                val subjectKey = getKeyPair()
//                val subjectData = getSubjectData(cer, subjectKey!!.public)
//                val certGenerator = CertificateGenerator
//                val certX509: X509Certificate = certGenerator.generateCertificate(subjectData!!, issuerData)!!
//                var keyStoreFile = ""
//                keyStoreFile = "first.jks"
//                val kw = KeyStoreWriter
//                kw.loadKeyStore(keyStoreFile, "bsep20".toCharArray())
//                kw.write(subjectData!!.serialNumber, subjectKey.private, "bsep20".toCharArray(), arrayOf<java.security.cert.Certificate>(certX509))
//                kw.saveKeyStore(keyStoreFile, "bsep20".toCharArray())
//                true
//            } else if (type == "End-entity") {
//                val cer = certificateRepository.save(certificate)
//                val issuerData: IssuerData = KeyStoreReader.readIssuerFromStore("first.jks", certificate.serialNumberIssuer, "bsep20".toCharArray(), "bsep20".toCharArray())!!
//                val subjectKey = getKeyPair()
//                val subjectData = getSubjectData(cer, subjectKey!!.public)
//                val certGenerator = CertificateGenerator
//                val certX509: X509Certificate = certGenerator.generateCertificate(subjectData!!, issuerData)!!
//                var keyStoreFile = ""
//                keyStoreFile = "second.jks"
//                val kw = KeyStoreWriter
//                kw.loadKeyStore(keyStoreFile, "bsep20".toCharArray())
//                kw.write(subjectData!!.serialNumber, subjectKey.private, "bsep20".toCharArray(), arrayOf<java.security.cert.Certificate>(certX509))
//                kw.saveKeyStore(keyStoreFile, "bsep20".toCharArray())
//                true
//            } else {
//                false
//            }
//        } else false
        return true
    }

    private fun validation(certificate: Certificate) : Boolean = true

    private fun getSubjectData(certificate: Certificate, pk: PublicKey): SubjectData? {
        //KeyPair keyPairSubject = getKeyPair();
        val startDate: Date = certificate.startDate
        val endDate: Date = certificate.endDate
        val serialNumber: String = certificate.serialNumberSubject
        val builder = X500NameBuilder(BCStyle.INSTANCE)
        builder.addRDN(BCStyle.CN, certificate.commonName)
        builder.addRDN(BCStyle.O, certificate.organization)
        builder.addRDN(BCStyle.E, certificate.email)
        builder.addRDN(BCStyle.C, certificate.country)
        builder.addRDN(BCStyle.SERIALNUMBER, certificate.serialNumberSubject)
        return SubjectData(pk, builder.build(), serialNumber, startDate, endDate)
    }
}