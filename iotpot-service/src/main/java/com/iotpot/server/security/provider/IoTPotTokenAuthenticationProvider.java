package com.iotpot.server.security.provider;

import com.iotpot.server.pojos.IoTPotTokenResponse;
import com.iotpot.server.security.token.IoTPotTokenPrincipal;
import com.iotpot.server.service.IoTPotTokenService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class IoTPotTokenAuthenticationProvider implements AuthenticationProvider {

  private final static Logger logger = Logger.getLogger(IoTPotTokenAuthenticationProvider.class);

  @Autowired
  private IoTPotTokenService tokenService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    IoTPotTokenPrincipal principal = (IoTPotTokenPrincipal)authentication.getPrincipal();
    logger.info("Called authenticated " + principal.toString());
    if (!principal.isValid()) {
      throw new BadCredentialsException("Invalid token");
    }
    IoTPotTokenResponse response = null;
    try {
      response = tokenService.contains(principal.getTenant().get(),
          principal.getRemoteAddr().get(),
          principal.getApplicationId().get(),
          principal.getToken().get(),
          principal.getTokenType()).getResponse();
    } catch (DatatypeConfigurationException e) {
      logger.log(Level.ERROR, "XMLGregorian Calendar conversion error.", e);
    }
    if (response != null) {
      List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
      if (response.getIoTPotRoles() != null) {

        response.getIoTPotRoles().forEach(e -> grantedAuthorityList.add(e));
      }
      return new PreAuthenticatedAuthenticationToken(principal, null, grantedAuthorityList);
    }
    throw new BadCredentialsException("Invalid token or token expired");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(PreAuthenticatedAuthenticationToken.class);
  }
}
