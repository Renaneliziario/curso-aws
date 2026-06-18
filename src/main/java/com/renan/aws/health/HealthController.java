package com.renan.aws.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Verificação de saúde da aplicação")
public class HealthController {

    @Operation(summary = "Home", description = "Página inicial da aplicação")
    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("app", "aws-spring-lab");
        response.put("status", "up");
        return response;
    }

    @Operation(summary = "Health check", description = "Retorna status da aplicação")
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "up");
        return response;
    }
}
