package com.mkundacina.pki.repository

import com.mkundacina.pki.model.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUserName(username: String): User?
}