package com.renan.helloworld.repository;

import com.renan.helloworld.model.Produto;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Optional<Produto> buscarPorId(String id) {
        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(Map.of("id", AttributeValue.fromS(id)))
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(fromMap(response.item()));
    }

    public List<Produto> listarTodos() {
        ScanResponse response = dynamoDb.scan(ScanRequest.builder()
                .tableName(TABLE)
                .build());

        return response.items().stream()
                .map(this::fromMap)
                .toList();
    }

    public void deletar(String id) {
        dynamoDb.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(Map.of("id", AttributeValue.fromS(id)))
                .build());
    }

    private Map<String, AttributeValue> toMap(Produto p) {
        return Map.of(
                "id",       AttributeValue.fromS(p.getId()),
                "nome",     AttributeValue.fromS(p.getNome()),
                "preco",    AttributeValue.fromN(p.getPreco().toPlainString()),
                "descricao", AttributeValue.fromS(p.getDescricao())
        );
    }

    private Produto fromMap(Map<String, AttributeValue> item) {
        return new Produto(
                item.get("id").s(),
                item.get("nome").s(),
                new BigDecimal(item.get("preco").n()),
                item.get("descricao").s()
        );
    }
}
