/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import org.apache.commons.codec.binary.Hex;

import java.util.UUID;

public class AbstractDatabaseLoaderService {

    public String generateDeviceAuthToken(String deviceRegistrationId) {

        UUID uuid = UUID.randomUUID();

        String token = Long.toHexString(uuid.getLeastSignificantBits()) +
                Long.toHexString(uuid.getMostSignificantBits()) +
                Hex.encodeHexString(deviceRegistrationId.getBytes());
        return token;
    }
}
