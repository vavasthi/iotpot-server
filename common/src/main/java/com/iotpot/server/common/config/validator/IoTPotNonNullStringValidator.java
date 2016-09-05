/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.config.validator;

import com.iotpot.server.common.annotations.IoTPotNonNullString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by vinay on 3/15/16.
 */
public class IoTPotNonNullStringValidator implements ConstraintValidator<IoTPotNonNullString, String> {

  private IoTPotNonNullString ioTPotNonNullString;
  @Override
  public void initialize(final IoTPotNonNullString ioTPotNonNullString) {
    this.ioTPotNonNullString = ioTPotNonNullString;
  }

  @Override
  public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {

    if (s == null) {
      return ioTPotNonNullString.nullAllowed();
    }
    if (s.contains("<") || s.contains("<")) {
      return false;
    }
    if (s.length() >= ioTPotNonNullString.min() && s.length() <= ioTPotNonNullString.max()) {
      return true;
    }
    return false;
  }
}
