/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.endpoints.v1;

import com.google.common.base.Optional;
import com.iotpot.server.common.caching.AccountCacheService;
import com.iotpot.server.common.caching.DeviceRegistrationTempAuthTokenCacheService;
import com.iotpot.server.pojos.*;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.common.exception.MismatchedCredentialHeaderAndAuthException;
import com.iotpot.server.common.caching.DeviceCacheService;
import com.iotpot.server.common.caching.TenantCacheService;
import com.iotpot.server.security.token.IoTPotTokenPrincipal;
import com.iotpot.server.service.IoTPotTokenService;
import com.iotpot.server.util.IoTPotUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Date;
import java.util.UUID;

@RestController
public class AuthenticationEndpoint extends BaseEndpoint {

  @Autowired
  private HttpServletRequest request;
  @Autowired
  private HttpServletResponse response;
  @Autowired
  private IoTPotTokenService tokenService;
  @Autowired
  private DeviceRegistrationTempAuthTokenCacheService deviceRegistrationTempAuthTokenCacheService;
  @Autowired
  private TenantCacheService tenantCacheService;
  @Autowired
  private DeviceCacheService deviceCacheService;
  @Autowired
  private AccountCacheService accountCacheService;

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL, method = RequestMethod.POST)
  public String authenticate(@PathVariable("tenant") String tenant) {
    return "This is just for in-code-documentation purposes and Rest API reference documentation." +
        "Servlet will never get to this point as Http requests are processed by AuthenticationFilter." +
        "Nonetheless to authenticate Domain User POST request with X-Auth-Username and X-Auth-Password headers " +
        "is mandatory to this URL. If username and password are correct valid token will be returned (just json string in response) " +
        "This token must be present in X-Auth-Token header in all requests for all other URLs, including logout." +
        "Authentication can be issued multiple times and each call results in new ticket.";
  }

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/refresh", method = RequestMethod.POST)
  public IoTPotTokenResponse refresh(@PathVariable("tenant") String tenant) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    try {

      IoTPotUsernameAndTokenResponse utResponse
          = tokenService.refresh(tenantHeader.get(), remoteAddr.get(), applicationId.get(), token.get(), tokenType);
      IoTPotTokenResponse tokenResponse
          = utResponse.getResponse();

      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }
  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/validate", method = RequestMethod.POST)
  public IoTPotTokenResponse validate(@PathVariable("tenant") String tenant) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    try {

      IoTPotUsernameAndTokenResponse utResponse
          = tokenService.contains(tenantHeader.get(), remoteAddr.get(), applicationId.get(), token.get(), tokenType);
      IoTPotTokenResponse tokenResponse
          = utResponse.getResponse();

      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/device", method = RequestMethod.POST)
  public IoTPotTokenResponse createDeviceAuthToken(@PathVariable("tenant") String tenant,
                                                   @RequestBody @Valid DeviceAuthentication deviceAuthentication) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    try {

      if (tokenType.equals(IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN)) {

        DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken
        = deviceRegistrationTempAuthTokenCacheService.findByTempAuthToken(token.get());
        deviceRegistrationTempAuthTokenCacheService.delete(deviceRegistrationTempAuthToken);
      }
      IoTPotUsernameAndTokenResponse utResponse
          = tokenService.assignAuthTokenToDevice(tenant,
          deviceAuthentication.getDeviceRegistrationId(),
          remoteAddr.get(),
          applicationId.get());
      IoTPotTokenResponse tokenResponse = utResponse.getResponse();
      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/tempAuth", method = RequestMethod.POST)
  public IoTPotTokenResponse createTempAuthToken(@PathVariable("tenant") String tenant,
                                                 @RequestBody @Valid DeviceAuthentication deviceAuthentication) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    try {

      Tenant t = tenantCacheService.findByDiscriminator(tenant);
      Account account = accountCacheService.findOne(deviceAuthentication.getAccountId());
      if (!account.getTenant().getId().equals(t.getId())) {
        throw new BadCredentialsException(String.format("Account %s does not belong to tenant %s",
                deviceAuthentication.getDeviceRegistrationId(), t.getDiscriminator()));
      }
      Appliance appliance = deviceCacheService.findByDeviceRegistrationId(deviceAuthentication.getDeviceRegistrationId());
      DeviceRegistrationTempAuthToken tempAuthToken
          = deviceRegistrationTempAuthTokenCacheService.create(account, appliance);
      IoTPotUsernameAndTokenResponse utResponse
          = new IoTPotUsernameAndTokenResponse(t.getDiscriminator(),
          account.getName(),
          new IoTPotTokenResponse(tempAuthToken.getTempAuthToken(),
              IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN,
              account.getDataCenter(),
                  new DateTime(new Date().getTime() + IoTPotConstants.HALF_HOUR * 1000), account.getIoTPotRoles()));
      IoTPotTokenResponse tokenResponse = utResponse.getResponse();
      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/{id}", method = RequestMethod.DELETE)
  public IoTPotTokenResponse deleteToken(@PathVariable("tenant") String tenant,
                                         @PathVariable("id") UUID id) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    Optional<String> token = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_HEADER);
    Optional<String> tokenTypeStr = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TOKEN_TYPE_HEADER);
    Optional<String> remoteAddr = Optional.fromNullable(httpRequest.getRemoteAddr());
    Optional<String> applicationId = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_APPLICATION_ID_HEADER);
    IoTPotTokenPrincipal.TOKEN_TYPE tokenType = IoTPotUtils.getTokenType(tokenTypeStr);
    try {

      IoTPotUsernameAndTokenResponse utResponse
          = tokenService.deleteToken(tenantHeader.get(), id, token.get(), tokenType);
      IoTPotTokenResponse tokenResponse
          = utResponse.getResponse();

      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }

  @Transactional
  @RequestMapping(value = IoTPotConstants.V1_AUTHENTICATE_URL + "/{id}/{token}", method = RequestMethod.DELETE)
  public IoTPotTokenResponse deleteToken(@PathVariable("tenant") String tenant,
                                         @PathVariable("id") UUID id,
                                         @PathVariable("token") String token) {

    HttpServletRequest httpRequest = asHttp(request);
    Optional<String> tenantHeader = getOptionalHeader(httpRequest, IoTPotConstants.AUTH_TENANT_HEADER);
    try {

      IoTPotUsernameAndTokenResponse utResponse
          = tokenService.deleteToken(tenantHeader.get(), token);
      IoTPotTokenResponse tokenResponse
          = utResponse.getResponse();

      return tokenResponse;
    }
    catch(DatatypeConfigurationException ex) {
      throw new MismatchedCredentialHeaderAndAuthException("Datatype configuration error.");
    }
  }
  private HttpServletRequest asHttp(ServletRequest request) {
    return (HttpServletRequest) request;
  }

  private HttpServletResponse asHttp(ServletResponse response) {
    return (HttpServletResponse) response;
  }

  private Optional<String>
  getOptionalHeader(HttpServletRequest httpRequest, String headerName) {
    return Optional.fromNullable(httpRequest.getHeader(headerName));
  }

}
