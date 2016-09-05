/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.test;

import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.pojos.IoTPotTokenResponse;
import com.jayway.restassured.response.Response;
import org.apache.log4j.Level;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.B64Code;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

/**
 * Created by vinay on 2/8/16.
 */
public class ITTestIdentityServerAuthenticateWorkflow extends TestCaseBase {

  private IoTPotTokenResponse authResponse;

  @BeforeClass
  public void setup() {
    initialization();
    authResponse =  given().
            header(IoTPotConstants.AUTH_AUTHORIZATION_HEADER, "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_USERNAME_HEADER, username).
            header(IoTPotConstants.AUTH_PASSWORD_HEADER, password).
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().post(authenticateUrl).body().as(IoTPotTokenResponse.class);
    logger.log(Level.INFO, String.format("Authenticating %s, received response %s", username, authResponse));
  }


  @Test(threadPoolSize = 100, invocationCount = 400, successPercentage = 98)
  public void testAuthenticate() {

    Response r  =  given().
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, authResponse.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().post(authenticateUrl + "/validate").andReturn();
    logger.log(Level.INFO, "Authenticating using token.\n" + r.prettyPrint());
    assertEquals(r.getStatusCode(), HttpStatus.OK_200);
    IoTPotTokenResponse response = r.as(IoTPotTokenResponse.class);
    logger.log(Level.INFO, response.toString());
  }

}
