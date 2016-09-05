/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.utils;

import com.google.common.collect.Lists;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.rmi.UnknownHostException;
import java.util.List;


public class HttpRestClient {

  private static final Logger log = Logger.getLogger(HttpRestClient.class);
  HttpRequestRetryHandler httpRetryHandler;
  ServiceUnavailableRetryStrategy serviceUnavailableHandler;
  Header header;

  public HttpRestClient() {

    header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    this.httpRetryHandler = (IOException exception, int executionCount, HttpContext context) -> {
      if (executionCount >= IoTPotConstants.HTTP_RETRY_COUNT) {
        // Do not retry if over max retry count
        return false;
      }
      if (exception instanceof InterruptedIOException) {
        // Timeout
        return false;
      }
      if (exception instanceof UnknownHostException) {
        // Unknown host
        return false;
      }
      if (exception instanceof ConnectTimeoutException) {
        // Connection refused
        return false;
      }
      if (exception instanceof SSLException) {
        // SSL handshake exception
        return false;
      }
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      HttpRequest request = clientContext.getRequest();
      boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
      return idempotent;
    };

    serviceUnavailableHandler = new ServiceUnavailableRetryStrategy() {
      @Override
      public boolean retryRequest(
          final HttpResponse response, final int executionCount, final HttpContext context) {
        int statusCode = response.getStatusLine().getStatusCode();
        boolean retryRequired = false;
        for (int t : IoTPotConstants.HTTP_RETRY_RESPONSE_CODES) {
          if (t == statusCode) {
            retryRequired = true;
          }
        }
        return (retryRequired) && executionCount < 5;
      }

      @Override
      public long getRetryInterval() {
        return 0;
      }
    };
  }
/*
  public static void main(String args[]) throws IOException {

    String url = String.format("%s/v1/jobs/stream_status/%s?api_key=Jdwi8dS-e3Fixz-L6y5x", JobWorkerConstants.API_SERVER_HOST, "b8e618f9e3d0");
    HttpRestClient http = new HttpRestClient();
    HttpRestResponse r = http.put(url, "");
    log.info(r.getResponse() + r.getResponseCode());


  }
*/
  public HttpRestResponse put(String url, String nameValuePairs) throws IOException {
    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
        .setRetryHandler(httpRetryHandler)
            .setDefaultRequestConfig(requestConfigWithTimeout(IoTPotConstants.HTTP_TIMEOUT))
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();
    HttpRestResponse result = new HttpRestResponse();
    HttpPut httpput = new HttpPut(url);
    StringEntity e = new StringEntity(nameValuePairs);
    httpput.setEntity(e);
    log.info("executing request  :" + httpput.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httpput)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }

    }
    return result;
  }

  public RequestConfig requestConfigWithTimeout(int timeoutInMilliseconds) {
    return RequestConfig.custom()
        .setSocketTimeout(timeoutInMilliseconds * 1000)
        .setConnectTimeout(timeoutInMilliseconds * 1000)
        .build();

  }

  public HttpRestResponse post(String url, List<NameValuePair> nameValuePairs, int timeOut) throws IOException {
    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
        .setRetryHandler(httpRetryHandler)
        .setDefaultRequestConfig(requestConfigWithTimeout(timeOut))
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();
    HttpRestResponse result = new HttpRestResponse();
    HttpPost httppost = new HttpPost(url);
    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    log.trace("executing request  :" + httppost.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httppost)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }

    }
    return result;
  }

  public HttpRestResponse post(String url, int timeOut) throws IOException {


    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
        .setRetryHandler(httpRetryHandler)
        .setDefaultRequestConfig(requestConfigWithTimeout(timeOut))
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();
    HttpRestResponse result = new HttpRestResponse();
    HttpPost httppost = new HttpPost(url);
    log.info("executing request  :" + httppost.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httppost)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }

    }
    return result;
  }

  public HttpRestResponse put(String url, List<NameValuePair> nameValuePairs, StringEntity inputJson) throws IOException {
    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
        .setRetryHandler(httpRetryHandler)
            .setDefaultRequestConfig(requestConfigWithTimeout(IoTPotConstants.HTTP_TIMEOUT))
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();
    HttpRestResponse result = new HttpRestResponse();
    HttpPut httpput = new HttpPut(url);
    httpput.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    log.info("executing request  :" + httpput.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httpput)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }

    }
    return result;
  }

  public HttpRestResponse delete(String url, List<NameValuePair> nameValuePairs) throws IOException {
    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
        .setRetryHandler(httpRetryHandler)
            .setDefaultRequestConfig(requestConfigWithTimeout(IoTPotConstants.HTTP_TIMEOUT))
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();
    HttpRestResponse result = new HttpRestResponse();
    HttpDelete httpDelete = new HttpDelete(url);
    log.info("executing request  :" + httpDelete.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httpDelete)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }

    }
    return result;
  }

  public HttpRestResponse get(String url) throws IOException {
    List<Header> headers = Lists.newArrayList(header);
    CloseableHttpClient httpclient = HttpClients.custom()
            .setDefaultRequestConfig(requestConfigWithTimeout(IoTPotConstants.HTTP_TIMEOUT))
        .setRetryHandler(httpRetryHandler)
        .setServiceUnavailableRetryStrategy(serviceUnavailableHandler)
        .setDefaultHeaders(headers)
        .build();

    HttpRestResponse result = new HttpRestResponse();
    HttpGet httpget = new HttpGet(url);
    log.info("executing request  :" + httpget.getRequestLine());
    try (CloseableHttpResponse response = httpclient.execute(httpget)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result.setResponseCode(response.getStatusLine().getStatusCode());
        result.setResponse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
      }
    }
    return result;
  }

}
