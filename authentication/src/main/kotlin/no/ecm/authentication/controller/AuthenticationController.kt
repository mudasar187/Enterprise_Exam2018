package no.ecm.authentication.controller

import no.ecm.authentication.service.AmqpService
import no.ecm.authentication.service.AuthenticationService
import no.ecm.utils.dto.auth.AuthenticationDto
import no.ecm.utils.dto.auth.RegistrationDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpSession

@RequestMapping(
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class AuthController(
        private val authService: AuthenticationService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val amqpService: AmqpService
) {

    @Value("\${adminCode}")
    private lateinit var adminCode: String


    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(map)
    }

    @PostMapping(path = ["/signup"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signUp(@RequestBody dto: RegistrationDto)
            : ResponseEntity<Void> {

        val userId : String = dto.userInfo!!.username!!
        val password : String = dto.password!!

        val registered = if(!dto.secretPassword.isNullOrBlank() && dto.secretPassword.equals(adminCode)) {
            authService.createUser(userId, password, setOf("ADMIN"))
        } else {
            authService.createUser(userId, password, setOf("USER"))
        }

        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        /**
         * AMQP
         */
        amqpService.send(dto.userInfo!!, "USER-REGISTRATION")

        return ResponseEntity.status(204).build()
    }

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthenticationDto)
            : ResponseEntity<Void> {

        val userId : String = dto.username!!
        val password : String = dto.password!!

        val userDetails = try{
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException){
            return ResponseEntity.status(400).build()
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).build()
    }

    @PostMapping(path = ["/logout"])
    fun logout(session: HttpSession): ResponseEntity<Void> {
        session.invalidate()
        return ResponseEntity.status(204).build()
    }

}