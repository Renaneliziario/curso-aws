package com.renan.helloworld.repository;

import com.renan.helloworld.model.Produto;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProdutoRepository {

    private static final String TABLE = "Produtos";

    private final DynamoDbClient dynamoDb;

    public ProdutoRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    public void salvar(Produto produto) {
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(toMap(produto))
                .build());
    }

    public Produto buscarPorId(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.fromS(id));

        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return null;
        }
        return fromMap(response.item());
    }

    public List<Produto> listarTodos() {
        ScanResponse response = dynamoDb.scan(ScanRequest.builder()
                .tableName(TABLE)
                .build());

        List<Produto> produtos = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            produtos.add(fromMap(item));
        }
        return produtos;
    }

    public void deletar(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.fromS(id));

        dynamoDb.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());
    }

    // converte objeto Java para o formato que o DynamoDB entende
    private Map<String, AttributeValue> toMap(Produto p) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id",        AttributeValue.fromS(p.getId()));
        item.put("nome",      AttributeValue.fromS(p.getNome()));
        item.put("preco",     AttributeValue.fromN(p.getPreco().toPlainString()));
        item.put("descricao", AttributeValue.fromS(p.getDescricao()));
        return item;
    }

    // converte o que veio do DynamoDB de volta para objeto Java
    private Produto fromMap(Map<String, AttributeValue> item) {
        Produto p = new Produto();
        p.setId(item.get("id").s());
        p.setNome(item.get("nome").s());
        p.setPreco(new BigDecimal(item.get("preco").n()));
        p.setDescricao(item.get("descricao").s());
        return p;
    }
}
