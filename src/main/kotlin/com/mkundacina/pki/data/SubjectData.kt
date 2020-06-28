package com.mkundacina.pki.data

import org.bouncycastle.asn1.x500.X500Name
import java.security.PublicKey
import java.util.*

data class SubjectData(
        val publicKey: PublicKey,
        val x500name: X500Name,
        val serialNumber: String,
        val startDate: Date,
        val endDate: Date
)