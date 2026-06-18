package com.renan.helloworld.controller;

import com.renan.helloworld.model.Produto;
import com.renan.helloworld.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "CRUD de Produtos no DynamoDB")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar produto", description = "Cria um novo produto no DynamoDB com ID gerado automaticamente")
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(produto));
    }

    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos da tabela Produtos no DynamoDB")
    @GetMapping
    public List<Produto> listar() {
        return service.listarTodos();
    }

    @Operation(summary = "Buscar produto por ID", description = "Busca um produto pelo ID no DynamoDB")
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Atualizar produto", description = "Substitui os dados do produto pelo ID informado")
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable String id, @RequestBody Produto produto) {
        return service.atualizar(id, produto);
    }

    @Operation(summary = "Deletar produto", description = "Remove o produto do DynamoDB pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
