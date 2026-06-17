package com.renan.helloworld.service;

import com.renan.helloworld.model.Produto;
import com.renan.helloworld.repository.ProdutoRepository;
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
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public List<Produto> listarTodos() {
        return repository.listarTodos();
    }

    public Produto atualizar(String id, Produto produto) {
        buscarPorId(id); // valida que existe
        produto.setId(id);
        repository.salvar(produto);
        return produto;
    }

    public void deletar(String id) {
        buscarPorId(id); // valida que existe
        repository.deletar(id);
    }
}
