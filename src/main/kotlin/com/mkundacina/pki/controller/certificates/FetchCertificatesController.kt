package com.mkundacina.pki.controller.certificates

import com.mkundacina.pki.model.entities.Certificate
import com.mkundacina.pki.services.CertificateService
import org.omg.SendingContext.RunTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException

@RestController
@RequestMapping("/certificates/fetch")
class FetchCertificatesController {

    @Autowired
    lateinit var certificateService: CertificateService

    // https://localhost:8089/certificates/fetch/all
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all")
    fun getAllCertificates(): ResponseEntity<List<Certificate>> {
        return ResponseEntity(certificateService.certificateRepository.findAll(), HttpStatus.OK)
    }

    // https://localhost:8089/certificates/fetch/serialValue
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{serial}")
    fun getCertificateBySerial(@PathVariable serial: String): ResponseEntity<Certificate> {
        return ResponseEntity(certificateService.findCertificateBySerial(serial), HttpStatus.OK)
    }
}