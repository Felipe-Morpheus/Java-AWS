package br.com.vulpicula.aws_project02.service;

import br.com.vulpicula.aws_project02.model.Envelope;
import br.com.vulpicula.aws_project02.model.ProductEvent;
import br.com.vulpicula.aws_project02.model.ProductEventLog;
import br.com.vulpicula.aws_project02.model.SnsMessage;
import br.com.vulpicula.aws_project02.repository.ProducEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {
    // Logger para registrar mensagens de log
    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    // ObjectMapper para desserializar mensagens JSON
    private final ObjectMapper objectMapper;
    private final ProducEventLogRepository producEventLogRepository;

    // Construtor que recebe um ObjectMapper (injetado pelo Spring)
    @Autowired
    public ProductEventConsumer(ObjectMapper objectMapper, ProducEventLogRepository producEventLogRepository) {
        this.objectMapper = objectMapper;
        this.producEventLogRepository = producEventLogRepository;
    }



    // Método para ouvir mensagens da fila SQS
    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {
        // Desserializa a mensagem SNS recebida em um objeto SnsMessage
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);

        // Desserializa a mensagem do envelope SNS em um objeto Envelope
        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);

        // Desserializa os dados do envelope em um objeto ProductEvent
        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);

        // Log do evento recebido
        log.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                envelope.getEventType(),
                productEvent.getProductId(),
                snsMessage.getMessageId());

        // Construir o objeto ProductEventLog
        ProductEventLog productEventLog = buildProductEventLog(envelope, productEvent);

        // Salva o objeto ProductEventLog no repositório
        producEventLogRepository.save(productEventLog);
    }

    // Método para construir o objeto ProductEventLog
    private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent) {
        // Obtém o timestamp atual em milissegundos
        long timestamp = Instant.now().toEpochMilli();

        ProductEventLog productEventLog = new ProductEventLog();    // Cria um novo objeto ProductEventLog
        productEventLog.setPk(productEvent.getCode());    // Define a chave primária (pk) como o código do evento do produto
        productEventLog.setSk(envelope.getEventType() + "-" + timestamp);   // Define a chave de classificação (sk) como uma combinação do tipo de evento do envelope e o timestamp atual
        productEventLog.setEventType(envelope.getEventType());    // Define o tipo de evento do produto no log
        productEventLog.setProductId(productEvent.getProductId());  // Define o ID do produto no log
        productEventLog.setUsername(productEvent.getUsername()); // Define o nome de usuário associado ao evento no log
        productEventLog.setTimestamp(Long.valueOf(timestamp));  // Define o timestamp do log como uma string representando o timestamp atual
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli()); // Define o TTL (time to live) do log como o timestamp atual mais 10 minutos em milissegundos


        return productEventLog;        // Retorna o objeto ProductEventLog construído

    }
}
