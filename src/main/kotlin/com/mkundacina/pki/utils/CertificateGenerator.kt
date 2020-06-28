package com.mkundacina.pki.utils

import com.mkundacina.pki.data.IssuerData
import com.mkundacina.pki.data.SubjectData
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.X509KeyUsage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.Security
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

object CertificateGenerator {

    fun generateCertificate(subjectData: SubjectData, issuerData: IssuerData, isCA: Boolean): X509Certificate? {

        try {

            Security.addProvider(BouncyCastleProvider())

            val contentSigner = JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .setProvider("BC")
                    .build(issuerData.privateKey)

            // https://access.redhat.com/documentation/en-us/red_hat_certificate_system/9/html/administration_guide/standard_x.509_v3_certificate_extensions
            val certGen: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(
                    issuerData.x500name,
                    BigInteger(subjectData.serialNumber),
                    subjectData.startDate,
                    subjectData.endDate,
                    subjectData.x500name,
                    subjectData.publicKey
            ).addExtension(
                    Extension.basicConstraints,
                    true,
                    BasicConstraints(isCA)
            )

            return JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certGen.build(contentSigner))

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