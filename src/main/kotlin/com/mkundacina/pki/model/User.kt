package com.mkundacina.pki.model

import java.util.*
import javax.persistence.*

@Entity
data class User(

    @Column(nullable = false)
    val username: String,
    @Column(nullable = false)
    val password: String,
    private val roles: String,
    private val permissions: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0
    var active = 1

    fun roleList(): List<String> = if (roles.isNotEmpty())
        listOf(*roles.split(",").toTypedArray()) else ArrayList()

    fun permissionList(): List<String> = if (permissions.isNotEmpty())
        listOf(*permissions.split(",").toTypedArray()) else ArrayList()

}