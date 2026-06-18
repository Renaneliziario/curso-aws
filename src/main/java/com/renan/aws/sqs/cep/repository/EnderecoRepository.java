package com.renan.aws.sqs.cep.repository;

import com.renan.aws.sqs.cep.model.Endereco;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

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

    // getItem busca direto pela chave - muito mais rapido que fazer scan
    // aqui o cep e a partition key entao faz sentido buscar assim
    public Endereco buscarPorCep(String cep) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("cep", AttributeValue.fromS(cep));

        GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return null;
        }
        return fromMap(response.item());
    }

    public void deletar(String cep) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("cep", AttributeValue.fromS(cep));

        dynamoDb.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(key)
                .build());
    }

    private Map<String, AttributeValue> toMap(Endereco e) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("cep",        AttributeValue.fromS(e.getCep()));
        item.put("logradouro", AttributeValue.fromS(e.getLogradouro()));
        item.put("bairro",     AttributeValue.fromS(e.getBairro()));
        item.put("cidade",     AttributeValue.fromS(e.getCidade()));
        item.put("uf",         AttributeValue.fromS(e.getUf()));
        return item;
    }

    private Endereco fromMap(Map<String, AttributeValue> item) {
        Endereco e = new Endereco();
        e.setCep(item.get("cep").s());
        e.setLogradouro(item.get("logradouro").s());
        e.setBairro(item.get("bairro").s());
        e.setCidade(item.get("cidade").s());
        e.setUf(item.get("uf").s());
        return e;
    }
}
