/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.util;

import com.google.common.base.Optional;
import com.iotpot.server.security.token.H2OTokenPrincipal;

/**
 * Created by vinay on 2/12/16.
 */
public class IoTPotUtils {


  public static String getTenantKeyFromDiscriminator(String discriminator) {
    return "<DiSCRIMINATOR>" + discriminator + "</DiSCRIMINATOR>";
  }

  public static String getAccountKeyFromAuthToken(String authToken) {
    return "<AUTH_TOKEN>" + authToken + "</AUTH_TOKEN>";
  }

  public static H2OTokenPrincipal.TOKEN_TYPE getTokenType(Optional<String> tokenTypeStr) {

    H2OTokenPrincipal.TOKEN_TYPE tokenType = H2OTokenPrincipal.TOKEN_TYPE.UNKNOWN_TOKEN;
    if (tokenTypeStr.isPresent()) {
      tokenType = H2OTokenPrincipal.TOKEN_TYPE.createFromString(tokenTypeStr.get());
    }
    return tokenType;
  }
}
