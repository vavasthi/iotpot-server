package com.iotpot.server.common.utils;

/**
 * Created by nikhilvs9999 on 12/12/15.
 */
public class HttpRestResponse {

  private String response;
  private int responseCode = -1;

  public HttpRestResponse(String response, int responseCode) {
    this.response = response;
    this.responseCode = responseCode;
  }

  public HttpRestResponse() {
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  @Override
  public String toString() {
    return "HttpRestResponse{" +
        "response='" + response + '\'' +
        ", responseCode=" + responseCode +
        '}';
  }
}
