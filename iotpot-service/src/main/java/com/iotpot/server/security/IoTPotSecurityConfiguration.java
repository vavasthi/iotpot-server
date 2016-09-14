/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.security;

import com.iotpot.server.common.enums.Role;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.security.filters.IoTPotAuthenticationFilter;
import com.iotpot.server.security.provider.IoTPotTokenAuthenticationProvider;
import com.iotpot.server.security.provider.IoTPotUsernamePasswordAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class IoTPotSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(h2oUsernamePasswordAuthenticationProvider()).
        authenticationProvider(tokenAuthenticationProvider());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(IoTPotConstants.V1_SETUP_ENDPOINT);
    web.ignoring().antMatchers("/manage/health");
    web.ignoring().antMatchers("/swagger-ui.html");
    web.ignoring().antMatchers("/webjars/**");
    web.ignoring().antMatchers("/v2/api-docs/**");
    web.ignoring().antMatchers("/swagger-resources/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
        and().
        authorizeRequests().
            antMatchers(actuatorEndpoints()).hasRole(Role.ADMIN.getValue()).
        anyRequest().authenticated().
        and().
        anonymous().disable().
        exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

    http.addFilterBefore(new IoTPotAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
  }

  private String[] actuatorEndpoints() {
    return new String[]{
            IoTPotConstants.AUTOCONFIG_ENDPOINT,
            IoTPotConstants.BEANS_ENDPOINT,
            IoTPotConstants.CONFIGPROPS_ENDPOINT,
            IoTPotConstants.ENV_ENDPOINT,
            IoTPotConstants.MAPPINGS_ENDPOINT,
            IoTPotConstants.METRICS_ENDPOINT,
            IoTPotConstants.SHUTDOWN_ENDPOINT
    };
  }

  @Bean
  public AuthenticationEntryPoint unauthorizedEntryPoint() {
    return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Bean
  public AuthenticationProvider h2oUsernamePasswordAuthenticationProvider() {
    return new IoTPotUsernamePasswordAuthenticationProvider();
  }

  @Bean
  public AuthenticationProvider tokenAuthenticationProvider() {
    return new IoTPotTokenAuthenticationProvider();
  }

}