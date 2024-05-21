package br.com.vulpicula.aws_project02.service;

import br.com.vulpicula.aws_project02.model.Envelope;
import br.com.vulpicula.aws_project02.model.ProductEvent;
import br.com.vulpicula.aws_project02.model.SnsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

@Service
public class ProductEventConsumer {
    // Logger para registrar mensagens de log
    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    // ObjectMapper para desserializar mensagens JSON
    private ObjectMapper objectMapper;

    // Construtor que recebe um ObjectMapper (injetado pelo Spring)
    @Autowired
    public ProductEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

        // Aqui você pode adicionar lógica para processar o evento do produto conforme necessário

        // Log do evento recebido
        log.info("Received product event: {}", productEvent);
    }
}
