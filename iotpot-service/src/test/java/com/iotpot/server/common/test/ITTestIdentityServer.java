/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.test;

import com.iotpot.server.pojos.DataCenter;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.pojos.IoTPotTokenResponse;
import com.jayway.restassured.response.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.B64Code;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

/**
 * Created by vinay on 2/8/16.
 */
public class ITTestIdentityServer extends TestCaseBase{

  @BeforeClass
  public void setup() {
    initialization();
  }


  @Test
  public void testAuthToken() {

    initialization();
    Response r = given().
            header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
            header("Content-Type", "application/json").
            header("X-AUTH_USERNAME", username).
            header("X-AUTH-PASSWORD", password).
            header("X-AUTH-TENANT", internalTenant).
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
            when().post(authenticateUrl).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.OK_200);
    IoTPotTokenResponse token = r.as(IoTPotTokenResponse.class);
    logger.info(r.prettyPrint());
    logger.info(token.getAuthToken());
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(new DataCenter("aaaa",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            0L)).
        post(String.format("%s%s/computeregions",baseUrl, internalTenant)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.BAD_REQUEST_400);
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(new DataCenter("aaaaaaa",
            "bbbbbbbbbb",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            0L)).
        post(String.format("%s%s/computeregions",baseUrl, internalTenant)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.BAD_REQUEST_400);
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(new DataCenter("aaaaaa",
            "bbbbbb",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            0L)).
        post(String.format("%s%s/computeregions",baseUrl, internalTenant)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.BAD_REQUEST_400);
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(new DataCenter
        ("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456",
                "http://api.hubble.in",
                "http://api.hubble.in",
                "http://api.hubble.in",
                "http://api.hubble.in",
                "http://api.hubble.in",
                "http://api.hubble.in",
                0L)).
        post(String.format("%s%s/computeregions",baseUrl, internalTenant)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.BAD_REQUEST_400);
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(new DataCenter("Name1",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            "http://api.hubble.in",
            0L)).
        post(String.format("%s%s/computeregions",baseUrl, internalTenant)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.OK_200);
    DataCenter cr = r.as(DataCenter.class);
    cr.setName("ThisIsMyNewName");
    String id = cr.getId().toString();
    cr.setId(null);
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().body(cr).
        put(String.format("%s%s/computeregions/%s",baseUrl, internalTenant, id)).thenReturn();
    assertEquals(r.getStatusCode(), HttpStatus.OK_200);
    DataCenter cr1 = r.as(DataCenter.class);
    assertEquals(cr.getName(), cr1.getName());
    r = given().
        header("Authorization", "Basic " + B64Code.encode(username + ":" + password)).
        header("Content-Type", "application/json").
            header(IoTPotConstants.AUTH_TENANT_HEADER, internalTenant).
            header(IoTPotConstants.AUTH_TOKEN_HEADER, token.getAuthToken()).
            header(IoTPotConstants.AUTH_TOKEN_TYPE_HEADER, "app_token").
            header(IoTPotConstants.AUTH_APPLICATION_ID_HEADER, "MyRestAssuredClient").
        when().
        delete(String.format("%s%s/computeregions/%s",baseUrl, internalTenant, cr1.getId().toString())).thenReturn();
    logger.info(r.prettyPrint());
    logger.info(token.getAuthToken());
  }
}
