/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.enums;

import com.iotpot.server.pojos.constants.IoTPotConstants;

/**
 * Created by maheshsapre on 04/04/16.
 */
public enum AlertType {

    SOUND_DETECTED("sound detected", IoTPotConstants.SOUND_EVENT, IoTPotConstants.SOUND_EVENT_MESSAGE),

    HIGH_TEMPERATURE("high temperature detected", IoTPotConstants.HIGH_TEMP_EVENT, IoTPotConstants.HIGH_TEMP_EVENT_MESSAGE),

    LOW_TEMPERATURE("low temperature detected", IoTPotConstants.LOW_TEMP_EVENT, IoTPotConstants.LOW_TEMP_EVENT_MESSAGE),

    MOTION_DETECTED("motion detected", IoTPotConstants.MOTION_DETECT_EVENT, IoTPotConstants.MOTION_DETECT_EVENT_MESSAGE),

    UPDATING_FIRMWARE_EVENT("updating firmware", IoTPotConstants.UPDATING_FIRMWARE_EVENT, IoTPotConstants.UPDATING_FIRMWARE_EVENT_MESSAGE),

    FIRMWARE_UPDATE_SUCCESS("firmware updated", IoTPotConstants.SUCCESS_FIRMWARE_EVENT, IoTPotConstants.SUCCESS_FIRMWARE_EVENT_MESSAGE),

    RESET_PASSWORD("reset password", IoTPotConstants.RESET_PASSWORD_EVENT, IoTPotConstants.RESET_PASSWORD_EVENT_MESSAGE),

    DEVICE_REMOVED("device removed", IoTPotConstants.DEVICE_REMOVED_EVENT, IoTPotConstants.DEVICE_REMOVED_EVENT_MESSAGE),

    DEVICE_ADDED("device added", IoTPotConstants.DEVICE_ADDED_EVENT, IoTPotConstants.DEVICE_ADDED_EVENT_MESSAGE),

    CHANGE_TEMPERATURE("temperature changed", IoTPotConstants.CHANGE_TEMPERATURE_EVENT, IoTPotConstants.CHANGE_TEMPERATURE_EVENT_MESSAGE),

    //# Alert types 14,15,16 and 25 are used by pet tracker
    DOOR_MOTOIN("door motion detected", IoTPotConstants.DOOR_MOTION_DETECT_EVENT, IoTPotConstants.DOOR_MOTION_DETECT_EVENT_MESSAGE),

    TAG_PRESENCE("tag is back", IoTPotConstants.TAG_PRESENCE_DETECT_EVENT, IoTPotConstants.TAG_PRESENCE_DETECT_EVENT_MESSAGE),

    SENSOR_PAIRED("sensor added", IoTPotConstants.SENSOR_PAIRED_EVENT, IoTPotConstants.SENSOR_PAIRED_EVENT_MESSAGE),

    SENSOR_PAIRED_FAIL("failed to add sensor", IoTPotConstants.SENSOR_PAIRED_FAIL_EVENT, IoTPotConstants.SENSOR_PAIRED_FAIL_EVENT_MESSAGE),

    SD_CARD_ADDED("sd card added", IoTPotConstants.SD_CARD_ADDED_EVENT, IoTPotConstants.SD_CARD_ADDED_EVENT_MESSAGE),

    TAG_LOW_BATTERY("tag loq battery detected", IoTPotConstants.TAG_LOW_BATTERY_EVENT, IoTPotConstants.TAG_LOW_BATTERY_EVENT_MESSAGE),

    NO_TAG_PRESENCE("no tag presence detected", IoTPotConstants.NO_TAG_PRESENCE_DETECT_EVENT, IoTPotConstants.NO_TAG_PRESENCE_DETECT_EVENT_MESSAGE),

    DOOR_OPEN("door opened", IoTPotConstants.DOOR_MOTION_DETECT_OPEN_EVENT, IoTPotConstants.DOOR_MOTION_DETECT_OPEN_EVENT_MESSAGE),

    DOOR_CLOSE("door closed", IoTPotConstants.DOOR_MOTION_DETECT_CLOSE_EVENT, IoTPotConstants.DOOR_MOTION_DETECT_CLOSE_EVENT_MESSAGE),

    OCS_LOW_BATTERY("Low battery", IoTPotConstants.OCS_LOW_BATTERY_EVENT, IoTPotConstants.OCS_LOW_BATTERY_EVENT_MESSAGE),

    SD_CARD_REMOVED("sd card removed", IoTPotConstants.SD_CARD_REMOVED_EVENT, IoTPotConstants.SD_CARD_REMOVED_EVENT_MESSAGE),

    SD_CARD_NEARLY_FULL("sd card nearly full", IoTPotConstants.SD_CARD_NEARLY_FULL_EVENT, IoTPotConstants.SD_CARD_NEARLY_FULL_EVENT_MESSAGE),

    PRESS_TO_RECORD("press to record", IoTPotConstants.PRESS_TO_RECORD_EVENT, IoTPotConstants.PRESS_TO_RECORD_EVENT_MESSAGE),

    BABY_SMILE("baby smile detected", IoTPotConstants.BABY_SMILE_DETECTION_EVENT, IoTPotConstants.BABY_SMILE_DETECTION_EVENT_MESSAGE),

    BABY_SLEEPING_CARE("baby sleeping care", IoTPotConstants.BABY_SLEEPING_CARE_EVENT, IoTPotConstants.BABY_SLEEPING_CARE_EVENT_MESSAGE),

    SD_CARD__FULL("sd card full,", IoTPotConstants.SD_CARD_FULL_EVENT, IoTPotConstants.SD_CARD_FULL_EVENT_MESSAGE),

    SD_CARD_INSERTED("sd card inserted", IoTPotConstants.SD_CARD_INSERTED_EVENT, IoTPotConstants.SD_CARD_INSERTED_EVENT_MESSAGE),

    SCHEDULE_SDCARD_RECORDING(IoTPotConstants.SCHEDULE_SDCARD_RECORDING_MESSAGE, IoTPotConstants.SCHEDULE_SDCARD_RECORDING, IoTPotConstants.SCHEDULE_SDCARD_RECORDING_MESSAGE),

    ATMOSPHERE_TEMPERATURE_CHECKED("device atmosphere temperature", IoTPotConstants.ATMOSPHERE_TEMPERATURE_EVENT, IoTPotConstants.ATMOSPHERE_TEMPERATURE_EVENT_MESSAGE),
    ATMOSPHERE_SOUND_CHECKED("device atmosphere sound", IoTPotConstants.ATMOSPHERE_SOUND_EVENT, IoTPotConstants.ATMOSPHERE_SOUND_EVENT_MESSAGE),
    ATMOSPHERE_HUMIDITY_CHECKED("device atmosphere humidity", IoTPotConstants.ATMOSPHERE_HUMIDITY_EVENT, IoTPotConstants.ATMOSPHERE_HUMIDITY_EVENT_MESSAGE),
    ATMOSPHERE_BSC_CHECKED("device atmosphere BSC", IoTPotConstants.ATMOSPHERE_BSC_EVENT, IoTPotConstants.ATMOSPHERE_BSC_EVENT_MESSAGE),

    SN_MIST_LEVEL("mist level change detected", IoTPotConstants.SN_MIST_LEVEL_EVENT, IoTPotConstants.SN_MIST_LEVEL_MESSAGE),
    SN_HUMIDIFIER("humidifier on", IoTPotConstants.SN_HUMIDIFIER_STATUS_EVENT, IoTPotConstants.SN_HUMIDIFIER_STATUS_MESSAGE),
    SN_WATER_LEVEL("low water detected", IoTPotConstants.SN_WATER_LEVEL_EVENT, IoTPotConstants.SN_WATER_LEVEL_MESSAGE),
    SN_WEIGHT("new weight update", IoTPotConstants.SN_WEIGHT_EVENT, IoTPotConstants.SN_WEIGHT_MESSAGE),
    SN_TEMPERATURE_HIGH("High temperature", IoTPotConstants.SN_TEMPERATURE_HIGH_EVENT, IoTPotConstants.SN_TEMPERATURE_HIGH_MESSAGE),
    SN_HUMIDITY_HIGH("High humidity", IoTPotConstants.SN_HUMIDITY_HIGH_EVENT, IoTPotConstants.SN_HUMIDITY_HIGH_MESSAGE),
    SN_TEMPERATURE_LOW("Low temperature", IoTPotConstants.SN_TEMPERATURE_LOW_EVENT, IoTPotConstants.SN_TEMPERATURE_LOW_MESSAGE),
    SN_HUMIDITY_LOW("Low humidity", IoTPotConstants.SN_HUMIDITY_LOW_EVENT, IoTPotConstants.SN_HUMIDITY_LOW_MESSAGE),
    SN_NOISE("Noise notification", IoTPotConstants.SN_NOISE_EVENT, IoTPotConstants.SN_NOISE_MESSAGE),
    SN_FILTER_CHANGE("Filter change notification", IoTPotConstants.SN_FILTER_CHANGE_EVENT, IoTPotConstants.SN_FILTER_CHANGE_MESSAGE),
    SN_WEIGHT_ANOMALY("Weight variation detected", IoTPotConstants.SN_WEIGHT_ANOMALY_EVENT, IoTPotConstants.SN_WEIGHT_ANOMALY_MESSAGE)
    ;

    private final String typeString;
    private final int type;
    private final String formattedMessage;

    AlertType(String typeString, int type, String formattedMessage) {
        this.type = type;
        this.typeString = typeString;
        this.formattedMessage = formattedMessage;
    }

    /**
     * Create mode.
     *
     * @param typeString the mode string
     * @return the mode
     */
    public static AlertType create(String typeString) {
        for (AlertType m : values()) {
            if (m.typeString.equalsIgnoreCase(typeString)) {
                return m;
            }
        }
        throw new IllegalArgumentException(String.format("%s is not a valid alert type", typeString));
    }

    /**
     * Create mode.
     *
     * @param type the mode
     * @return the mode
     */
    public static AlertType create(int type) {
        for (AlertType m : values()) {
            if (m.type == type) {
                return m;
            }
        }
        throw new IllegalArgumentException(String.format("%d is not a valid alert type", type));
    }

    @Override
    public String toString() {
        return typeString;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Integer getValue() {
        return type;
    }
}
