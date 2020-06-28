package com.mkundacina.pki.data

import org.bouncycastle.asn1.x500.X500Name
import java.security.PrivateKey

data class IssuerData(
        val x500name: X500Name,
        val privateKey: PrivateKey
)