/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.security.provider;

import com.google.common.base.Optional;
import com.iotpot.server.pojos.IoTPotRole;
import com.iotpot.server.security.token.IoTPotPrincipal;
import com.iotpot.server.service.AccountService;
import com.iotpot.server.pojos.Account;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

public class IoTPotUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    public static final String INVALID_BACKEND_ADMIN_CREDENTIALS = "Invalid Backend Admin Credentials";
    private final static Logger logger = Logger.getLogger(IoTPotUsernamePasswordAuthenticationProvider.class);
    @Autowired
    private AccountService accountService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        IoTPotPrincipal principal = (IoTPotPrincipal)authentication.getPrincipal();
        Optional remoteAddr = principal.getRemoteAddr();
        Optional<String> tenant = principal.getTenant();
        Optional<String> username = principal.getOptionalName();
        Optional<String> password = (Optional<String>) authentication.getCredentials();


        logger.info("tenant = " + tenant.toString() + " username " + username.toString());
        try {
            if (credentialsMissing(username, password) || !credentialsValid(tenant, username, password)) {
                throw new BadCredentialsException(INVALID_BACKEND_ADMIN_CREDENTIALS);
            }
        } catch (NoSuchAlgorithmException
            | InvalidKeyException
            | InvalidAlgorithmParameterException
            | NoSuchPaddingException
            | BadPaddingException
            | UnsupportedEncodingException
            | InvalidParameterSpecException
            | InvalidKeySpecException
            | IllegalBlockSizeException e) {

            logger.log(Level.ERROR, "Encryption error", e);
            e.printStackTrace();
            throw new AuthenticationServiceException("Encryption error", e);

        }

        Account account = accountService.getAccount(tenant.get(), username.get());
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        if (account.getIoTPotRoles() == null) {
            grantedAuthorityList.add(new IoTPotRole("user"));
        }
        else {

            account.getIoTPotRoles().forEach(e -> grantedAuthorityList.add(e));
        }
        Authentication auth
            = new UsernamePasswordAuthenticationToken(new IoTPotPrincipal(remoteAddr,
                principal.getApplicationId(),
                tenant,
                username),
                password,
                grantedAuthorityList);
        return auth;
    }

    private boolean credentialsMissing(Optional<String> username, Optional<String> password) {
        return !username.isPresent() || !password.isPresent();
    }

    private boolean credentialsValid(Optional<String> tenant, Optional<String> username, Optional<String> password)
        throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException, InvalidParameterSpecException, InvalidKeySpecException, IllegalBlockSizeException {
        return accountService.validateCredentials(tenant.get(), username.get(), password.get());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}