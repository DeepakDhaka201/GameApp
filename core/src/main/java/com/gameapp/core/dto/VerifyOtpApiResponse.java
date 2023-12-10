package com.gameapp.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyOtpApiResponse {
    @JsonProperty("Status")
    private String Status;

    @JsonProperty("Details")
    private String Details;
}
