package br.com.vulpicula.aws_project01.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Anotação para indicar que esta classe é uma configuração do Spring
@Configuration
// Anotação para especificar que esta configuração será usada apenas no perfil "local"
@Profile("local")
public class SnsCreate {

    // Declaração do logger estático final para registro de informações
    private static final Logger LOG = LoggerFactory.getLogger(SnsCreate.class);

    // Atributos finais para armazenar o tópico do evento do produto e o cliente Amazon SNS
    private final String productEventsTopic;
    private final AmazonSNS snsClient;

    // Construtor da classe
    public SnsCreate() {
        // Configuração do cliente Amazon SNS para o ambiente local
        this.snsClient = AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566",
                        Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        // Criação de um tópico de evento do produto usando o cliente Amazon SNS
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("product-events");
        this.productEventsTopic = this.snsClient.createTopic(createTopicRequest).getTopicArn();

        // Registro de informações sobre o ARN do tópico criado
        LOG.info("SNS topic ARN:  {}", this.productEventsTopic);
    }

    // Método para expor o cliente Amazon SNS como um bean gerenciado pelo Spring
    @Bean
    public AmazonSNS snsClient() {
        return this.snsClient;
    }

    // Método para expor o tópico de eventos do produto como um bean gerenciado pelo Spring
    @Bean(name = "productEventsTopic")
    public Topic snsProductEventsTopic() {
        return new Topic().withTopicArn(productEventsTopic); // Cria e retorna um objeto Topic com o ARN especificado
    }
}
