package com.renan.helloworld.service;

import com.renan.helloworld.model.Produto;
import com.renan.helloworld.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// @Service indica camada de regras de negócio — é um @Component especializado.
// O Service orquestra as operações: valida, transforma e delega ao Repository.
// O Controller não deve falar diretamente com o Repository.
@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    // Na criação, o id é gerado aqui — o cliente não precisa enviar.
    // UUID.randomUUID() gera um identificador único universal (ex: "a3f2c1d0-...")
    // garantindo que nunca haverá colisão de ids na tabela.
    public Produto criar(Produto produto) {
        produto.setId(UUID.randomUUID().toString());
        repository.salvar(produto);
        return produto;
    }

    public Produto buscarPorId(String id) {
        // orElseThrow converte o Optional vazio em exceção —
        // o Spring retorna 500. Em produção, usaríamos uma exception customizada
        // para retornar 404 Not Found.
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public List<Produto> listarTodos() {
        return repository.listarTodos();
    }

    // Atualizar = buscar (valida existência) + salvar com mesmo id.
    // O PutItem do DynamoDB substitui o item inteiro — comportamento de PUT REST.
    public Produto atualizar(String id, Produto produto) {
        buscarPorId(id);
        produto.setId(id);
        repository.salvar(produto);
        return produto;
    }

    // Valida existência antes de deletar para retornar erro significativo
    // se o id não existir, já que o DynamoDB não lança exceção nesse caso.
    public void deletar(String id) {
        buscarPorId(id);
        repository.deletar(id);
    }
}
