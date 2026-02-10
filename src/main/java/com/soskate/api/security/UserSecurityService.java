package com.soskate.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service("userSecurity")
public class UserSecurityService {

    public boolean isOwner(Long userId) {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long customUserId = customUserDetails.getUserEntity().getId();
        return Objects.equals(customUserId, userId);
    }

    public boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
