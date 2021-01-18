package com.qverkk.orgella.zuul.security

import io.jsonwebtoken.Jwts
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val env: Environment
) : BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val cookies: Array<out Cookie>? = request.cookies
//        val cookie: Cookie? = cookies?.first { it.name == "UserInfo" }
        if (cookies == null) {
            chain.doFilter(request, response)
            return
        }
        val cookie = cookies.toList().stream().filter { it.name == "UserInfo" }.findFirst().orElseGet { null }
        if (cookie == null) {
            chain.doFilter(request, response)
            return
        }

        val authentication = getAuthentication(request)

        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
//        val cookie: Cookie = request.cookies?.first { it.name == "UserInfo" } ?: return null
        val cookie = request.cookies.toList().stream().filter { it.name == "UserInfo" }.findFirst().orElseGet { null }

        val jwtBody = Jwts.parser()
            .setSigningKey(env.getProperty("token.secret"))
            .parseClaimsJws(cookie.value)
            .body

        val userId = jwtBody.subject ?: return null

        val roles = (jwtBody["roles"] as String)
            .replace("[", "")
            .replace("]", "")
            .split(",")
            .stream()
            .map { SimpleGrantedAuthority(it) }
            .collect(Collectors.toList())

        return UsernamePasswordAuthenticationToken(userId, null, roles)
    }
}
