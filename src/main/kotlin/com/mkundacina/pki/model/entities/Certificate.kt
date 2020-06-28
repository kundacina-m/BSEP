package com.mkundacina.pki.model.entities

import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
data class Certificate(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "serialNumberSubject", nullable = false, unique = true)
    val serialNumberSubject: String,

    @Column(name = "serialNumberIssuer", nullable = false)
    val serialNumberIssuer: String,


    @Column(name = "organization", nullable = false, unique = true)
    val organization: String,

    @Column(name = "country", nullable = false)
    val country: String,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "startDate", nullable = false)
    val startDate: String,

    @Column(name = "endDate", nullable = false)
    val endDate: String,

    @Column(name = "ca", nullable = false)
    val isCA: Boolean = true,

    @Column(name = "revoked", nullable = false)
    var isRevoked: Boolean = false,

    @Transient
    val keyUsage: ArrayList<Int> = arrayListOf(),

    @Transient
    val extendedKeyUsage: ArrayList<String> = arrayListOf(),

    @Column(name = "revocationReason", nullable = true)
    var revocationReason : String? = null,

    @Column(name = "revocationTimestamp", nullable = true)
    var revocationTimestamp : String? = null
)