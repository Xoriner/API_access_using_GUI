package pl.edu.pwr.mrodak.jp.lab04.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvgLivingSpace {
    @JsonProperty("id-pozycja-1")
    private int provinceId;
    @JsonProperty("id-daty")
    private int year;
    @JsonProperty("wartosc")
    private double value;

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getYear() {
        return year;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AvgLivingSpace{" +
                "provinceId=" + provinceId +
                ", year=" + year +
                ", value=" + value +
                '}';
    }
}
