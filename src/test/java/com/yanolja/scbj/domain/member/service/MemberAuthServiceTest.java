package com.yanolja.scbj.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.helper.TestConstants;
import com.yanolja.scbj.global.config.CustomUserDetailsService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @InjectMocks
    private MemberAuthService memberAuthService;


    @Test
    @DisplayName("리프레쉬 토큰 재발급할 때")
    void refreshAccessToken() {
        RefreshRequest refreshRequest = RefreshRequest.builder()
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .build();


        given(jwtUtil.extractUsername(any())).willThrow(new ExpiredJwtException(null, new ClaimImpl("1"),null));
        given(jwtUtil.isRefreshTokenValid(any(), any())).willReturn(true);
        given(jwtUtil.generateToken(any())).willReturn(TestConstants.GRANT_TYPE.getValue());
        given(jwtUtil.generateRefreshToken(any())).willReturn(
            TestConstants.REFRESH_PREFIX.getValue());

        //when & then
        assertThat(tokenResponse).usingRecursiveComparison()
            .isEqualTo(memberAuthService.refreshAccessToken(refreshRequest));
    }

    class ClaimImpl implements Claims{

        private String username;
        public ClaimImpl(String username) {

        }
        @Override
        public String getIssuer() {
            return null;
        }

        @Override
        public Claims setIssuer(String iss) {
            return null;
        }

        @Override
        public String getSubject() {
            return username;
        }

        @Override
        public Claims setSubject(String sub) {
            return null;
        }

        @Override
        public String getAudience() {
            return null;
        }

        @Override
        public Claims setAudience(String aud) {
            return null;
        }

        @Override
        public Date getExpiration() {
            return null;
        }

        @Override
        public Claims setExpiration(Date exp) {
            return null;
        }

        @Override
        public Date getNotBefore() {
            return null;
        }

        @Override
        public Claims setNotBefore(Date nbf) {
            return null;
        }

        @Override
        public Date getIssuedAt() {
            return null;
        }

        @Override
        public Claims setIssuedAt(Date iat) {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public Claims setId(String jti) {
            return null;
        }

        @Override
        public <T> T get(String claimName, Class<T> requiredType) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Nullable
        @Override
        public Object put(String key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {

        }

        @Override
        public void clear() {

        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NotNull
        @Override
        public Collection<Object> values() {
            return null;
        }

        @NotNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }

        @Override
        public Object getOrDefault(Object key, Object defaultValue) {
            return Claims.super.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super Object> action) {
            Claims.super.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
            Claims.super.replaceAll(function);
        }

        @Nullable
        @Override
        public Object putIfAbsent(String key, Object value) {
            return Claims.super.putIfAbsent(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return Claims.super.remove(key, value);
        }

        @Override
        public boolean replace(String key, Object oldValue, Object newValue) {
            return Claims.super.replace(key, oldValue, newValue);
        }

        @Nullable
        @Override
        public Object replace(String key, Object value) {
            return Claims.super.replace(key, value);
        }

        @Override
        public Object computeIfAbsent(String key,
            @NotNull Function<? super String, ?> mappingFunction) {
            return Claims.super.computeIfAbsent(key, mappingFunction);
        }

        @Override
        public Object computeIfPresent(String key,
            @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
            return Claims.super.computeIfPresent(key, remappingFunction);
        }

        @Override
        public Object compute(String key,
            @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
            return Claims.super.compute(key, remappingFunction);
        }

        @Override
        public Object merge(String key, @NotNull Object value,
            @NotNull BiFunction<? super Object, ? super Object, ?> remappingFunction) {
            return Claims.super.merge(key, value, remappingFunction);
        }
    }
}