package com.renan.aws.dynamodb.produto.model;

import java.math.BigDecimal;

// Modelo que representa um Produto.
// No DynamoDB apenas o id (Partition Key) e obrigatorio, o resto e flexivel.
public class Produto {

    private String id;
    private String nome;
    private BigDecimal preco;
    private String descricao;

    public Produto() {}

    public Produto(String id, String nome, BigDecimal preco, String descricao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
