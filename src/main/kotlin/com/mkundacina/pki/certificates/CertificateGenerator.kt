package com.mkundacina.pki.certificates

import data.IssuerData
import data.SubjectData
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.Security
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

object CertificateGenerator {

    fun generateCertificate(subjectData: SubjectData, issuerData: IssuerData): X509Certificate? {

        try {
            Security.addProvider(BouncyCastleProvider())
            var builder = JcaContentSignerBuilder("SHA256WithRSAEncryption")
            builder = builder.setProvider("BC")
            val contentSigner = builder.build(issuerData.privateKey)
            val certGen: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(issuerData.x500name,
                BigInteger(subjectData.serialNumber),
                subjectData.startDate,
                subjectData.endDate,
                subjectData.x500name,
                subjectData.publicKey)
            val certHolder = certGen.build(contentSigner)
            var certConverter = JcaX509CertificateConverter()
            certConverter = certConverter.setProvider("BC")
            return certConverter.getCertificate(certHolder)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: OperatorCreationException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        return null
    }
}