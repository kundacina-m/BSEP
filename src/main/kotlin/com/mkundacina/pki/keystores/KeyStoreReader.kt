package com.mkundacina.pki.keystores

import data.IssuerData
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Paths
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

object KeyStoreReader {
    //KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
    //Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
    // - Sertifikati koji ukljucuju javni kljuc
    // - Privatni kljucevi
    // - Tajni kljucevi, koji se koriste u simetricnima siframa
    private var keyStore: KeyStore? = null

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
    fun readIssuerFromStore(keyStoreFile: String, alias: String?, password: CharArray?, keyPass: CharArray?): IssuerData? {
        try {
            //Datoteka se ucitava
            val `in` = BufferedInputStream(FileInputStream(Paths.get(ResourceUtils.getFile("classpath:").toString() + "\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + keyStoreFile))
            keyStore!!.load(`in`, password)
            //Iscitava se sertifikat koji ima dati alias
            val cert = keyStore!!.getCertificate(alias)
            //Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
            val privKey = keyStore!!.getKey(alias, keyPass) as PrivateKey
            val issuerName = JcaX509CertificateHolder(cert as X509Certificate).subject
            return IssuerData(privKey, issuerName)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Ucitava sertifikat is KS fajla
     */
    fun readCertificate(keyStoreFile: String, keyStorePass: String, alias: String): Certificate? {
        try {
            println("Prosledjen keystore fajl $keyStoreFile")
            println("Prosledjen keyStorePass $keyStorePass")
            println("Prosledjen alias $alias")
            //kreiramo instancu KeyStore
            val ks = KeyStore.getInstance("JKS", "SUN")
            println("Posle ks get instance")
            //ucitavamo podatke
            val `in` = BufferedInputStream(FileInputStream(Paths.get(ResourceUtils.getFile("classpath:").toString() + "\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + keyStoreFile))
            println("Posle buffered reader")
            ks.load(`in`, keyStorePass.toCharArray())
            println("Posle ks load-a")
            println("Sta je ks " + ks.type)
            if (ks.isKeyEntry(alias)) {
                println("Udjes li u if?")
                val cert = ks.getCertificate(alias)
                println("Sta vracas iz key store reader-a?")
                return cert
            }
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Ucitava privatni kljuc is KS fajla
     */
    fun readPrivateKey(keyStoreFile: String, keyStorePass: String, alias: String?, pass: String): PrivateKey? {
        try {
            //kreiramo instancu KeyStore
            val ks = KeyStore.getInstance("JKS", "SUN")
            //ucitavamo podatke
            val `in` = BufferedInputStream(FileInputStream(Paths.get(ResourceUtils.getFile("classpath:").toString() + "\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + keyStoreFile))
            ks.load(`in`, keyStorePass.toCharArray())
            if (ks.isKeyEntry(alias)) {
                return ks.getKey(alias, pass.toCharArray()) as PrivateKey
            }
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        }
        return null
    }

    init {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN")
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
    }
}