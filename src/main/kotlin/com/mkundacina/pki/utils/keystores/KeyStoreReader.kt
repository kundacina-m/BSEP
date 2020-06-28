package com.mkundacina.pki.utils.keystores

import com.mkundacina.pki.data.IssuerData
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.springframework.util.ResourceUtils
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Paths
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*

object KeyStoreReader {
    //KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
    //Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
    // - Sertifikati koji ukljucuju javni kljuc
    // - Privatni kljucevi
    // - Tajni kljucevi, koji se koriste u simetricnima siframa
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance("JKS", "SUN")
    }

    /**
     * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
     * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
     *
     * @param keyStoreFile - datoteka odakle se citaju podaci
     * @param alias        - alias putem kog se identifikuje sertifikat izdavaoca
     * @param password     - lozinka koja je neophodna da se otvori key store
     * @param keyPass      - lozinka koja je neophodna da se izvuce privatni kljuc
     * @return - podatke o izdavaocu i odgovarajuci privatni kljuc
     */
    fun readIssuerFromStore(keyStoreFile: String, alias: String, password: CharArray, keyPass: CharArray): IssuerData? =
            try {
                //Datoteka se ucitava
                val file = BufferedInputStream(FileInputStream(keyStoreFile))
                keyStore.load(file, password)
                //Iscitava se sertifikat koji ima dati alias
                val cert = keyStore.getCertificate(alias)
                //Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
                val privKey = keyStore.getKey(alias, keyPass) as PrivateKey
                val issuerName = JcaX509CertificateHolder(cert as X509Certificate).subject
                IssuerData(issuerName, privKey)
            } catch (e: java.lang.Exception) {
                null
            }

    /**
     * Ucitava sertifikat is KS fajla
     */
    fun readCertificate(keyStoreFile: String, keyStorePass: String, alias: String): Certificate {
        println("Prosledjen keystore fajl $keyStoreFile")
        println("Prosledjen keyStorePass $keyStorePass")
        println("Prosledjen alias $alias")
        //kreiramo instancu KeyStore
        val ks = KeyStore.getInstance("JKS", "SUN")
        println("Posle ks get instance")
        //ucitavamo podatke
        val stream = BufferedInputStream(FileInputStream(keyStoreFile))
        println("Posle buffered reader")
        ks.load(stream, keyStorePass.toCharArray())
        println("Posle ks load-a")
        println("Sta je ks " + ks.type)
        if (ks.isKeyEntry(alias)) {
            println("Udjes li u if?")
            val cert = ks.getCertificate(alias)
            println("Sta vracas iz key store reader-a?")
            return cert
        }
        throw Exception("Couldn't read certificate")
    }

    /**
     * Ucitava privatni kljuc is KS fajla
     */
    fun readPrivateKey(keyStoreFile: String, keyStorePass: String, alias: String?, pass: String): PrivateKey? {
        //kreiramo instancu KeyStore
        val ks = KeyStore.getInstance("JKS", "SUN")
        //ucitavamo podatke
        val stream = BufferedInputStream(FileInputStream(keyStoreFile))
        ks.load(stream, keyStorePass.toCharArray())
        if (ks.isKeyEntry(alias)) {
            return ks.getKey(alias, pass.toCharArray()) as PrivateKey
        }
        throw Exception("Couldn't read private key")
    }

    fun readAllCertificates(keyStoreFile: String, keyStorePass: String): ArrayList<Certificate>? {
        val certs = ArrayList<Certificate>(50)
        try {
            val stream = BufferedInputStream(FileInputStream(keyStoreFile))
            keyStore.load(stream, keyStorePass.toCharArray())
            val es = keyStore.aliases()
            var alias = ""
            while (es.hasMoreElements()) {
                alias = es.nextElement() as String
                val chain = keyStore.getCertificateChain(alias)
                val c = chain[chain.size - 1]
                //Certificate c = ks.getCertificate(alias);
                certs.add(c)
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