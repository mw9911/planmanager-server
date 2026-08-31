package com.example.planmanager

import com.example.planmanager.application.auth.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) } // 💡 핵심: CORS 필터 적용
            .csrf { it.disable() } // REST API이므로 CSRF 비활성화
            .formLogin { it.disable() } // 기본 로그인 폼 비활성화
            .httpBasic { it.disable() } // 기본 HTTP Basic 인증 비활성화
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 미사용 (JWT 사용)
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                auth.requestMatchers("/api/v1/routines", "/api/v1/routines/**").authenticated()
                auth.requestMatchers("/api/v1/plans", "/api/v1/plans/**").authenticated()
                auth.requestMatchers("/api/v1/calendar", "/api/v1/calendar/**").authenticated()
                auth.anyRequest().permitAll()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // 💡 핵심: CORS 허용 정책 상세 설정
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // 1. 허용할 프론트엔드 도메인 (로컬 개발 환경 추가)
        configuration.allowedOrigins = listOf(
            "http://localhost:5173",
            "https://planmanager-api.duckdns.org",
            "https://plan-manager-web.vercel.app"
        )

        // 2. 허용할 HTTP 메서드
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")

        // 3. 모든 헤더 허용
        configuration.allowedHeaders = listOf("*")

        // 4. 쿠키 및 인증 정보 전송 허용 (withCredentials: true 환경에서 필수)
        configuration.allowCredentials = true

        // 5. 프론트엔드에서 읽을 수 있도록 응답 헤더 노출
        configuration.exposedHeaders = listOf("Set-Cookie", "Authorization")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}