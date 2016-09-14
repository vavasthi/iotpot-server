/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.annotations;

import com.iotpot.server.common.config.validator.IoTPotDateTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = IoTPotDateTimeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface IoTPotDateTime {

    String message() default "Invalid datetime format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String dateTimePattern() default "";

}
