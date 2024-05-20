package br.com.vulpicula.aws_project01.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Define a classe como uma configuração do Spring
@Configuration
@Profile("/!local") // Define quando essa classe deve ser executada, ou seja, só quando for diferente de local nome dado a run configuration
public class SnsConfig {

    // Injeta o valor da propriedade 'aws.region' do arquivo de configuração
    @Value("${aws.region}")
    private String awsRegion;

    // Injeta o valor da propriedade 'aws.sns.topic.product-events.arn' do arquivo de configuração
    @Value("${aws.sns.topic.product-events.arn}")
    private String productEventsTopic;

    // Define um bean gerenciado pelo Spring que cria e configura um cliente Amazon SNS
    @Bean
    public AmazonSNS snsClient(){
        return AmazonSNSClientBuilder.standard()
                .withRegion(awsRegion) // Define a região do cliente SNS
                .withCredentials(new DefaultAWSCredentialsProviderChain()) // Define as credenciais usando o provedor de credenciais padrão da AWS
                .build(); // Constrói e retorna o cliente SNS configurado
    }

    // Método para criar um objeto Topic do Amazon SNS com o ARN especificado
    @Bean(name = "productEventsTopic")
    public Topic snsProductEventsTopic() {
        return new Topic().withTopicArn(productEventsTopic); // Cria e retorna um objeto Topic com o ARN especificado
    }

}
