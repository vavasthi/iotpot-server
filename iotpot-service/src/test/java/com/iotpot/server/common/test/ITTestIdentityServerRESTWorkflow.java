/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.test;

import com.iotpot.server.pojos.*;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.jayway.restassured.response.Response;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.B64Code;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.jayway.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by vinay on 2/8/16.
 */
public class ITTestIdentityServerRESTWorkflow extends TestCaseBase {

  Logger logger = Logger.getLogger(ITTestIdentityServerRESTWorkflow.class);

  private Tenant tenant1;
  private Tenant tenant2;
  private Account account;
  private Appliance appliance;
  private String defaultAppName = "MyRestAssuredClient";
  @BeforeClass
  public void setup() {
    initialization();
  }

  @Test(priority = 1)
  public void testCreateAndUpdateTenant() {

    logger.info("Testing tenant creation..");

    List<DataCenter> dataCenterList = Arrays.asList(dataCenters);
    List<UUID> uuidList = new ArrayList<>();
    dataCenterList.forEach(e -> uuidList.add(e.getId()));

    String newTenant = "tenant1";
    tenant1 = getTenant(newTenant);
    if (tenant1 != null) {
      deleteTenant(tenant1);
    }
    tenant1 = new Tenant(newTenant, "tenant@tenant1.com", newTenant, uuidList);

    Tenant responseTenant =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(tenant1).post(baseUrl + "internal/tenants").body().as(Tenant.class);

    assertTrue(tenant1.getName().equals(responseTenant.getName()) && tenant1.getEmail().equals(responseTenant.getEmail()));
    tenant1 = responseTenant;

    tenant1.setEmail("newEmail@newDomain.com");
    responseTenant =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
        when().body(tenant1).put(baseUrl + "internal/tenants/"+ tenant1.getId().toString()).body().as(Tenant.class);

    assertTrue(responseTenant.getEmail().equals(tenant1.getEmail()) && responseTenant.getId().equals(tenant1.getId()));
    tenant1 = responseTenant;
  }

  @Test(priority = 2)
  public void queryNonExistentTenent() {

    logger.info("Testing non existent tenant query.");

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + "internal/tenants/" + UUID.randomUUID().toString()).thenReturn();
    logger.info(response.prettyPrint());
    assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND_404);

  }
  @Test(priority = 3)
  public void testAccountLifecycle() {

    Set<IoTPotRole> ioTPotRoleList = new HashSet<>();
    ioTPotRoleList.add(new IoTPotRole("admin"));
    String username = "user1";
    deleteAccount(tenant1, username);

    account = createAccount(tenant1, username, "user1@tenant1.com", "user123", ioTPotRoleList);
    assertTrue(verifyAccount(account, username));
    Account[] accounts = getListOfAccounts(tenant1);
    logger.info(String.format("Found %d accounts", accounts.length));
    String newEmail = "newEmail@tenant1.com";
    account.setEmail(newEmail);
    account = updateAccount(account);
    account.setPassword("user123");
    accountAuthToken = createAuthToken(account).getAuthToken();
  }
  @Test(priority = 4)
  public void testCreateDeviceLifecycle() {


    Set<IoTPotRole> ioTPotRoleList = new HashSet<>();
    ioTPotRoleList.add(new IoTPotRole("admin"));
    appliance = new Appliance("ABCDEFDEADBEEF", ioTPotRoleList);
    account = adoptDevice(account, appliance);
    appliance = account.getAppliances().iterator().next();
  }

  @Test(priority = 5)
  public void tokenLifeCycleTests() {

    logger.info("Create a new auth token..");

    IoTPotTokenResponse tokenResponse = createTempAuthToken(account, appliance);
    tempAuthToken = tokenResponse.getAuthToken();
    assertTrue(validateToken(tenant1.getDiscriminator(), tempAuthToken, "temp_token"));
    tokenResponse = createDeviceAuthToken(account, appliance, tempAuthToken);
    deviceToken = tokenResponse.getAuthToken();
    assertTrue(validateToken(tenant1.getDiscriminator(), accountAuthToken, "app_token"));
    assertTrue(validateToken(tenant1.getDiscriminator(), deviceToken, "device_token"));
    accountAuthToken = refreshToken(tenant1.getDiscriminator(), accountAuthToken, "app_token");

    deviceToken = refreshToken(tenant1.getDiscriminator(), deviceToken, "device_token");
    assertTrue(validateToken(tenant1.getDiscriminator(), authToken, "app_token"));
    assertTrue(validateToken(tenant1.getDiscriminator(), deviceToken, "device_token"));
  }

  @Test(priority = 6)
  public void unadoptDevice() {

    account = unadoptDevice(account, appliance);
  }
  @AfterClass
  public void cleanup() {

    deleteAccounts();
//    super.cleanup();
  }

  private Tenant getTenant(String discriminator) {

    Tenant[] tenantList =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + "internal/tenants").body().as(Tenant[].class);

    for (Tenant t : tenantList) {
      if (t.getDiscriminator().equals(discriminator)) {
        logger.info(String.format("%s tenant already exists. Deleting.", t.getDiscriminator()));
        return t;
      }
    }
    return null;
  }
  private void deleteAccounts() {

    Account[] accounts = getListOfAccounts(tenant1);
    for (Account account : accounts) {
      deleteAccount(account);
    }
  }
  private Tenant deleteTenant(Tenant tenant) {

    Response response =  given().
            header(IoTPotConstants.AUTH_AUTHORIZATION_HEADER, "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_USERNAME_HEADER, username).
            header(IoTPotConstants.AUTH_PASSWORD_HEADER, password).
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().post(authenticateUrl).thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    IoTPotTokenResponse authResponse = response.body().as(IoTPotTokenResponse.class);

    return given().header("Content-Type", "application/json")
            .header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant)
            .header(IoTPotConstants.AUTH_TOKEN_HEADER, authResponse.getAuthToken())
            .header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token")
            .header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName)
        .delete(baseUrl + "internal/tenants/" + tenant.getId().toString()).body().as(Tenant.class);
  }

  private IoTPotTokenResponse createAuthToken(Account account) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_USERNAME_HEADER, account.getName()).
            header(IoTPotConstants.AUTH_PASSWORD_HEADER, account.getPassword()).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().post(baseUrl + tenant1.getDiscriminator() + "/authenticate").thenReturn();
    IoTPotTokenResponse tempAuthTokenResponse = response.as(IoTPotTokenResponse.class);
    return tempAuthTokenResponse;
  }
  private IoTPotTokenResponse createTempAuthToken(Account account, Appliance appliance) {

    DeviceAuthentication deviceAuthentication = new DeviceAuthentication(account.getId(), appliance.getDeviceRegistrationId());
    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(deviceAuthentication).post(baseUrl + tenant1.getDiscriminator() + "/authenticate/tempAuth").thenReturn();
    IoTPotTokenResponse tempAuthTokenResponse = response.as(IoTPotTokenResponse.class);
    return tempAuthTokenResponse;
  }
  private IoTPotTokenResponse createDeviceAuthToken(Account account, Appliance appliance, String authToken) {

    DeviceAuthentication deviceAuthentication = new DeviceAuthentication(account.getId(), appliance.getDeviceRegistrationId());
    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, account.getTenant().getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, tempAuthToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "temp_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(deviceAuthentication).post(baseUrl + account.getTenant().getDiscriminator() + "/authenticate/appliance").thenReturn();
    IoTPotTokenResponse deviceAuthTokenResponse = response.as(IoTPotTokenResponse.class);
    return deviceAuthTokenResponse;
  }
  private Account createAccount(Account account) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(account).post(baseUrl + tenant1.getDiscriminator() + "/accounts").thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    account = response.as(Account.class);
    return account;
  }
  private Account createAccount(Tenant tenant, String username, String email, String password, Set<IoTPotRole> ioTPotRoleList) {

    Account account = new Account(tenant, username, email, password, ioTPotRoleList);
    return createAccount(account);
  }
  private Account getAccount(Account account) {

    return getAccount(account.getTenant().getDiscriminator(), account.getName());
  }
  private Account getAccount(String tenant, UUID id) {
    return getAccount(tenant, id.toString());
  }
  private Account getAccount(String tenant, String username) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + tenant + "/accounts/" + username).thenReturn();

    if (response.statusCode() == HttpStatus.OK_200) {
      return response.as(Account.class);
    }
    else {
      return null;
    }
  }

  private boolean verifyAccount(Account account, String username) {
    Account storedAccount = getAccount(account);
    assertEquals(account, storedAccount);
    return true;
  }
  private Account[] getListOfAccounts(Tenant tenant) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + tenant.getDiscriminator() + "/accounts").thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    Account[] accountList = response.as(Account[].class);
    return accountList;
  }
  private Appliance[] getListOfDevices(Tenant tenant) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + tenant.getDiscriminator() + "/appliances").thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    Appliance[] appliances = response.as(Appliance[].class);
    return appliances;
  }
  private void deleteDevice(Appliance appliance) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().delete(baseUrl + tenant1.getDiscriminator() + "/devices/" + appliance.getId()).thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
  }
  private Account updateAccount(Account account) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(account).put(baseUrl + account.getTenant().getDiscriminator() + "/accounts/" + account.getId().toString()).
        thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().get(baseUrl + tenant1.getDiscriminator() + "/accounts/" + account.getName()).thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    Account newAccount = response.as(Account.class);
    assertTrue(account.equals(newAccount));
    return newAccount;
  }
  private void deleteAccount(Tenant tenant, String username) {

    // Delete account if it exists. Ignore the error.
    Account account = getAccount(tenant.getDiscriminator(), username);
    if (account != null) {

      deleteAccount(account);
    }
  }
  private void deleteAccount(Account account) {

    // Delete account if it exists. Ignore the error.
    given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().delete(baseUrl + account.getTenant().getDiscriminator() + "/accounts/" + account.getId().toString()).thenReturn();
  }
  private String refreshToken(String tenantDiscriminator, String token, String tokenType) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenantDiscriminator).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, tokenType).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().post(String.format("%s%s/authenticate/refresh", baseUrl, tenantDiscriminator)).thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    IoTPotTokenResponse authResponse = response.body().as(IoTPotTokenResponse.class);
    return authResponse.getAuthToken();
  }
  private boolean validateToken(String tenantDiscriminator, String token, String tokenType) {

    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenantDiscriminator).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, tokenType).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().post(String.format("%s%s/authenticate/validate", baseUrl, tenantDiscriminator)).thenReturn();
    assertEquals(response.getStatusCode(), HttpStatus.OK_200);
    IoTPotTokenResponse authResponse = response.body().as(IoTPotTokenResponse.class);
    return true;
  }
  private Account adoptDevice(Account account, Appliance appliance) {

    String adoptUrl
        = baseUrl
        + tenant1.getDiscriminator()
        + "/accounts/" + account.getId().toString() + "/adopt";
    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, accountAuthToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(appliance).post(adoptUrl).
        thenReturn();
    logger.info(response.prettyPrint());
    return response.as(Account.class);
  }
  private Account unadoptDevice(Account account, Appliance appliance) {

    String adoptUrl
        = baseUrl
        + tenant1.getDiscriminator()
        + "/accounts/" + account.getId().toString() + "/adopt";
    Response response =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, tenant1.getDiscriminator()).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, accountAuthToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, defaultAppName).
        when().body(appliance).delete(adoptUrl).
        thenReturn();
    logger.info(response.prettyPrint());
    return response.as(Account.class);
  }
}
