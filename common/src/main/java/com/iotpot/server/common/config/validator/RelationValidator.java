/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.config.validator;

import com.iotpot.server.common.enums.RELATION;
import com.iotpot.server.common.annotations.Relation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by nikhilvs on 16/06/16.
 */
public class RelationValidator implements ConstraintValidator<Relation, String> {

  private Relation relation;
  @Override
  public void initialize(final Relation genderType) {
    this.relation = relation;
  }

  @Override
  public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
      try {
          if(s!=null) {
              RELATION relation = RELATION.valueOf(s);
          }
          return true;
      }
      catch (IllegalArgumentException ex){
          return false;
      }
  }


}
