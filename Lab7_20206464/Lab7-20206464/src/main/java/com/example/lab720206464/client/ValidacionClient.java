package com.example.lab720206464.client;

import com.example.lab720206464.dto.ValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "validacion-service")
public interface ValidacionClient {

    @GetMapping("/validar/dni/{dni}")
    ValidationResponse validarDni(@PathVariable String dni);

    @GetMapping("/validar/correo/{correo}")
    ValidationResponse validarCorreo(@PathVariable String correo);
}
