package com.mkundacina.pki.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("api/validation")
class ValidationApiController {
    // admin got access to this endpoint
    // Input validation regarding id parameter that us type Long, should return response code 400 for bad request
    // https://localhost:8089/api/validation/type/123124
    @GetMapping("type/{id}")
    fun typeValidation(@PathVariable id: Long): String {
        return "Parameter type correct and id = $id"
    }

    // Manual input validation regarding length of parameter
    // https://localhost:8089/api/validation/complexArg/123124
    @GetMapping("complexArg/{limitedWord}")
    @Throws(Exception::class)
    fun complexValidation(@PathVariable limitedWord: String): String {
        return if (limitedWord.length > 10) "Too long argument" else "Parameter is not null or blank, also length is =< 10 chars. Parameter = $limitedWord"
    } // TODO: JPA mapping for @Entity check with javax.validation.constraints
}