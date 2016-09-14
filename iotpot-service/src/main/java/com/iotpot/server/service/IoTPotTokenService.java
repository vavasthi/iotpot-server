package com.iotpot.server.service;

import com.iotpot.server.common.caching.AccountCacheService;
import com.iotpot.server.common.caching.DeviceCacheService;
import com.iotpot.server.common.caching.DeviceRegistrationTempAuthTokenCacheService;
import com.iotpot.server.common.caching.TenantCacheService;
import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.mapper.TenantMapper;
import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.common.exception.TokenExpiredException;
import com.iotpot.server.common.exception.UnauthorizedException;
import com.iotpot.server.pojos.*;
import com.iotpot.server.mapper.AccountMapper;
import com.iotpot.server.security.token.IoTPotTokenPrincipal;
import com.iotpot.server.pojos.IoTPotRole;
import org.eclipse.jetty.http.HttpStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IoTPotTokenService {

  @Autowired
  private AccountDao accountDao;
  @Autowired
  private TenantDao tenantDao;
  @Autowired
  private SessionDao sessionDao;
  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private TenantMapper tenantMapper;
  @Autowired
  private AccountMapper accountMapper;
  @Autowired
  private DeviceMapper deviceMapper;
  @Autowired
  private AccountCacheService accountCacheService;
  @Autowired
  private TenantCacheService tenantCacheService;
  @Autowired
  private DeviceRegistrationTempAuthTokenCacheService deviceRegistrationTempAuthTokenCacheService;
  @Autowired
  private DeviceCacheService deviceCacheService;
  /**
   * This method is called by the authentication filter when token based authentication is performed. This method
   * checks the token cache to find the appropriate token and validate the ip address from which the request had come
   * from. If the ip address could not be validated, the the user is asked to authenticate using username and password.
   *
   * If the token doesn't exist in the cache but is present in the database, then it is populated in the cache.
   *
   * @param tenantDiscriminator discriminator for the tenant
   * @param remoteAddr the ip address from which the incoming request came
   * @param authToken the auth token that needs to be verified.
   * @return token response object.
   * @throws DatatypeConfigurationException
   */
  public IoTPotUsernameAndTokenResponse contains(String tenantDiscriminator,
                                                 String remoteAddr,
                                                 String applicationId,
                                                 String authToken,
                                                 IoTPotTokenPrincipal.TOKEN_TYPE tokenType)
          throws DatatypeConfigurationException {

    if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN) {
      return validateAppToken(tenantDiscriminator, remoteAddr, applicationId, authToken);
    }
    else if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN) {
      return validateTempToken(tenantDiscriminator, remoteAddr, applicationId, authToken);
    }
    else if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.DEVICE_TOKEN) {
      return validateDeviceToken(tenantDiscriminator, remoteAddr, applicationId, authToken);
    }
    throw new BadClientCredentialsException();
  }

  /**
   * This method is called when a token needs to be refreshed. The request is validated against the token that
   * is sent as part of the request, a new token is generated and returned.
   *
   *
   * @param tenantDiscriminator discriminator for the tenant
   * @param remoteAddr the ip address from which the incoming request came
   * @param authToken the auth token that needs to be verified.
   * @return token response object.
   * @throws DatatypeConfigurationException
   */
  public IoTPotUsernameAndTokenResponse refresh(String tenantDiscriminator,
                                                String remoteAddr,
                                                String applicationId,
                                                String authToken,
                                                IoTPotTokenPrincipal.TOKEN_TYPE tokenType)
          throws DatatypeConfigurationException {

    if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN) {
      return refreshAppToken(tenantDiscriminator, remoteAddr, applicationId, authToken);
    }
    else if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.DEVICE_TOKEN) {
      return refreshDeviceToken(tenantDiscriminator, remoteAddr, applicationId, authToken);
    }
    throw new BadClientCredentialsException();
  }

  private IoTPotUsernameAndTokenResponse validateAppToken(String tenantDiscriminator,
                                                          String remoteAddr,
                                                          String applicationId,
                                                          String authToken) throws DatatypeConfigurationException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
    Account account = accountCacheService.findByAuthToken(tenant, authToken);
    if (account != null) {

      Session session = account.getSessionMap().get(applicationId);
      if (session == null) {
        throw new UnauthorizedException(String.format("Token %s doesn't belong to application %s", authToken, applicationId));
      }
      if (account.getSessionMap().get(applicationId).getExpiry().isBefore(new DateTime())) {
        throw new TokenExpiredException(HttpStatus.UNAUTHORIZED_401, authToken + " is expired.");
      }
      Set<String> remoteAddresses = account.getRemoteAddresses().stream().collect(Collectors.toSet());
      if (!remoteAddresses.contains(remoteAddr)) {

        throw new UnauthorizedException("Unknown IP address, please reauthenticate." + remoteAddr);
      }
      return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
              account.getName(),
              new IoTPotTokenResponse(authToken,
                      IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN,
                      account.getDataCenter(),
                      account.getSessionMap().get(applicationId).getExpiry(),
                      account.getIoTPotRoles()));
    }
    throw new BadClientCredentialsException();
  }
  private IoTPotUsernameAndTokenResponse validateTempToken(String tenantDiscriminator,
                                                           String remoteAddr,
                                                           String applicationId,
                                                           String authToken) throws DatatypeConfigurationException {

    DeviceRegistrationTempAuthToken tempAuthToken
            = deviceRegistrationTempAuthTokenCacheService.findByTempAuthToken(authToken);
    if (tempAuthToken != null) {

      if (tempAuthToken.getExpiry().isBefore(new DateTime())) {
        throw new TokenExpiredException(HttpStatus.UNAUTHORIZED_401, authToken + " is expired.");
      }

      return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
              tempAuthToken.getAccount().getName(),
              new IoTPotTokenResponse(authToken,
                      IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN,
                      tempAuthToken.getAccount().getDataCenter(),
                      tempAuthToken.getAccount().getSessionMap().get(applicationId).getExpiry(),
                      tempAuthToken.getAccount().getIoTPotRoles()));
    }
    throw new BadClientCredentialsException();
  }
  private IoTPotUsernameAndTokenResponse validateDeviceToken(String tenantDiscriminator,
                                                             String remoteAddr,
                                                             String applicationId,
                                                             String authToken) throws DatatypeConfigurationException {

    Appliance appliance = deviceCacheService.findByAuthToken(authToken);
    if (appliance != null) {

      if (appliance.getSession().getExpiry().isBefore(new DateTime())) {

        throw new TokenExpiredException(HttpStatus.UNAUTHORIZED_401, authToken + " is expired.");
      }
      Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
      Account account = accountCacheService.findOne(appliance.getAccountId());
      return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
              account.getName(),
              new IoTPotTokenResponse(authToken,
                      IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN,
                      account.getDataCenter(),
                      appliance.getSession().getExpiry(),
                      appliance.getIoTPotRoles()));
    }
    throw new BadClientCredentialsException();
  }
  private IoTPotUsernameAndTokenResponse refreshAppToken(String tenantDiscriminator,
                                                         String remoteAddr,
                                                         String applicationId,
                                                         String authToken) throws DatatypeConfigurationException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
    Account account = accountCacheService.findByAuthToken(tenant, authToken);
    if (account != null) {

      accountCacheService.evictFromCache(account);
      Session session = account.getSessionMap().get(applicationId);
      if (session == null) {
        throw new UnauthorizedException(String.format("Token %s doesn't belong to application %s", authToken, applicationId));
      }
      return assignAuthTokenToUser(tenantDiscriminator, account.getName(), remoteAddr, applicationId);
    }
    throw new BadClientCredentialsException();
  }
  private IoTPotUsernameAndTokenResponse refreshDeviceToken(String tenantDiscriminator,
                                                            String remoteAddr,
                                                            String applicationId,
                                                            String authToken) throws DatatypeConfigurationException {

    Appliance appliance = deviceCacheService.findByAuthToken(authToken);
    if (appliance != null) {
      deviceCacheService.evictFromCache(appliance);
      return assignAuthTokenToDevice(tenantDiscriminator, appliance.getDeviceRegistrationId(), remoteAddr, applicationId);
    }
    throw new BadClientCredentialsException();
  }
  /**
   * This method generates an auth token for user and returns it. This method is called when a user performs authentication
   * using username and pasword. Usual validation including the validation of the ip address is also performed.
   *
   * @param tenantDiscriminator
   * @param username
   * @param remoteAddr
   * @param applicationId
   * @return
   * @throws DatatypeConfigurationException
   */
  @Transactional
  public IoTPotUsernameAndTokenResponse assignAuthTokenToUser(String tenantDiscriminator,
                                                              String username,
                                                              String remoteAddr,
                                                              String applicationId)
          throws DatatypeConfigurationException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
    Account account = accountCacheService.findByTenantAndNameOrEmail(tenant, username);
    account = accountCacheService.generateAuthToken(tenant, account, remoteAddr, applicationId);
    return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
            username,
            new IoTPotTokenResponse(account.getSessionMap().get(applicationId).getAuthToken(),
                    IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN,
                    account.getDataCenter(),
                    account.getSessionMap().get(applicationId).getExpiry(),
                    account.getIoTPotRoles()));
  }
  public IoTPotUsernameAndTokenResponse assignAuthTokenToDevice(String tenantDiscriminator,
                                                                String deviceRegistrationId,
                                                                String remoteAddr,
                                                                String applicationId)
          throws DatatypeConfigurationException {

    Appliance appliance = deviceCacheService.findByDeviceRegistrationId(deviceRegistrationId);
    appliance = deviceCacheService.createSession(appliance, remoteAddr, applicationId);
    Account account = accountCacheService.findOne(appliance.getAccountId());

    return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
            account.getName(),
            new IoTPotTokenResponse(appliance.getSession().getAuthToken(),
                    IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN,
                    account.getDataCenter(),
                    appliance.getSession().getExpiry(),
                    account.getIoTPotRoles()));
  }

  @Transactional
  public IoTPotUsernameAndTokenResponse deleteToken(String tenantDiscriminator,
                                                    UUID id,
                                                    String token,
                                                    IoTPotTokenPrincipal.TOKEN_TYPE tokenType)
          throws DatatypeConfigurationException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
    if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.APP_TOKEN) {

      Account account = accountCacheService.deleteToken(tenant, id);
      return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
              account.getName(),
              new IoTPotTokenResponse("",
                      IoTPotTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN,
                      null,
                      new DateTime(),
                      account.getIoTPotRoles()));
    }
    else if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.DEVICE_TOKEN) {

      Appliance appliance = deviceCacheService.deleteToken(id);
      Account account = accountCacheService.findOne(appliance.getAccountId());
      return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
              account.getName(),
              new IoTPotTokenResponse("",
                      IoTPotTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN,
                      null,
                      new DateTime(),
                      new ArrayList<IoTPotRole>()));
    }
    else if (tokenType == IoTPotTokenPrincipal.TOKEN_TYPE.TEMP_TOKEN) {
      Appliance appliance = deviceCacheService.findOne(id);
      if (appliance != null) {

        DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken
                = deviceRegistrationTempAuthTokenCacheService.findByTempAuthToken(token);
        deviceRegistrationTempAuthTokenCacheService.delete(deviceRegistrationTempAuthToken);
        Account account = accountCacheService.findOne(appliance.getAccountId());
        return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
                account.getName(),
                new IoTPotTokenResponse("",
                        IoTPotTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN,
                        null,
                        new DateTime(),
                        new ArrayList<IoTPotRole>()));
      }
    }
    throw new BadClientCredentialsException();
  }

  public IoTPotUsernameAndTokenResponse deleteToken(String tenantDiscriminator,
                                                    String token)
          throws DatatypeConfigurationException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDiscriminator);
    Account account = accountCacheService.deleteToken(tenant, token);
    return new IoTPotUsernameAndTokenResponse(tenantDiscriminator,
            account.getName(),
            new IoTPotTokenResponse("",
                    IoTPotTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN,
                    null,
                    new DateTime(),
                    account.getIoTPotRoles()));
  }
}
