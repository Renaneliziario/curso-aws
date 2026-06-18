package com.renan.helloworld.service;

import com.renan.helloworld.model.Endereco;
import com.renan.helloworld.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CepService {

    // URL da fila criada no SQS - gerada quando criei a fila pelo terminal
    private static final String QUEUE_URL =
            "https://sqs.us-east-2.amazonaws.com/213180001857/fila-cep";

    private final SqsClient sqsClient;
    private final ViaCepService viaCepService;
    private final EnderecoRepository enderecoRepository;

    public CepService(SqsClient sqsClient, ViaCepService viaCepService, EnderecoRepository enderecoRepository) {
        this.sqsClient = sqsClient;
        this.viaCepService = viaCepService;
        this.enderecoRepository = enderecoRepository;
    }

    public String enviarCep(String cep) {
        SendMessageResponse response = sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(cep)
                .build());

        return "CEP enviado para a fila. MessageId: " + response.messageId();
    }

    public List<String> processarFila() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10)
                // waitTimeSeconds e o long polling - espera ate 5s por mensagens
                // sem isso ele retorna vazio imediatamente mesmo tendo mensagens chegando
                .waitTimeSeconds(5)
                // visibilityTimeout - enquanto estou processando a mensagem fica invisivel
                // pra outros consumers nao pegarem a mesma mensagem ao mesmo tempo
                .visibilityTimeout(30)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        List<String> resultados = new ArrayList<>();

        for (Message message : messages) {
            String cep = message.body();
            try {
                Endereco endereco = viaCepService.buscar(cep);
                enderecoRepository.salvar(endereco);

                // precisa deletar a mensagem depois de processar
                // se nao deletar ela volta pra fila apos o visibilityTimeout e processa de novo
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build());

                resultados.add("CEP " + cep + " processado e salvo.");

            } catch (Exception e) {
                resultados.add("CEP " + cep + " FALHOU: " + e.getMessage());
            }
        }

        return resultados;
    }

    public Endereco buscarEndereco(String cep) {
        Endereco endereco = enderecoRepository.buscarPorCep(cep);
        if (endereco == null) {
            throw new RuntimeException("CEP não encontrado na base: " + cep);
        }
        return endereco;
    }

    // espia a fila sem processar - as mensagens voltam pra fila depois
    public List<String> peek() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(2)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        List<String> ceps = new ArrayList<>();

        for (Message message : messages) {
            ceps.add(message.body());
        }

        return ceps;
    }

    public void deletarEndereco(String cep) {
        Endereco existente = enderecoRepository.buscarPorCep(cep);
        if (existente == null) {
            throw new RuntimeException("CEP não encontrado na base: " + cep);
        }
        enderecoRepository.deletar(cep);
    }
}
