package com.mkundacina.pki.model.requests

import com.mkundacina.pki.data.SubjectData
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import java.security.PublicKey
import java.util.*

data class CSR(

        // Name data
        val countryName: String = "Unknown",
        val organizationName: String = "Unknown",
        val emailAddress: String = "Unknown",

        // Subject data
        val serialNumber: String = "2141312412412",
        val startTimestamp: Long = 0L,
        val endTimestamp: Long = 0L,

        // Flags for choosing jks files and checking validity
        val issuerSerialNumber: String = "Unknown",
        val isCA: Boolean = false
)

fun CSR.toSubjectData(publicKey: PublicKey): SubjectData {
    val builder = X500NameBuilder(BCStyle.INSTANCE).apply {
        addRDN(BCStyle.C, countryName)
        addRDN(BCStyle.O, organizationName)
        addRDN(BCStyle.E, emailAddress)
    }

    return SubjectData(publicKey, builder.build(), serialNumber, Date(startTimestamp), Date(endTimestamp))
}