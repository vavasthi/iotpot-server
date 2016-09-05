/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.config.validator;

import com.iotpot.server.common.annotations.IoTPotUrlString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vinay on 3/15/16.
 */
public class IoTPotUrlStringValidator implements ConstraintValidator<IoTPotUrlString, String> {

  private IoTPotUrlString urlString;
  @Override
  public void initialize(final IoTPotUrlString urlString) {
    this.urlString = urlString;
  }

  @Override
  public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {

    if (s == null) {
      return true;
    }
    try {
      URL url = new URL(s);
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }
}
