package no.ecm.gateway.controller

import io.swagger.annotations.Api
import no.ecm.gateway.service.AuthService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Api(value = "/", description = "API for authentication")
@RequestMapping(
        path = ["/"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class AuthController(
        private val authService: AuthService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService
) {


    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<Map<String, Any>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return ResponseEntity.ok(map)
    }

}