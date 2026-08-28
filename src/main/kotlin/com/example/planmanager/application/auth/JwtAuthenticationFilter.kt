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

        try {
            if (token != null) {
                // 💡 분기점: 토큰 검증 성공 vs 실패
                if (jwtProvider.validateToken(token)) {
                    val userId = jwtProvider.getUserIdFromToken(token)
                    request.setAttribute("userId", userId)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userId, null, listOf(SimpleGrantedAuthority("ROLE_USER"))
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                } else {
                    // 💡 핵심 교정: validateToken이 false를 반환하면(만료 등) 즉시 401 반환 후 종료
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "application/json;charset=UTF-8"
                    response.writer.write("""{"error": "Unauthorized", "message": "토큰이 만료되었거나 유효하지 않습니다."}""")
                    return
                }
            }
            // 토큰이 없는 정상적인 로그인/회원가입 요청 등은 필터 통과
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("""{"error": "Unauthorized", "message": "인증 처리 중 에러 발생"}""")
            return
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}