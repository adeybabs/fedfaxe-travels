package com.project.fedfaxe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/rides")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class RideAdminController {
}
