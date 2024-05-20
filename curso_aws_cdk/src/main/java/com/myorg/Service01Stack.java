package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
    // Construtor que aceita scope, id e cluster, delegando para o outro construtor com props como null
    public Service01Stack(final Construct scope, final String id, Cluster cluster, SnsTopic productEventsTopic) {
        this(scope, id, null, cluster, productEventsTopic);
    }

    // Construtor principal que inicializa a stack com scope, id, props e cluster
    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic productEventsTopic) {
        super(scope, id, props);

        // Define variáveis de ambiente para a aplicação
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://" + Fn.importValue("rds-endpoint")
                + ":5432/aws_project01"); // URL do PostgreSQL
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin"); // Nome de usuário do PostgreSQL
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password")); // Senha do PostgreSQL
        envVariables.put("AWS_REGION", "us-east-1"); // Região da AWS
        envVariables.put("AWS_SNS_TOPIC_PRODUCT_EVENTS_ARN", productEventsTopic.getTopic().getTopicArn());

        // Cria um serviço Fargate com balanceador de carga
        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
                .serviceName("service01")
                .cluster(cluster) // Cluster ECS onde o serviço será executado
                .cpu(512) // CPU alocada para o serviço
                .memoryLimitMiB(1024) // Memória alocada para o serviço
                .desiredCount(2) // Número desejado de instâncias de tarefa
                .listenerPort(8080) // Porta do listener do ALB
                .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                        .containerName("aws-project01")
                        .image(ContainerImage.fromRegistry("vulpicula/curso_aws_project01:1.0.0")) // Imagem do container
                        .containerPort(8080) // Porta do container
                        .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                        .logGroupName("Service01") // Nome do grupo de logs
                                        .removalPolicy(RemovalPolicy.DESTROY) // Política de remoção do grupo de logs
                                        .build())
                                .streamPrefix("Service01")
                                .build()))
                        .environment(envVariables) // Variáveis de ambiente
                        .build())
                .publicLoadBalancer(true) // Torna o load balancer público
                .build();

        // Configura a verificação de integridade do serviço
        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health") // Caminho para a verificação de integridade
                .port("8080") // Porta para a verificação de integridade
                .healthyHttpCodes("200") // Código HTTP considerado saudável
                .build());

        // Configura o auto scaling baseado na utilização da CPU
        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(2) // Capacidade mínima
                .maxCapacity(4) // Capacidade máxima
                .build());

        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50) // Utilização de CPU alvo
                .scaleInCooldown(Duration.seconds(60)) // Tempo de resfriamento para scale-in
                .scaleOutCooldown(Duration.seconds(60)) // Tempo de resfriamento para scale-out
                .build());

        // Concede permissão para publicar no tópico SNS
        productEventsTopic.getTopic().grantPublish(service01.getTaskDefinition().getTaskRole());
    }
}
