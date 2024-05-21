package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.constructs.Construct;
import software.amazon.awscdk.services.sqs.Queue;

import java.util.HashMap;
import java.util.Map;

public class Service02Stack extends Stack {

    // Construtor(1) que aceita scope, id e cluster, delegando para o outro construtor com props como null
    public Service02Stack(final Construct scope, final String id, Cluster cluster, SnsTopic productEventsTopic, Table productEventsDdb) {
        this(scope, id, null, cluster, productEventsTopic, productEventsDdb);
    }

    // Construtor(2) principal que inicializa a stack com scope, id, props e cluster
    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic productEventsTopic,Table productEventsDdb) {
        super(scope, id, props);

        // Cria uma fila SQS para eventos do produto
        Queue productEventsDlq = Queue.Builder.create(this, "ProductEventsDlq")
                .queueName("product-events-dlq") // Define o nome da fila
                .build(); // Cria a fila

        // Configuração da fila de redirecionamento de mensagens mortas (DLQ)
        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(productEventsDlq) // Define a fila para redirecionamento
                .maxReceiveCount(3) // Define o número máximo de recebimentos antes do redirecionamento
                .build();

        // Cria uma fila SQS para eventos do produto
        Queue productEventsQueuee = Queue.Builder.create(this, "ProductEvents")
                .queueName("product-events")// Define o nome da fila
                .deadLetterQueue(deadLetterQueue)
                .build();  // Cria a fila

        // Cria uma assinatura SQS para receber mensagens do tópico SNS de eventos do produto
        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(productEventsQueuee).build();
        productEventsTopic.getTopic().addSubscription(sqsSubscription);

        // Define variáveis de ambiente para a aplicação
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("AWS_REGION", "us-east-1");
        envVariables.put("AWS_SQS_QUEUE_PRODUCT_EVENTS_NAME", productEventsQueuee.getQueueName());

        // Cria um serviço Fargate com balanceador de carga
        ApplicationLoadBalancedFargateService service02 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
                .serviceName("service02") // Define o nome do serviço
                .cluster(cluster) // Define o cluster ECS onde o serviço será executado
                .cpu(512) // CPU alocada para o serviço
                .memoryLimitMiB(1024) // Memória alocada para o serviço
                .desiredCount(2) // Número desejado de instâncias de tarefa
                .listenerPort(9090) // Porta do listener do ALB
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .containerName("aws-project01")
                        .image(ContainerImage.fromRegistry("vulpicula/curso_aws_project01:1.0.0")) // Imagem do container
                        .containerPort(9090) // Porta do container
                        .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
                                        .logGroupName("Service02") // Nome do grupo de logs
                                        .removalPolicy(RemovalPolicy.DESTROY) // Política de remoção do grupo de logs
                                        .build())
                                .streamPrefix("Service02")
                                .build()))
                        .environment(envVariables) // Variáveis de ambiente
                        .build())
                .publicLoadBalancer(true) // Torna o load balancer público
                .build();

        // Configura a verificação de integridade do serviço
        service02.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health") // Caminho para a verificação de integridade
                .port("9090") // Porta para a verificação de integridade
                .healthyHttpCodes("200") // Código HTTP considerado saudável
                .build());

        // Configura o auto scaling baseado na utilização da CPU
        ScalableTaskCount scalableTaskCount = service02.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(2) // Capacidade mínima
                .maxCapacity(4) // Capacidade máxima
                .build());

        scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50) // Utilização de CPU alvo
                .scaleInCooldown(Duration.seconds(60)) // Tempo de resfriamento para scale-in
                .scaleOutCooldown(Duration.seconds(60)) // Tempo de resfriamento para scale-out
                .build());

        // Concede permissão para publicar no tópico SNS
        productEventsQueuee.grantConsumeMessages(service02.getTaskDefinition().getTaskRole());
        productEventsDdb.grantReadWriteData(service02.getTaskDefinition().getTaskRole());
    }
}
