package com.iotpot.server.security.provider;

import com.iotpot.server.service.DateTimeService;
import org.springframework.data.auditing.DateTimeProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by vinay on 1/28/16.
 */
public class IoTPotAuditingDateTimeProvider implements DateTimeProvider {

    private final DateTimeService dateTimeService;

    public IoTPotAuditingDateTimeProvider(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Override
    public Calendar getNow() {
        return GregorianCalendar.from(dateTimeService.getCurrentDateAndTime());
    }
}