package com.iotpot.server.pojos;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class IoTPotAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -1092219614309982278L;
    private final Object principal;
    private Object credentials;

    public IoTPotAuthenticationToken(Object principal, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }
}