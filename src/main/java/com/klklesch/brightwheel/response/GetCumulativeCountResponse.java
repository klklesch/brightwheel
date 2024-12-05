package com.klklesch.brightwheel.response;

public class GetCumulativeCountResponse {
    //TODO - is it reasonable to expect this to stay below the max integer level?
    int cumulativeCount;


    public GetCumulativeCountResponse(int cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }

    public int getCumulativeCount() {
        return cumulativeCount;
    }

    public void setCumulativeCount(int cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }
}