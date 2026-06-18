package com.renan.helloworld.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renan.helloworld.model.Endereco;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ViaCepService {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/%s/json/";

    // usando o HttpClient nativo do Java 11+ pra nao precisar de dependencia extra
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Endereco buscar(String cep) {
        try {
            String url = String.format(VIA_CEP_URL, cep);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode json = objectMapper.readTree(response.body());

            // quando o cep nao existe a API retorna {"erro": true} em vez de 404
            if (json.has("erro")) {
                throw new RuntimeException("CEP não encontrado: " + cep);
            }

            return new Endereco(
                    json.get("cep").asText(),
                    json.get("logradouro").asText(),
                    json.get("bairro").asText(),
                    json.get("localidade").asText(), // no json e "localidade" mas salvo como cidade
                    json.get("uf").asText()
            );

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar ViaCEP: " + e.getMessage(), e);
        }
    }
}
