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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
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
            // UsernamePasswordAuthenticationFilter 전에 커스텀 JWT 필터 삽입
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * 회원가입 시 비밀번호 단방향 암호화를 위한 BCrypt 인코더 빈 등록
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}