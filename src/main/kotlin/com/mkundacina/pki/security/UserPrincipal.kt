package com.mkundacina.pki.security

import com.mkundacina.pki.model.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.function.Consumer

class UserPrincipal(private val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()

        // Extract list of permissions (name)
        user.permissionList().forEach(Consumer { p: String? ->
            val authority: GrantedAuthority = SimpleGrantedAuthority(p)
            authorities.add(authority)
        })

        // Extract list of roles (ROLE_name)
        user.roleList().forEach(Consumer { r: String ->
            val authority: GrantedAuthority = SimpleGrantedAuthority("ROLE_$r")
            authorities.add(authority)
        })
        return authorities
    }

    override fun getPassword() = user.password
    override fun getUsername() = user.username
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = user.active

}