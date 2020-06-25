package com.mkundacina.pki.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/rbac")
class RBACApiController {
    // admin got access fot this endpoint
    // https://localhost:8089/api/rbac/test1
    @GetMapping("test1")
    fun adminEndpoint(): String {
        return "Admin got access to this endpoint"
    }

    // admin and manager got access for this endpoint
    // https://localhost:8089/api/rbac/test2
    @GetMapping("test2")
    fun adminAndManagerEndpoint(): String {
        return "Admin and manager got access for this endpoint"
    }

    // access for everyone, even for anons
    // https://localhost:8089/api/rbac/public
    @GetMapping("public")
    fun publicEndpoint(): String {
        return "Everyone is welcome here!"
    }
}