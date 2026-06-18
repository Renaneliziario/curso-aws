package com.renan.helloworld.repository;

import com.renan.helloworld.model.Endereco;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.Optional;

// Salva e busca endereços na tabela Enderecos do DynamoDB.
// A Partition Key é o próprio CEP — busca sempre por CEP exato (GetItem).
@Repository
public class EnderecoRepository {

    private static final String TABLE = "Enderecos";

    private final DynamoDbClient dynamoDb;

    public EnderecoRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    public void salvar(Endereco endereco) {
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(toMap(endereco))
                .build());
    }

    public Optional<Endereco> buscarPorCep(String cep) {
        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(Map.of("cep", AttributeValue.fromS(cep)))
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(fromMap(response.item()));
    }

    private Map<String, AttributeValue> toMap(Endereco e) {
        return Map.of(
                "cep",         AttributeValue.fromS(e.getCep()),
                "logradouro",  AttributeValue.fromS(e.getLogradouro()),
                "bairro",      AttributeValue.fromS(e.getBairro()),
                "cidade",      AttributeValue.fromS(e.getCidade()),
                "uf",          AttributeValue.fromS(e.getUf())
        );
    }

    private Endereco fromMap(Map<String, AttributeValue> item) {
        return new Endereco(
                item.get("cep").s(),
                item.get("logradouro").s(),
                item.get("bairro").s(),
                item.get("cidade").s(),
                item.get("uf").s()
        );
    }
}
