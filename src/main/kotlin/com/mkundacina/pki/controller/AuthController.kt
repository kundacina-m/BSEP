package com.mkundacina.pki.controller

import com.mkundacina.pki.model.entities.User
import com.mkundacina.pki.model.requests.LoginRequest
import com.mkundacina.pki.model.requests.RegisterRequest
import com.mkundacina.pki.security.TokenUtils
import com.mkundacina.pki.security.UserPrincipal
import com.mkundacina.pki.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.scheduling.annotation.Async
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping(value = ["/auth"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthController {

    @Autowired
    lateinit var tokenUtils: TokenUtils

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var mailSender: MailSender

    @Autowired
    lateinit var environment: Environment

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest,
              response: HttpServletResponse?): ResponseEntity<*> {

        val authentication = authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken(loginRequest.username,
                        loginRequest.password))

        // Ubaci username + password u kontext
        SecurityContextHolder.getContext().authentication = authentication

        // Kreiraj token
        val user: User = authentication.principal as User
        val jwt: String = tokenUtils.generateToken(user.username)

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok<String>(jwt)
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest, ucBuilder: UriComponentsBuilder): ResponseEntity<*> {
        val existUser = userService.findByUsername(registerRequest.username)
        if (existUser != null) throw RuntimeException("Username already exists")

        sendMail(userService.save(registerRequest), tokenUtils.generateToken(registerRequest.username))
        return ResponseEntity<UserPrincipal>(HttpStatus.CREATED)
    }

    @GetMapping("/activate/{token}")
    fun activateUser(@PathVariable token: String) {
        val username = tokenUtils.getUsernameFromToken(token)
        userService.enableUser(username!!)
    }

    @Async
    @Throws(MailException::class)
    fun sendMail(user: User, tokenValue: String) {
        val uri = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        val controller = "/auth/activate/$tokenValue"
        val url = uri + controller
        val mail = SimpleMailMessage()
        mail.setTo(user.email)
        mail.setFrom(environment.getProperty("spring.mail.username")!!)
        mail.setSubject("noreply")
        mail.setText("""You have recently made an account on our website.

Your credentials are: 
   username: ${user.username}

Please verify you account by clicking on the link: 
$url"""
        )
        mailSender.send(mail)
    }
}
