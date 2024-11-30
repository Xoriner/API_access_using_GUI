package pl.edu.pwr.mrodak.jp.lab04.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Province {
    @JsonProperty("id-pozycja")
    private int provinceId;
    @JsonProperty("nazwa-pozycja")
    private String provinceName;

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    @Override
    public String toString() {
        return "Province{" +
                "provinceId=" + provinceId +
                ", provinceName='" + provinceName + '\'' +
                '}';
    }
}
