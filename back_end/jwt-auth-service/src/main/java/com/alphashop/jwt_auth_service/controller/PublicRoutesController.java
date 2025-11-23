package com.alphashop.jwt_auth_service.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alphashop.jwt_auth_service.security.constants.PublicRoutes;

@RestController
public class PublicRoutesController {

    @GetMapping(PublicRoutes.PUBLIC_ROUTES)
    public ResponseEntity<List<String>> getPublicRoutes() {
        // Restituisce tutte le rotte pubbliche definite in PublicRoutes
        List<String> routes = Arrays.asList(
            PublicRoutes.all()
        );
        return ResponseEntity.ok(routes);
    }
}