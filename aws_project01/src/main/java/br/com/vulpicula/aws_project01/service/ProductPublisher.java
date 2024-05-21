package br.com.vulpicula.aws_project01.service;

import br.com.vulpicula.aws_project01.enums.EventType;
import br.com.vulpicula.aws_project01.model.Envelope;
import br.com.vulpicula.aws_project01.model.Product;
import br.com.vulpicula.aws_project01.model.ProductEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProductPublisher {

    // Define o logger estático para esta classe
    private static Logger LOG = LoggerFactory.getLogger(ProductPublisher.class);

    // Declaração das dependências
    private final AmazonSNS snsClient; // Cliente SNS da AWS
    private final Topic productEventsTopic; // Tópico SNS de eventos de produto
    private final ObjectMapper objectMapper; // Objeto ObjectMapper para conversão de objetos Java para JSON

    // Construtor da classe ProductPublisher
    public ProductPublisher(AmazonSNS snsClient,
                            @Qualifier("productEventsTopic") Topic productEventsTopic,
                            ObjectMapper objectMapper) {
        // Inicializa as dependências
        this.snsClient = snsClient; // Inicializa o cliente SNS
        this.productEventsTopic = productEventsTopic; // Inicializa o tópico SNS
        this.objectMapper = objectMapper; // Inicializa o ObjectMapper
    }

    /**
     * Publishes product events to the SNS topic.
     *
     * @param product   The product for which the event is being published.
     * @param eventType The type of event (CREATE, UPDATE, DELETE).
     * @param username  The username of the user initiating the event.
     */
    public void publishProductEvents(Product product, EventType eventType, String username) {
        // Cria um objeto ProductEvent com os detalhes relevantes
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setCode(product.getCode());
        productEvent.setUsername(username);

        // Cria um objeto Envelope para envolver o ProductEvent
        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);

        try {
            // Converte os objetos Envelope e ProductEvent em strings JSON
            envelope.setData(objectMapper.writeValueAsString(productEvent));

            // Publica a mensagem JSON no tópico SNS
            snsClient.publish(productEventsTopic.getTopicArn(), objectMapper.writeValueAsString(envelope));

        } catch (JsonProcessingException e) {
            // Registra um erro se houver algum problema ao criar a mensagem JSON
            LOG.error("Failed to create product event message");
        }
    }
}
