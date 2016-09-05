package com.iotpot.server.security;

import com.iotpot.server.security.token.H2OPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by vinay on 1/28/16.
 */

public class IoTPotAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return ((H2OPrincipal) authentication.getPrincipal()).getName();
    }
}