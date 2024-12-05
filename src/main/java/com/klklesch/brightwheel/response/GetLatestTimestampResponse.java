package com.klklesch.brightwheel.response;

public class GetLatestTimestampResponse {
    String latest_timestamp;

    public GetLatestTimestampResponse(String latest_timestamp) {
        this.latest_timestamp = latest_timestamp;
    }

    public String getLatest_timestamp() {
        return latest_timestamp;
    }

    public void setLatest_timestamp(String latest_timestamp) {
        this.latest_timestamp = latest_timestamp;
    }
}