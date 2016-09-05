/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.iotpot.server.security.provider.IoTPotAuditingDateTimeProvider;
import com.iotpot.server.service.DateTimeService;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.security.IoTPotAuditorAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Logger;

/**
 * Created by vinay on 1/8/16.
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {
        "com.iotpot.server"
})
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = {"com.iotpot.server.dao"})
//@ImportResource({"classpath*:spring/applicationContext.xml"})
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableTransactionManagement
//@EnableSwagger2
public class IoTPotServiceLauncher extends SpringBootServletInitializer {

  Logger logger = Logger.getLogger(IoTPotServiceLauncher.class.getName());
  @Autowired
  private TenantDao tenantDao;

  public static void main(String[] args) throws Exception {

    IoTPotServiceLauncher launcher = new IoTPotServiceLauncher();
    launcher
            .configure(new SpringApplicationBuilder(IoTPotServiceLauncher.class))
            .run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(IoTPotServiceLauncher.class);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  AuditorAware<String> auditorProvider() {
    return new IoTPotAuditorAware();
  }

  @Bean
  DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
    return new IoTPotAuditingDateTimeProvider(dateTimeService);
  }

  @Bean
  public Module jodaModule() {
    return new JodaModule();
  }
/*    @Bean
    RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = IoTPotConstants.IoTPotRole.ADMIN.getValue() + " > " + IoTPotConstants.IoTPotRole.FW_UPGRADE_ADMIN.getValue() + " ";
        hierarchy += ()
        roleHierarchy.setHierarchy();

    }*/

}