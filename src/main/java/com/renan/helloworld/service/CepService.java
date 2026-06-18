package com.renan.helloworld.service;

import com.renan.helloworld.model.Endereco;
import com.renan.helloworld.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class CepService {

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

    // Envia o CEP como mensagem para a fila SQS.
    // O processamento acontece de forma assíncrona — quem chamou não espera.
    public String enviarCep(String cep) {
        SendMessageResponse response = sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(cep)
                .build());

        return "CEP enviado para a fila. MessageId: " + response.messageId();
    }

    // Consome mensagens da fila, consulta o ViaCEP e salva no DynamoDB.
    // Chamado manualmente via endpoint — em produção seria um @Scheduled ou listener.
    // waitTimeSeconds(5) = Long Polling: aguarda até 5s por mensagens antes de retornar.
    // Reduz chamadas desnecessárias quando a fila está vazia (mais barato e eficiente).
    public List<String> processarFila() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .visibilityTimeout(30)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();

        return messages.stream().map(message -> {
            String cep = message.body();
            try {
                Endereco endereco = viaCepService.buscar(cep);
                enderecoRepository.salvar(endereco);

                // Deleta a mensagem da fila após processar com sucesso.
                // Se não deletar, ela volta para a fila após o visibilityTimeout.
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build());

                return "CEP " + cep + " processado e salvo.";

            } catch (Exception e) {
                return "CEP " + cep + " FALHOU: " + e.getMessage();
            }
        }).toList();
    }

    // Busca o endereço salvo no DynamoDB pelo CEP
    public Endereco buscarEndereco(String cep) {
        return enderecoRepository.buscarPorCep(cep)
                .orElseThrow(() -> new RuntimeException("CEP não encontrado na base: " + cep));
    }
}
