package com.klklesch.brightwheel.service;

import com.klklesch.brightwheel.model.Reading;
import com.klklesch.brightwheel.request.AddReadingRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.SortedSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.klklesch.brightwheel.response.GetLatestTimestampResponse;
import com.klklesch.brightwheel.response.GetCumulativeCountResponse;
import org.springframework.stereotype.Component;

@Component
public class CounterDeviceService {

    private static ConcurrentHashMap<String, SortedSet<Reading>> deviceReadings = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Integer> cumulativeCounts = new ConcurrentHashMap<>();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public void addReadings(AddReadingRequest request) {
        if(deviceReadings.containsKey(request.getId())) {
            deviceReadings.get(request.getId()).addAll(request.getReadings());
        } else {
            ConcurrentSkipListSet<Reading> set = new ConcurrentSkipListSet<>();
            set.addAll(request.getReadings());
            deviceReadings.put(request.getId(), set);
        }

        cumulativeCounts.put(request.getId(), getCumulativeCount(deviceReadings.get(request.getId())));
    }

    public GetLatestTimestampResponse getLatestTimestamp(String deviceId) {
        if(deviceReadings.containsKey(deviceId)) {
            return new GetLatestTimestampResponse(
                    formatDate(deviceReadings.get(deviceId).last().getTimestamp()));
        }
        else {
            //TODO - handle this case more gracefully
            return null;
        }
    }

    public GetCumulativeCountResponse getCumulativeCount(String deviceId) {
        if(cumulativeCounts.containsKey(deviceId)) {
            return new GetCumulativeCountResponse(cumulativeCounts.get(deviceId));
        }
        else {
            return new GetCumulativeCountResponse(0);
        }
    }

    private String formatDate(Date date) {
        return sdf.format(date);
    }

    private int getCumulativeCount(SortedSet<Reading> readings) {
        int sum=0;
        for(Reading r: readings) {
            sum += r.getCount();
        }

        return sum;
    }
}