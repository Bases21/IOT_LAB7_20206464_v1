package com.example.lab720206464.controller;

import com.example.lab720206464.client.ValidacionClient;
import com.example.lab720206464.dto.RegistroRequest;
import com.example.lab720206464.dto.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private ValidacionClient validacionClient;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody RegistroRequest request) {
        Map<String, Object> response = new HashMap<>();

        ValidationResponse dniValidation = validacionClient.validarDni(request.getDni());
        if (!dniValidation.isExito()) {
            response.put("valido", false);
            response.put("mensaje", dniValidation.getMensaje());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ValidationResponse correoValidation = validacionClient.validarCorreo(request.getCorreo());
        if (!correoValidation.isExito()) {
            response.put("valido", false);
            response.put("mensaje", correoValidation.getMensaje());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("valido", true);
        response.put("mensaje", "Usuario validado correctamente");
        return ResponseEntity.ok(response);
    }
}
