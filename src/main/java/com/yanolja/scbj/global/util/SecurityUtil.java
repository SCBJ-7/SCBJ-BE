package com.yanolja.scbj.global.util;

import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.exception.ForbbidenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (isUserNotAuthenticated()) {
            throw new ForbbidenException(ErrorCode.AUTH_FORBIDDEN);
        }

        return Long.parseLong(authentication.getName());
    }

    public boolean isUserNotAuthenticated(){
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        return authentication == null || !authentication.isAuthenticated()
            || authentication.getName().equals("anonymousUser");
    }

}
