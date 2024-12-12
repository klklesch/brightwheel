package com.klklesch.brightwheel.service;

import com.klklesch.brightwheel.model.Reading;
import com.klklesch.brightwheel.request.AddReadingRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.io.BufferedOutputStream;
import java.util.SortedSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.klklesch.brightwheel.response.GetLatestTimestampResponse;
import com.klklesch.brightwheel.response.GetCumulativeCountResponse;
import org.springframework.stereotype.Component;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.OutputStream;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;

@Component
public class CounterDeviceService {

    public CounterDeviceService() {
        this(true);
    }

    public CounterDeviceService(boolean useSavedData) {
        enableSaveFile = useSavedData;
        if(useSavedData) {
            readDataFromFile();
        }
    }

    private static final String SAVE_FILE_PATH = "C:/Code/flatfile.txt";

    private static boolean enableSaveFile = true;

    private static ConcurrentHashMap<String, SortedSet<Reading>> deviceReadings = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Integer> cumulativeCounts = new ConcurrentHashMap<>();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public void addReadings(AddReadingRequest request, boolean saveToFile) {
        if(deviceReadings.containsKey(request.getId())) {
            deviceReadings.get(request.getId()).addAll(request.getReadings());
        } else {
            ConcurrentSkipListSet<Reading> set = new ConcurrentSkipListSet<>();
            set.addAll(request.getReadings());
            deviceReadings.put(request.getId(), set);
        }

        cumulativeCounts.put(request.getId(), getCumulativeCount(deviceReadings.get(request.getId())));

        if(saveToFile) {
            saveToFile(request);
        }
    }

    public void addReadings(AddReadingRequest request) {
        addReadings(request, true);
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

    public void saveToFile(AddReadingRequest addReadingRequest){
        if (enableSaveFile) {
            System.out.println("AddReadingRequest : " + addReadingRequest);

            byte data[] = addReadingRequest.toString().getBytes();
            Path p = Paths.get("C:/Code/flatfile.txt");

            try (OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
                out.write(data, 0, data.length);
            } catch (IOException x) {
                System.err.println(x);
            }
        } else {
            System.out.println("Saving to File is Disabled");
        }
    }

    public void readDataFromFile() {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(SAVE_FILE_PATH));
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);

                AddReadingRequest addReadingRequest = parseJsonObject(line);
                if(addReadingRequest != null) {
                    addReadings(addReadingRequest, false);
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AddReadingRequest parseJsonObject(String line) {
        Gson gson = new Gson();
        AddReadingRequest addReadingRequest = gson.fromJson(line, AddReadingRequest.class);

        return addReadingRequest;
    }
}