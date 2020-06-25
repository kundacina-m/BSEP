package com.mkundacina.pki.keystores

import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException

object KeyStoreWriter {

    //KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
    //Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
    // - Sertifikati koji ukljucuju javni kljuc
    // - Privatni kljucevi
    // - Tajni kljucevi, koji se koriste u simetricnima siframa

    lateinit var keyStore: KeyStore

    init {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN")
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
    }

    fun loadKeyStore(fileName: String?, password: CharArray) {
        try {
            if (fileName != null) {
                println("File name $fileName password $password")
                keyStore.load(FileInputStream(Paths.get(ResourceUtils.getFile("classpath:").toString() + "\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + fileName), password)
            } else {
                keyStore.load(null, password)
            }
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

    fun saveKeyStore(fileName: String, password: CharArray?) {
        try {
            keyStore.store(FileOutputStream(Paths.get(ResourceUtils.getFile("classpath:").toString() + "\\..\\..\\src\\main\\resources").toRealPath().toString() + "\\" + fileName), password)
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

    fun write(alias: String?, privateKey: PrivateKey?, password: CharArray?, certificate: Array<Certificate?>?) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, certificate)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }
    }
}