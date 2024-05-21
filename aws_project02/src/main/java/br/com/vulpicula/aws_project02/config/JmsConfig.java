package br.com.vulpicula.aws_project02.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import javax.jms.Session;

@Configuration
@EnableJms // Habilita o suporte ao JMS no Spring
public class JmsConfig {

    @Value("${aws.region}") // Injeta o valor da região AWS a partir das propriedades
    private String awsRegion;

    private SQSConnectionFactory sqsConnectionFactory;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        // Configura a conexão SQS
        sqsConnectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withRegion(awsRegion) // Configura a região AWS
                        .withCredentials(new DefaultAWSCredentialsProviderChain()) // Configura as credenciais AWS
                        .build());

        // Configuração da fábrica de contêiner de ouvintes JMS
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsConnectionFactory); // Define a fábrica de conexões SQS
        factory.setDestinationResolver(new DynamicDestinationResolver()); // Define o resolvedor de destino dinâmico
        factory.setConcurrency("2"); // Configura a concorrência
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE); // Define o modo de reconhecimento de sessão

        return factory;
    }
}
