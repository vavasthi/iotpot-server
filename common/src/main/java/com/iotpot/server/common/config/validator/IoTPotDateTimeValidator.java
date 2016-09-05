/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.config.validator;

import com.iotpot.server.common.annotations.IoTPotDateTime;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by nikhilvs on 16/06/16.
 */
public class IoTPotDateTimeValidator implements ConstraintValidator<IoTPotDateTime, String> {

  private Logger logger = Logger.getLogger(GenderTypeValidator.class);
  private IoTPotDateTime ioTPotDateTime;
  @Override
  public void initialize(final IoTPotDateTime ioTPotDateTime) {
    this.ioTPotDateTime = ioTPotDateTime;
  }

  @Override
  public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
    try {

      if(s!=null) {
          logger.info(" s :" + s + " h2oDateTime : " + ioTPotDateTime.dateTimePattern());
          DateTimeFormatter formatter = DateTimeFormat.forPattern(ioTPotDateTime.dateTimePattern());
          formatter.parseDateTime(s);
      }
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
