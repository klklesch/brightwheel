package com.klklesch.brightwheel.model;

import java.util.Date;
import java.util.Objects;
//import org.apache.commons.lang3.builder.ToStringBuilder;

public class Reading implements Comparable<Reading> {
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private Date timestamp;
    private int count;

    public Reading(Date timestamp, int count) {
        this.timestamp = timestamp;
        this.count = count;
    }


    public final boolean equals(Object object) {
        if (!(object instanceof Reading)) return false;
        if (!super.equals(object)) return false;

        Reading reading = (Reading) object;
        return java.util.Objects.equals(getTimestamp(), reading.getTimestamp()) && java.util.Objects.equals(getCount(), reading.getCount());
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getTimestamp());
        result = 31 * result + Objects.hashCode(getCount());
        return result;
    }

    public int compareTo(Reading other) {
        return this.getTimestamp().compareTo(other.getTimestamp());
    }

}