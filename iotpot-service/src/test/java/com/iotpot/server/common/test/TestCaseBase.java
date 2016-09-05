/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.test;

import com.iotpot.server.pojos.DataCenter;
import com.iotpot.server.pojos.IoTPotTokenResponse;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.jayway.restassured.response.Response;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.B64Code;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by vinay on 2/10/16.
 */
public class TestCaseBase {

  protected static String authenticateUrl = "http://localhost:8080/v1/internal/authenticate";
  protected static String baseUrl = "http://localhost:8080/v1/";
  protected static String username = "Hubble";
  protected static String password = "Hobble";
  protected static String internalTenant = "internal";
  protected Logger logger = Logger.getLogger(TestCaseBase.class.getName());
  protected String authToken;
  protected String deviceToken;
  protected String tempAuthToken;
  protected String accountAuthToken;
  protected DataCenter[] dataCenters;
  protected void initialization() {

    {

      String envVal  = System.getenv("TEST_AUTH_URL");
      if (envVal != null) {
        authenticateUrl = envVal;
      }
    }
    {

      String envVal  = System.getenv("TEST_AUTH_USERNAME");
      if (envVal != null) {
        username = envVal;
      }
    }
    {

      String envVal  = System.getenv("TEST_AUTH_PASSWORD");
      if (envVal != null) {
        password = envVal;
      }
    }
    {

      String envVal  = System.getenv("TEST_AUTH_TENANT");
      if (envVal != null) {
        internalTenant = envVal;
      }
    }
    Response r = given().get(baseUrl + "setup").andReturn();
    logger.info(r.prettyPrint());
    IoTPotTokenResponse authResponse =  given().
            header(IoTPotConstants.AUTH_AUTHORIZATION_HEADER, "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_USERNAME_HEADER, username).
            header(IoTPotConstants.AUTH_PASSWORD_HEADER, password).
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().post(authenticateUrl).body().as(IoTPotTokenResponse.class);

    logger.log(Level.INFO, String.format("Authenticating %s, received response %s", username, authResponse));
    authToken = authResponse.getAuthToken();
    dataCenters =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authToken).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().get(baseUrl + "internal/computeregions").body().as(DataCenter[].class);
  }
  protected void cleanup() {

/*    Response response =  given().
        header(IoTPotConstants.AUTH_AUTHORIZATION_HEADER, "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
        header(IoTPotConstants.AUTH_USERNAME_HEADER, username).
        header(IoTPotConstants.AUTH_PASSWORD_HEADER, password).
        header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
        header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
        header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().delete(IoTPotConstants.V1_SETUP_ENDPOINT).andReturn();*/
  }
}