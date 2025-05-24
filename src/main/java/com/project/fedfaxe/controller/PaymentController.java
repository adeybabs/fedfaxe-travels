package com.project.fedfaxe.controller;

import com.project.fedfaxe.service.PaystackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaystackService paystackService;


//    @PostMapping("/init-stay-payment")
//    public ResponseEntity<InitializePaymentResponse> initStayPayment(@RequestBody @Valid BookStayRequest request,
//                                                                     @RequestHeader("X-USER-ID") String userId) {
//        return ResponseEntity.ok(paystackService.initializeStayPayment(request, userId));
//    }
}
