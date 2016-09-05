/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.config;

/**
 * Created by vinay on 3/29/16.
 */
public class ApplicationProperties {

  /**
   * The constant INSTANCE.
   */
  public static ApplicationProperties INSTANCE;
  private final String queueName;
  private final String wowzaLoadBalancerHost;
  private final String wowzaLoadBalancerPort;
  private final String apiUpdateJobUrl;
  private final String deploymentGroup;
  private final String apiServerHost;
  private final String resetPasswordPortalUrl;
  private final String s3BucketName;
  private final String stunServerName;
  private final int stunPort;

  private ApplicationProperties(final String queueName,
                                final String wowzaLoadBalancerHost,
                                final String wowzaLoadBalancerPort,
                                final String apiUpdateJobUrl,
                                final String deploymentGroup,
                                final String apiServerHost,
                                final String resetPasswordPortalUrl,
                                final String s3BucketName,
                                final String stunServerName,
                                final int stunPort) {
    this.queueName = queueName;
    this.wowzaLoadBalancerHost = wowzaLoadBalancerHost;
    this.wowzaLoadBalancerPort = wowzaLoadBalancerPort;
    this.apiUpdateJobUrl = apiUpdateJobUrl;
    this.deploymentGroup = deploymentGroup;
    this.apiServerHost = apiServerHost;
    this.resetPasswordPortalUrl = resetPasswordPortalUrl;
    this.s3BucketName = s3BucketName;
    this.stunServerName = stunServerName;
    this.stunPort = stunPort;


  }

  /**
   * Initialize.
   *
   * @param queueName              the queue name
   * @param wowzaLoadBalancerHost  the wowza load balancer host
   * @param wowzaLoadBalancerPort  the wowza load balancer port
   * @param apiUpdateJobUrl        the api update job url
   * @param deploymentGroup        the deployment group
   * @param apiServerHost          the api server host
   * @param resetPasswordPortalUrl the reset password portal url
   * @param s3BucketName           the s 3 bucket name
   */
  public static void initialize(final String queueName,
                                final String wowzaLoadBalancerHost,
                                final String wowzaLoadBalancerPort,
                                final String apiUpdateJobUrl,
                                final String deploymentGroup,
                                final String apiServerHost,
                                final String resetPasswordPortalUrl,
                                final String s3BucketName,
                                final String stunServerName,
                                final int stunPort) {

    if (INSTANCE == null) {

      INSTANCE = new ApplicationProperties(queueName,
              wowzaLoadBalancerHost,
              wowzaLoadBalancerPort,
              apiUpdateJobUrl,
              deploymentGroup,
              apiServerHost,
              resetPasswordPortalUrl,
              s3BucketName,
              stunServerName,
              stunPort);
    }
  }

  /**
   * Gets queue name.
   *
   * @return the queue name
   */
  public String getQueueName() {
    return queueName;
  }

  /**
   * Gets wowza load balancer host.
   *
   * @return the wowza load balancer host
   */
  public String getWowzaLoadBalancerHost() {
    return wowzaLoadBalancerHost;
  }

  /**
   * Gets wowza load balancer port.
   *
   * @return the wowza load balancer port
   */
  public String getWowzaLoadBalancerPort() {
    return wowzaLoadBalancerPort;
  }

  /**
   * Gets api update job url.
   *
   * @return the api update job url
   */
  public String getApiUpdateJobUrl() {
    return apiUpdateJobUrl;
  }

  /**
   * Gets deployment group.
   *
   * @return the deployment group
   */
  public String getDeploymentGroup() {
    return deploymentGroup;
  }

  /**
   * Gets api server host.
   *
   * @return the api server host
   */
  public String getApiServerHost() {
    return apiServerHost;
  }

  /**
   * Gets reset password portal url.
   *
   * @return the reset password portal url
   */
  public String getResetPasswordPortalUrl() {
    return resetPasswordPortalUrl;
  }

  /**
   * Gets s 3 bucket name.
   *
   * @return the s 3 bucket name
   */
  public String getS3BucketName() {
    return s3BucketName;
  }

  public String getStunServerName() {
    return stunServerName;
  }

  public int getStunPort() {
    return stunPort;
  }
}
