package com.renan.helloworld.controller;

import com.renan.helloworld.model.Endereco;
import com.renan.helloworld.service.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    // POST /cep/{cep} — envia o CEP para a fila SQS
    // Retorna imediatamente sem processar — comportamento assíncrono
    @PostMapping("/{cep}")
    public ResponseEntity<String> enviar(@PathVariable String cep) {
        return ResponseEntity.accepted().body(cepService.enviarCep(cep));
    }

    // POST /cep/processar — consome mensagens da fila e salva no DynamoDB
    @PostMapping("/processar")
    public List<String> processar() {
        return cepService.processarFila();
    }

    // GET /cep/{cep} — lê o endereço salvo no DynamoDB
    @GetMapping("/{cep}")
    public Endereco buscar(@PathVariable String cep) {
        return cepService.buscarEndereco(cep);
    }
}
