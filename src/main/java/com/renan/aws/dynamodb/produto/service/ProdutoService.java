package com.renan.aws.dynamodb.produto.service;

import com.renan.aws.dynamodb.produto.model.Produto;
import com.renan.aws.dynamodb.produto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto criar(Produto produto) {
        produto.setId(UUID.randomUUID().toString());
        repository.salvar(produto);
        return produto;
    }

    public Produto buscarPorId(String id) {
        Produto produto = repository.buscarPorId(id);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }
        return produto;
    }

    public List<Produto> listarTodos() {
        return repository.listarTodos();
    }

    public Produto atualizar(String id, Produto produto) {
        Produto existente = repository.buscarPorId(id);
        if (existente == null) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }
        produto.setId(id);
        repository.salvar(produto);
        return produto;
    }

    public void deletar(String id) {
        Produto existente = repository.buscarPorId(id);
        if (existente == null) {
            throw new RuntimeException("Produto não encontrado: " + id);
        }
        repository.deletar(id);
    }
}
