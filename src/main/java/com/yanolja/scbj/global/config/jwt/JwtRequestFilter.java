package com.yanolja.scbj.global.config.jwt;

import com.yanolja.scbj.global.config.jwt.exception.ExpiredTokenException;
import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String AUTHORIZATION_HEADER = "Authorization";


    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
        RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith(JwtUtil.GRANT_TYPE)) {
                jwt = authorizationHeader.substring(JwtUtil.GRANT_TYPE.length());
                username = jwtUtil.extractUsername(jwt);
            }

            if (jwt != null && username != null) {
                if (redisTemplate.opsForValue().get(JwtUtil.BLACK_LIST_PREFIX + jwt) != null) {
                    throw new ExpiredTokenException(ErrorCode.EXPIRED_TOKEN);
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext()
                        .setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        } catch (ExpiredJwtException | ExpiredTokenException ex) {
            sendErrorResponse(response, ErrorCode.EXPIRED_TOKEN.getHttpStatus().value(),
                ErrorCode.EXPIRED_TOKEN.getSimpleMessage());
            return;
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException |
                 SecurityException ex) {
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN.getHttpStatus().value(),
                ErrorCode.INVALID_TOKEN.getSimpleMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
        throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }
}
