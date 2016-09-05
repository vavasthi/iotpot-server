/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.annotations;

import com.iotpot.server.common.config.validator.IoTPotNonNullStringValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by vinay on 3/15/16.
 */
@Constraint(validatedBy = IoTPotNonNullStringValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface IoTPotNonNullString {

  int min() default Integer.MIN_VALUE;
  int max() default Integer.MAX_VALUE;
  boolean nullAllowed() default true;
  String message() default "Invalid string value";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
