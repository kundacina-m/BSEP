package com.mkundacina.pki.controller.certificates

import com.mkundacina.pki.logger
import com.mkundacina.pki.model.entities.Certificate
import com.mkundacina.pki.services.CertificateService
import org.apache.logging.log4j.kotlin.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/certificates")
class RevocationCertificatesController {

    @Autowired
    lateinit var certificateService: CertificateService


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("isRevoked")
    fun checkIfCertificateIsRevoked(@RequestBody certificate: Certificate): ResponseEntity<*> =
        ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("revoke")
    fun revokeCertificate(@RequestBody certificate: Certificate): ResponseEntity<*> {
        val certFromDB = certificateService.certificateRepository.findBySerialNumberSubject(certificate.serialNumberSubject)
        if (certFromDB.isRevoked) {
            logger.get().warn("Trying to revoke already revoked certificate")
            return ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)
        }
        return if (certificateService.revokeCertificate(certificate)){
            logger.get().info("Revoked certificate ${certificate.serialNumberSubject}")
            ResponseEntity<Unit>(HttpStatus.OK)
        }

        else ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)
    }
}