package pl.edu.pwr.mrodak.jp.lab04.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvgLivingSpaceResponse {
    @JsonProperty("data")
    private List<AvgLivingSpace> data;

    public List<AvgLivingSpace> getData() {
        return data;
    }

    public void setData(List<AvgLivingSpace> data) {
        this.data = data;
    }
}