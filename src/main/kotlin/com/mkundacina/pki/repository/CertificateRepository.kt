package com.mkundacina.pki.repository

import com.mkundacina.pki.model.entities.Certificate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CertificateRepository : JpaRepository<Certificate,Long> {
    fun findBySerialNumberSubject(serial: String) : Certificate
    fun findByIsCATrue() : List<Certificate>
}