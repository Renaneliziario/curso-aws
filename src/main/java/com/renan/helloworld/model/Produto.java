package com.renan.helloworld.model;

import java.math.BigDecimal;

// Modelo que representa um Produto. No DynamoDB não existe schema fixo —
// os atributos são definidos pelo código, não pela tabela.
// Apenas a Partition Key (id) é obrigatória em todos os itens.
public class Produto {

    // Partition Key da tabela — identifica unicamente cada item no DynamoDB
    private String id;

    private String nome;

    // BigDecimal é o tipo correto para valores monetários em Java —
    // evita erros de arredondamento do double/float
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
