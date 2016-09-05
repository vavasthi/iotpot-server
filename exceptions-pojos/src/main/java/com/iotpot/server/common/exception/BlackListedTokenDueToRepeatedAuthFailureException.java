/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.exception;

/**
 * Created by vinay on 6/17/16.
 */
public class BlackListedTokenDueToRepeatedAuthFailureException extends BlackListedDueToRepeatedAuthFailureException {
    public BlackListedTokenDueToRepeatedAuthFailureException(String authToken, String ipAddress) {
        super(authToken, ipAddress);
    }
}
