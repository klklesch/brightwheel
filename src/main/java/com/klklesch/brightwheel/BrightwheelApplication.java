package com.klklesch.brightwheel;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.klklesch.brightwheel.model.Reading;
import com.klklesch.brightwheel.request.AddReadingRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.klklesch.brightwheel.response.GetLatestTimestampResponse;
import com.klklesch.brightwheel.response.GetCumulativeCountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.klklesch.brightwheel.service.CounterDeviceService;

@SpringBootApplication
@RestController
public class BrightwheelApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrightwheelApplication.class, args);
	}

	//TODO @Autowired wasn't working here
	public static CounterDeviceService counterDeviceService = new CounterDeviceService();

	@PostMapping("/addReadings")
	public void addReadings(@RequestBody AddReadingRequest request) {
		counterDeviceService.addReadings(request);
	}

	@GetMapping("/latestTimestamp")
	public GetLatestTimestampResponse getLatestTimestamp(String deviceId) {
		return counterDeviceService.getLatestTimestamp(deviceId);
	}

	@GetMapping("/cumulativeCount")
	public GetCumulativeCountResponse getCumulativeCount(String deviceId) {
		return counterDeviceService.getCumulativeCount(deviceId);
	}
}
