package com.yanolja.scbj.global.config;

import com.yanolja.scbj.global.config.jwt.JwtRequestFilter;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private static final String[] PERMIT_URL_ARRAY = {
        "/api/member/signup",
        "/api/member/signin",
        "/api/token/refresh",
        "/h2-console/**",
        "/**"
    };
    private final CorsConfig corsConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService customUserDetailsService;

    SecurityConfig(CorsConfig corsConfig, RedisTemplate<String, Object> redisTemplate,
        CustomUserDetailsService customUserDetailsService) {
        this.redisTemplate = redisTemplate;
        this.corsConfig = corsConfig;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(redisTemplate);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(PERMIT_URL_ARRAY)
                .permitAll()
                .anyRequest()
                .hasRole("USER"))
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilter(corsConfig.corsFilter())
            .addFilterBefore(new JwtRequestFilter(jwtUtil(), customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/favicon.ico", "/resources/**", "/error");
    }
}