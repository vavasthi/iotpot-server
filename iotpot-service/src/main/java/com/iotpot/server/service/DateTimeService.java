package com.iotpot.server.service;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DateTimeService {

    public ZonedDateTime getCurrentDateAndTime() {
        return ZonedDateTime.now();
    }
}