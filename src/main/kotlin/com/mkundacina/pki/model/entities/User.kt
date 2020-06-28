package com.mkundacina.pki.model.entities

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.function.Consumer
import javax.persistence.*

@Entity
data class User(

        @Column(nullable = false)
        val userName: String,
        @Column(nullable = false)
        val passWord: String,
        val email: String,
        var active: Boolean = false,
        private val roles: String,
        private val permissions: String
) : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0

    fun roleList(): List<String> = if (roles.isNotEmpty())
        listOf(*roles.split(",").toTypedArray()) else ArrayList()

    fun permissionList(): List<String> = if (permissions.isNotEmpty())
        listOf(*permissions.split(",").toTypedArray()) else ArrayList()

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()

        // Extract list of permissions (name)
        permissionList().forEach(Consumer { p: String? ->
            val authority: GrantedAuthority = SimpleGrantedAuthority(p)
            authorities.add(authority)
        })

        // Extract list of roles (ROLE_name)
        roleList().forEach(Consumer { r: String ->
            val authority: GrantedAuthority = SimpleGrantedAuthority("ROLE_$r")
            authorities.add(authority)
        })
        return authorities
    }

    override fun getPassword() = passWord
    override fun getUsername() = userName
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = active
}