package com.mkundacina.pki.controller.certificates

import com.mkundacina.pki.model.entities.Certificate
import com.mkundacina.pki.model.requests.CSR
import com.mkundacina.pki.services.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/certificates/create")
class CreateCertificateController {

    @Autowired
    lateinit var certificateService: CertificateService

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("selfSigned")
    fun createSelfSignedCertificate(@RequestBody certificate: Certificate): ResponseEntity<*> =
            if (certificateService.createSelfSignedCertificateCA(certificate))
                ResponseEntity<Unit>(HttpStatus.CREATED)
            else ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("signed")
    fun createSignedCertificate(@RequestBody certificate: Certificate): ResponseEntity<*> =
            if (certificateService.createSignedCertificates(certificate))
                ResponseEntity<Unit>(HttpStatus.CREATED)
            else ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)


}