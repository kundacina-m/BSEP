package com.mkundacina.pki.certificates

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory

object CertificateReader {

    fun readFromBase64EncFile(filepath: String): Certificate? {
        try {
            val fis = FileInputStream(filepath)
            val bis = BufferedInputStream(fis)
            val cf = CertificateFactory.getInstance("X.509")

            //Cita sertifikat po sertifikat
            //Svaki certifikat je izmedju
            //-----BEGIN CERTIFICATE-----,
            //i
            //-----END CERTIFICATE-----.
            if (bis.available() > 0) {
                return cf.generateCertificate(bis)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}