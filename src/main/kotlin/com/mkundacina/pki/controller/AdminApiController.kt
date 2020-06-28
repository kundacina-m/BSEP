package com.mkundacina.pki.controller

import com.mkundacina.pki.repository.UserRepository
import com.mkundacina.pki.model.entities.User
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("admin")
class AdminApiController(

// Since we used User Repository that extends JPA, in that case ORM escapes all special chars and SQL injection is not possible
// but in case that we wanted to do this manually with query and then executing it following could have happened
// String query = "SELECET * from users where username = " + username + ";"
// but if username is "nothing; drop table users" then with concatenation we would got 2 queries and table would be dropped
    private val userRepository: UserRepository) {

    // Only admin got access to this endpoint
    // Returns info for all registered members with their hashed passwords
    // https://localhost:8089/admin/getAllUsers
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("getAllUsers")
    fun getAllUsers(): List<User?> =
        userRepository.findAll()

    // Only admin got access to this endpoint
    // https://localhost:8089/admin/getUser/admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("getUser/{username}")
    fun getUser(@PathVariable username: String): User? {
        return userRepository.findByUserName(username)
    } // ^^^^^^^^^^^^^^^^^^^^^

}