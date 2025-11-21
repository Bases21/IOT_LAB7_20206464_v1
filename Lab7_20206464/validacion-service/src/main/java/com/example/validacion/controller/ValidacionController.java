package com.example.validacion.controller;

import com.example.validacion.dto.ValidationResponse;
import com.example.validacion.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validar")
public class ValidacionController {

    @Autowired
    private FirebaseService firebaseService;

    @GetMapping("/dni/{dni}")
    public ValidationResponse validarDni(@PathVariable String dni) {
        if (dni == null || dni.length() != 8) {
            return new ValidationResponse(false, "El DNI debe tener 8 dígitos");
        }
        
        if (!dni.matches("\\d+")) {
            return new ValidationResponse(false, "El DNI solo puede contener números");
        }

        if (firebaseService.dniExists(dni)) {
            return new ValidationResponse(false, "El DNI ya está registrado");
        }
        
        return new ValidationResponse(true, "DNI disponible");
    }

    @GetMapping("/correo/{correo}")
    public ValidationResponse validarCorreo(@PathVariable String correo) {
        if (correo == null || !correo.endsWith("@pucp.edu.pe")) {
            return new ValidationResponse(false, "El correo debe ser del dominio @pucp.edu.pe");
        }

        if (firebaseService.correoExists(correo)) {
            return new ValidationResponse(false, "El correo ya está registrado");
        }
        
        return new ValidationResponse(true, "Correo disponible");
    }
}
