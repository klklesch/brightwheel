package com.klklesch.brightwheel.request;
import com.klklesch.brightwheel.model.Reading;
import java.util.Collection;

public class AddReadingRequest {

    private String id;
    private Collection<Reading> readings;

    public AddReadingRequest(String id, Collection<Reading> readings) {
        this.id = id;
        this.readings = readings;
    }

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public Collection<Reading> getReadings() {
        return readings;
    }

    public void setReadings(Collection<Reading> readings) {
        this.readings = readings;
    }

}