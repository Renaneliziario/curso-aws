package com.renan.helloworld.controller;

import com.renan.helloworld.model.Produto;
import com.renan.helloworld.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Todos os métodos retornam JSON automaticamente (sem precisar de @ResponseBody em cada um)
@RestController
// @RequestMapping define o prefixo da URL para todos os endpoints da classe
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // POST /produtos — recebe o JSON do corpo da requisição e cria o produto
    // @RequestBody deserializa o JSON para o objeto Produto automaticamente
    // ResponseEntity permite controlar o status HTTP — 201 Created é o correto para criação
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(produto));
    }

    // GET /produtos — lista todos os produtos
    // Retorno direto de List<Produto> usa status 200 OK automaticamente
    @GetMapping
    public List<Produto> listar() {
        return service.listarTodos();
    }

    // GET /produtos/{id} — busca um produto pelo id
    // @PathVariable captura o valor do {id} da URL
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    // PUT /produtos/{id} — substitui o produto inteiro pelo id informado
    // PUT = substituição completa. PATCH seria atualização parcial.
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable String id, @RequestBody Produto produto) {
        return service.atualizar(id, produto);
    }

    // DELETE /produtos/{id} — remove o produto pelo id
    // 204 No Content = sucesso sem corpo de resposta (padrão REST para DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
