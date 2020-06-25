package com.mkundacina.pki.controller.certificates

import com.mkundacina.pki.model.Certificate
import com.mkundacina.pki.services.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/certificates/create")
class CertificateController {

    @Autowired
    lateinit var certificateService: CertificateService

    // https://localhost:8089/certificates/createSelfSigned
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("selfSigned")
    fun createSelfSignedCertificate() : ResponseEntity<Certificate> {
        certificateService.createCertificate(generateCSR())
        return ResponseEntity(HttpStatus.CREATED)
    }

    // https://localhost:8089/certificates/createCA
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("CA")
    fun createCACertificate() : ResponseEntity<Certificate> {
        certificateService.createCertificate(generateCSR())
        return ResponseEntity(HttpStatus.CREATED)
    }

    // https://localhost:8089/certificates/createEndEntity
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("endEntity")
    fun createEndEntityCertificate() : ResponseEntity<Certificate> {
        certificateService.createCertificate(generateCSR())
        return ResponseEntity(HttpStatus.CREATED)
    }

    private fun generateCSR() =
        Certificate(
            serialNumberSubject = "asdas",
            serialNumberIssuer = "aaaa",
            city = "Novi Sad",
            commonName = "htec",
            email = "mkundacina",
            endDate = Date(),
            startDate = Date(),
            organization = "org"
        )
}