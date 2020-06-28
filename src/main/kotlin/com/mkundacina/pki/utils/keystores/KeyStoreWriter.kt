package com.mkundacina.pki.utils.keystores

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.CertificateException

object KeyStoreWriter {

    //KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
    //Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
    // - Sertifikati koji ukljucuju javni kljuc
    // - Privatni kljucevi
    // - Tajni kljucevi, koji se koriste u simetricnima siframa
    val keyStorePassword = "password".toCharArray()

    init {
        KeyStore.getInstance("JKS", "SUN").run {
            load(null, keyStorePassword)
            store(FileOutputStream("cert_authority.jks"), keyStorePassword)
            store(FileOutputStream("end_entity.jks"), keyStorePassword)
        }
    }

    val keyStore: KeyStore = KeyStore.getInstance("JKS", "SUN")


    fun loadKeyStore(fileName: String?, password: CharArray) {
        try {
            if (fileName != null) {
                println("File name $fileName password $password")
                keyStore.load(FileInputStream(fileName), password)
            } else {
                keyStore.load(null, password)
            }
        } catch (e: NoSuchFileException) {
            keyStore.load(null, password)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveKeyStore(fileName: String, password: CharArray) {
        try {
            keyStore.store(FileOutputStream(fileName), password)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun write(alias: String, privateKey: PrivateKey, keyPassword: CharArray, certificate: Array<Certificate>) {
        keyStore.setKeyEntry(alias, privateKey, keyPassword, certificate)
    }

    fun getChain(alias: String?): Array<Certificate?>? {
        try {
            return keyStore.getCertificateChain(alias)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
        return null // bacio eksepsn
    }
}