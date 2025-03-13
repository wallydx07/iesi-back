package com.example.iesiback.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/request-info")
    public ResponseEntity<Map<String, String>> logRequestInfo(HttpServletRequest request) {
        String requestedUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        // Crear un mapa para devolver la respuesta como JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "URL Requested: " + requestedUrl + (queryString != null ? "?" + queryString : ""));

        // Retornar la respuesta como JSON
        return ResponseEntity.ok(response);
    }

}
