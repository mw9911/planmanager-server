package com.example.planmanager.application.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        // 💡 디버깅용 로그 추가: 어떤 요청이 들어왔고, 토큰이 잘 파싱되었는지 확인
        println("요청 URI: ${request.requestURI}")
        if (token == null) {
            println("🚨 에러: 앱에서 Authorization 헤더(토큰)를 보내지 않았습니다.")
        } else {
            if (jwtProvider.validateToken(token)) {
                println("✅ 토큰 검증 성공!")
                val userId = jwtProvider.getUserIdFromToken(token)
                request.setAttribute("userId", userId)

                val authentication = UsernamePasswordAuthenticationToken(
                    userId, null, listOf(SimpleGrantedAuthority("ROLE_USER"))
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } else {
                println("🚨 에러: 토 큰이 전달되었으나 서버가 검증(validateToken)을 실패했습니다. (만료, 키 불일치 등)")
            }
        }

        filterChain.doFilter(request, response)
    }


    /**
     * HTTP 헤더에서 'Bearer '로 시작하는 토큰 문자열 추출
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7) // "Bearer " 이후의 순수 토큰만 반환
        }
        return null
    }
}