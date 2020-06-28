package com.mkundacina.pki.security

import com.mkundacina.pki.security.auth.RestAuthenticationEntryPoint
import com.mkundacina.pki.security.auth.TokenAuthenticationFilter
import com.mkundacina.pki.services.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Autowired
    lateinit var tokenUtils: TokenUtils

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var restAuthenticationEntryPoint: RestAuthenticationEntryPoint

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<org.springframework.security.core.userdetails.UserDetailsService>(userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/certificates/**/**").hasAnyRole("ADMIN")
                .antMatchers("/api/rbac/test1").hasAuthority("ACCESS_TEST1")
                .antMatchers("/api/rbac/test2").hasAuthority("ACCESS_TEST2")
                .antMatchers("/api/validation/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().cors().and().csrf().disable()
                .addFilterBefore(TokenAuthenticationFilter(tokenUtils, userDetailsService), BasicAuthenticationFilter::class.java)
    }

//    @Bean
//    fun authenticationProvider(): DaoAuthenticationProvider {
//        val daoAuthenticationProvider = DaoAuthenticationProvider()
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())
//        daoAuthenticationProvider.setUserDetailsService(userPrincipalDetailsService)
//        return daoAuthenticationProvider
//    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

}