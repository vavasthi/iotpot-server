/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.iotpot.server.common.caching.AccountCacheService;
import com.iotpot.server.common.caching.TenantCacheService;
import com.iotpot.server.common.enums.Role;
import com.iotpot.server.common.exception.BadRequestException;
import com.iotpot.server.common.exception.MismatchedCredentialHeaderAndAuthException;
import com.iotpot.server.pojos.*;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.security.token.IoTPotPrincipal;
import com.iotpot.server.security.token.IoTPotTokenPrincipal;
import com.iotpot.server.service.AccountService;
import com.iotpot.server.service.IoTPotTokenService;
import com.iotpot.server.util.IoTPotUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashSet;
import java.util.Set;

public class IoTPotAuthenticationFilter extends GenericFilterBean {

  public static final String TOKEN_SESSION_KEY = "token";
  public static final String USER_SESSION_KEY = "user";
  private final static Logger logger = Logger.getLogger(IoTPotAuthenticationFilter.class);
  private AccountService accountService = null;
  private AccountCacheService accountCacheService = null;
  private TenantCacheService tenantCacheService = null;
  private IoTPotTokenService tokenService = null;
  private AuthenticationManager authenticationManager;

  public IoTPotAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  /**
   * Entry point into the authentication filter. We check if the token and token cache is present, we do token based
   * authentication. Otherwise we assume it to be username and password based authentication.
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request,
                       ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = asHttp(request);
    HttpServletResponse httpResponse = asHttp(response);

    Optional<String> username = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_USERNAME_HEADER);
    Optional<String> password = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_PASSWORD_HEADER);
    Optional<String> tenant = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> basicAuth = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_AUTHORIZATION_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    if(!applicationId.isPresent()) {

      throw new MismatchedCredentialHeaderAndAuthException("Application Id is not provided as header for authentication.");
    }
    if (basicAuth.isPresent()) {

      String basicAuthValue = new String(Base64.decode(basicAuth.get().substring("Basic ".length()).getBytes()));
      String[] basicAuthPair = basicAuthValue.split(":", 2);
      if (username.isPresent() && password.isPresent()) {
        if (basicAuth.isPresent() && basicAuth.get() != null && !basicAuth.get().isEmpty()) {

          validateCredentialFields(username, password, basicAuthPair);
        }
      }
      else {
        username = Optional.fromNullable(basicAuthPair[0]);
        password = Optional.fromNullable(basicAuthPair[1]);
      }

    }
    boolean tokenBasedAuthentication = true;
    if (token.isPresent() &&
        tenant.isPresent() &&
        !tokenType.equals(IoTPotTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN)) {
      tokenBasedAuthentication = true;
    }
    else if (remoteAddr.isPresent() && tenant.isPresent() && username.isPresent() && password.isPresent()) {
      tokenBasedAuthentication = false;
    }
    else {
      throw new MismatchedCredentialHeaderAndAuthException("Insufficient headers for username/password as well token " +
          "based authentication");
    }
    String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
    String[] resourcePathArray = resourcePath.split("/", -1); // Split the url as many times as required.
    if (resourcePathArray.length > 2) {

      String urlTenant = resourcePathArray[2];
      if (tenant.isPresent()) {

        if (!urlTenant.equals(tenant.get()) && !tenant.get().equals(IoTPotConstants.IOTPOT_INTERNAL_TENANT)) {
          throw new MismatchedCredentialHeaderAndAuthException("Mismatched tenants in header and URL" + urlTenant + " and " + tenant);
        }
      }
      else {
        // The URL is not recognizable.
        throw new MismatchedCredentialHeaderAndAuthException(String.format("The URL is not in VERSION/NAME " + "format. {}", resourcePath));
      }
    }
    else {
      throw new MismatchedCredentialHeaderAndAuthException("Tenant header is absent.");
    }

    try {
      if (!tokenBasedAuthentication && postToCreateUser(httpRequest, tenant.get(), resourcePath)) {

        logger.log(Level.INFO,
                String.format("Trying to create user {} by X-Auth-Username method", username));
        processCreateUser(httpRequest,
                httpResponse,
                remoteAddr,
                applicationId,
                tenant,
                username,
                password);
        return;
      }
      if (!tokenBasedAuthentication && postToAuthenticate(httpRequest, tenant.get(), resourcePath)) {
        logger.log(Level.INFO,
            String.format("Trying to authenticate user {} by X-Auth-Username method", username));
        processUsernamePasswordAuthentication(httpRequest,
                httpResponse,
                remoteAddr,
                applicationId,
                tenant,
                username,
                password);
        return;
      }

      if (tokenBasedAuthentication) {
        logger.log(Level.INFO,
            String.format("Trying to authenticate user by X-Auth-Token method. Token: {}", token));
        IoTPotUsernameAndTokenResponse utResponse
            = getTokenService(httpRequest).
            contains(tenant.get(), remoteAddr.get(), applicationId.get(), token.get(), tokenType);
        IoTPotTokenResponse tokenResponse
            = utResponse.getResponse();
        if (tokenResponse != null) {

          processTokenAuthentication(remoteAddr, applicationId, tenant, utResponse.getUsername(), token, tokenType);
        }
      }

      logger.debug("AuthenticationFilter is passing request down the filter chain");
      chain.doFilter(request, response);
    } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
      SecurityContextHolder.clearContext();
      logger.error("Internal authentication service exception", internalAuthenticationServiceException);
      httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (AuthenticationException authenticationException) {
      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
    } catch (DatatypeConfigurationException e) {
      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    } catch(Exception e) {
      SecurityContextHolder.clearContext();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
    finally {
    }
  }

  private HttpServletRequest asHttp(ServletRequest request) {
    return (HttpServletRequest) request;
  }

  private HttpServletResponse asHttp(ServletResponse response) {
    return (HttpServletResponse) response;
  }

  private boolean postToAuthenticate(HttpServletRequest httpRequest, String tenant, String resourcePath) {
    String endPointURL = IoTPotConstants.V1_AUTHENTICATE_URL.replace(IoTPotConstants.TENANT_PARAMETER_PATTERN, tenant);
    return endPointURL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
  }
  private boolean postToCreateUser(HttpServletRequest httpRequest, String tenant, String resourcePath) {
    String endPointURL = IoTPotConstants.V1_ACCOUNTS_ENDPOINT.replace(IoTPotConstants.TENANT_PARAMETER_PATTERN, tenant);
    return endPointURL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
  }

  /**
   * This method gets called whent the user has tried to authenticate and has provided username and password
   * either in the form of X-Auth-Username and X-Auth-Password header or as a Basic Auth string. This method
   * creates a new token for the user and returns back to him.
   *
   * @param httpResponse http response
   * @param remoteAddr address of client
   * @param tenantDiscriminator tenantDiscriminator for which the authentication is being done
   * @param username username for which the authentication is being done
   * @param password password that is being used for authentication.
   * @throws IOException
   */
  private void processUsernamePasswordAuthentication(HttpServletRequest request,
                                                     HttpServletResponse httpResponse,
                                                     Optional<String> remoteAddr,
                                                     Optional<String> applicationId,
                                                     Optional<String> tenantDiscriminator,
                                                     Optional<String> username,
                                                     Optional<String> password) throws IOException, DatatypeConfigurationException {

    Tenant tenant = getTenantService(request).findByDiscriminator(tenantDiscriminator.get());
    Account account = getAccountCacheService(request).findByTenantAndNameOrEmail(tenant, username.get());
    if (account == null) {

      // Account not found for given tenantDiscriminator. Let's just bail out here.
      throw new BadCredentialsException(username.get() + " is not a valid user for tenant " + tenant.getDiscriminator());
    }
    Authentication resultOfAuthentication
        = tryToAuthenticateWithUsernameAndPassword(remoteAddr, applicationId, tenantDiscriminator, username, password);
    SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    httpResponse.setStatus(HttpServletResponse.SC_OK);
    IoTPotTokenResponse tokenResponse
        = getTokenService(request).assignAuthTokenToUser(tenant.getDiscriminator(),
        username.get(),
        remoteAddr.get(),
        applicationId.get()).getResponse();
    String tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
    httpResponse.addHeader("Content-Type", "application/json");
    httpResponse.getWriter().print(tokenJsonResponse);
  }

  private void processCreateUser(HttpServletRequest request,
                                 HttpServletResponse httpResponse,
                                 Optional<String> remoteAddr,
                                 Optional<String> applicationId,
                                 Optional<String> tenantDiscriminator,
                                 Optional<String> username,
                                 Optional<String> password)
          throws IOException,
          DatatypeConfigurationException,
          NoSuchPaddingException,
          NoSuchAlgorithmException,
          IllegalBlockSizeException,
          BadPaddingException,
          InvalidParameterSpecException,
          InvalidKeyException,
          InvalidKeySpecException {

    Tenant tenant = getTenantService(request).findByDiscriminator(tenantDiscriminator.get());
    String body = getBody(request);
    ObjectMapper objectMapper = new ObjectMapper();
    Account account = objectMapper.readValue(body.getBytes(), Account.class);
    if (account == null
            || !account.getName().equals(username.get())
            || !account.getPassword().equals(password.get())
            || (account.getIoTPotRoles() != null && account.getIoTPotRoles().size() > 0)) {

      // Account not found for given tenantDiscriminator. Let's just bail out here.
      throw new BadRequestException(String.format("%s is not a valid request", body));
    }
    Set<IoTPotRole> userRole = new HashSet<>();
    userRole.add(new IoTPotRole(Role.USER.getValue()));
    account.setIoTPotRoles(userRole);
    account = getAccountService(request).createAccount(tenant, account);
    String tokenJsonResponse = new ObjectMapper().writeValueAsString(account);
    httpResponse.addHeader("Content-Type", "application/json");
    httpResponse.getWriter().print(tokenJsonResponse);
  }

  private Authentication tryToAuthenticateWithUsernameAndPassword(Optional<String> remoteAddr,
                                                                  Optional<String> applicationId,
                                                                  Optional<String> tenant,
                                                                  Optional<String> username,
                                                                  Optional<String> password) {
    UsernamePasswordAuthenticationToken requestAuthentication
        = new UsernamePasswordAuthenticationToken(new IoTPotPrincipal(remoteAddr, applicationId, tenant, username), password);
    return tryToAuthenticate(requestAuthentication);
  }

  private void processTokenAuthentication(Optional<String> remoteAddr,
                                          Optional<String> applicationId,
                                          Optional<String> tenant,
                                          String username,
                                          Optional<String> token,
                                          IoTPotTokenPrincipal.TOKEN_TYPE tokenType) {

    Authentication resultOfAuthentication = tryToAuthenticateWithToken(remoteAddr,
            applicationId,
        tenant,
        username,
        token,
        tokenType);
    SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
  }

  private Authentication tryToAuthenticateWithToken(Optional<String> remoteAddr,
                                                    Optional<String> applicationId,
                                                    Optional<String> tenant,
                                                    String username,
                                                    Optional<String> token,
                                                    IoTPotTokenPrincipal.TOKEN_TYPE tokenType) {
    PreAuthenticatedAuthenticationToken requestAuthentication
        = new PreAuthenticatedAuthenticationToken(new IoTPotTokenPrincipal(remoteAddr,
            applicationId,
        tenant,
        username,
        token,
        tokenType), null);
    return tryToAuthenticate(requestAuthentication);
  }

  private Authentication tryToAuthenticate(Authentication requestAuthentication) {
    Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
    if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
      throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
    }
    logger.debug("User successfully authenticated");
    return responseAuthentication;
  }
  private void validateCredentialFields(Optional<String> field1, Optional<String> field2, String[] basicAuthPair)
      throws MismatchedCredentialHeaderAndAuthException {

    validateCredentialField(field1, basicAuthPair[0]);
    validateCredentialField(field2, basicAuthPair[1]);
  }
  private void validateCredentialField(Optional<String> field, String value)
      throws MismatchedCredentialHeaderAndAuthException {

    if (!field.isPresent()) {
      field = Optional.fromNullable(value);
    }
    else {
      if (!field.get().equals(value)) {

        logger.info(" USERNAME" + field.get() + " VALUE " + value);
        throw new MismatchedCredentialHeaderAndAuthException("Mismatched credential value and header");
      }
    }
  }
  private AccountCacheService getAccountCacheService(HttpServletRequest request) {

    synchronized (this) {

      if (accountCacheService == null) {

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        accountCacheService =  webApplicationContext.getBean(AccountCacheService.class);
      }
    }
    return accountCacheService;
  }
  private AccountService getAccountService(HttpServletRequest request) {

    synchronized (this) {

      if (accountService == null) {

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        accountService =  webApplicationContext.getBean(AccountService.class);
      }
    }
    return accountService;
  }

  private TenantCacheService getTenantService(HttpServletRequest request) {

    synchronized (this) {

      if (tenantCacheService == null) {

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        tenantCacheService =  webApplicationContext.getBean(TenantCacheService.class);
      }
    }
    return tenantCacheService;
  }
  private IoTPotTokenService getTokenService(HttpServletRequest request) {

    synchronized (this) {

      if (tokenService == null) {

        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        tokenService =  webApplicationContext.getBean(IoTPotTokenService.class);
      }
    }
    return tokenService;
  }
  private Optional<String>
  getOptionalHeader(HttpServletRequest httpRequest, String headerName) {
    return Optional.fromNullable(httpRequest.getHeader(headerName));
  }
  private static String getBody(HttpServletRequest request) throws IOException {

    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try (InputStream inputStream = request.getInputStream()) {

      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[1024];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    }

    body = stringBuilder.toString();
    return body;
  }
}