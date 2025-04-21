package com.project.fedfaxe.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitializePaymentResponse {

    private String authorizationUrl;
    private String accessCode;
    private String reference;
}
