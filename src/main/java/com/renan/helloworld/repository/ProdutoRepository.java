package com.renan.helloworld.repository;

import com.renan.helloworld.model.Produto;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// @Repository indica camada de acesso a dados — é um @Component especializado.
// Aqui ficam todas as operações diretas com o DynamoDB via SDK.
@Repository
public class ProdutoRepository {

    // Nome da tabela criada no Console do DynamoDB
    private static final String TABLE = "Produtos";

    private final DynamoDbClient dynamoDb;

    public ProdutoRepository(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    // PutItem — cria ou substitui um item completo na tabela.
    // No DynamoDB não existe INSERT separado de UPDATE — o PutItem faz os dois.
    // Se o id já existir, o item é sobrescrito inteiro.
    public void salvar(Produto produto) {
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(TABLE)
                .item(toMap(produto))
                .build());
    }

    // GetItem — busca um único item pela Partition Key (id).
    // É a operação mais barata e rápida do DynamoDB — O(1) por chave.
    // Retorna Optional para forçar o tratamento do caso "não encontrado".
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

    // Scan — lê TODOS os itens da tabela. Caro em tabelas grandes pois
    // percorre partição por partição. Para produção prefira Query com índices.
    // Para o curso com poucos dados, é suficiente.
    public List<Produto> listarTodos() {
        ScanResponse response = dynamoDb.scan(ScanRequest.builder()
                .tableName(TABLE)
                .build());

        return response.items().stream()
                .map(this::fromMap)
                .toList();
    }

    // DeleteItem — remove um item pela Partition Key.
    // O DynamoDB não retorna erro se o id não existir — por isso validamos
    // a existência antes no Service.
    public void deletar(String id) {
        dynamoDb.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(Map.of("id", AttributeValue.fromS(id)))
                .build());
    }

    // Converte o objeto Java para o formato do DynamoDB.
    // AttributeValue representa cada campo — o tipo deve bater com o DynamoDB:
    // fromS = String | fromN = Number | fromBOOL = Boolean | fromL = List
    private Map<String, AttributeValue> toMap(Produto p) {
        return Map.of(
                "id",        AttributeValue.fromS(p.getId()),
                "nome",      AttributeValue.fromS(p.getNome()),
                // Números são armazenados como String no DynamoDB internamente —
                // toPlainString() evita notação científica (ex: 3.5E+3)
                "preco",     AttributeValue.fromN(p.getPreco().toPlainString()),
                "descricao", AttributeValue.fromS(p.getDescricao())
        );
    }

    // Converte o Map do DynamoDB de volta para o objeto Java
    private Produto fromMap(Map<String, AttributeValue> item) {
        return new Produto(
                item.get("id").s(),
                item.get("nome").s(),
                new BigDecimal(item.get("preco").n()),
                item.get("descricao").s()
        );
    }
}
