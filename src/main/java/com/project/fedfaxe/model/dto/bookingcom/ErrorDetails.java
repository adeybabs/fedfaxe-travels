package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetails {

    private String code;
    private String message;

    @Override
    public String toString() {
        return "ErrorDetails{code='" + code + "', message='" + message + "'}";
    }
}
