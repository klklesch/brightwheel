package com.klklesch.brightwheel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import com.klklesch.brightwheel.request.*;
import com.klklesch.brightwheel.model.Reading;
import java.util.Date;
import java.util.Calendar;

@SpringBootTest
public class CounterDeviceServiceTests {

    private CounterDeviceService counterDeviceService = new CounterDeviceService(false);

    @Test
    public void shouldCalculateCumulativeCount() {
        assertEquals(counterDeviceService.getCumulativeCount("DEVICE_ID").getCumulativeCount(), 0);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2024, 1, 1);
        ArrayList<Reading> readings = new ArrayList<>();
        readings.add(new Reading(calendar1.getTime(), 1));
        AddReadingRequest addReadingRequest = new AddReadingRequest("DEVICE_ID", readings);
        counterDeviceService.addReadings(addReadingRequest);

        assertEquals(1, counterDeviceService.getCumulativeCount("DEVICE_ID").getCumulativeCount());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2024, 2, 2);
        readings.add(new Reading(calendar2.getTime(), 10));
        counterDeviceService.addReadings(addReadingRequest);

        assertEquals(11, counterDeviceService.getCumulativeCount("DEVICE_ID").getCumulativeCount());
    }

    @Test
    public void shouldReturnLatestTimestamp() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(100000000);
        ArrayList<Reading> readings = new ArrayList<>();
        readings.add(new Reading(calendar1.getTime(), 1));
        AddReadingRequest addReadingRequest = new AddReadingRequest("DEVICE_ID2", readings);
        counterDeviceService.addReadings(addReadingRequest);

        assertEquals("1970-01-01T22:46:40.000-05:00", counterDeviceService.getLatestTimestamp("DEVICE_ID2").getLatest_timestamp());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(200000000);
        readings.add(new Reading(calendar2.getTime(), 10));
        counterDeviceService.addReadings(addReadingRequest);

        assertEquals("1970-01-03T02:33:20.000-05:00", counterDeviceService.getLatestTimestamp("DEVICE_ID2").getLatest_timestamp());


        Calendar calendar3 = Calendar.getInstance();
        calendar2.setTimeInMillis(300000000);
        readings.add(new Reading(calendar2.getTime(), 10));
        counterDeviceService.addReadings(addReadingRequest);

        assertEquals("1970-01-04T06:20:00.000-05:00", counterDeviceService.getLatestTimestamp("DEVICE_ID2").getLatest_timestamp());
    }
}