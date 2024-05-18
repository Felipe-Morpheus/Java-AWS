package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.constructs.Construct;

public class ClusterStack extends Stack {
    // Variável privada para armazenar a instância do Cluster ECS
    private Cluster cluster;

    // Construtor que aceita scope, id e vpc, delegando para o outro construtor com props como null
    public ClusterStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    // Construtor principal que inicializa a stack com scope, id, props e vpc
    public ClusterStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        // Chama o construtor da classe pai (Stack) para inicializar a stack
        super(scope, id, props);

        // Cria um novo cluster ECS dentro da VPC fornecida
        cluster = Cluster.Builder.create(this, id)
                .clusterName("cluster-01") // Define o nome do cluster
                .vpc(vpc) // Associa o cluster à VPC fornecida
                .build(); // Constrói a instância do cluster
    }

    // Método público para retornar a instância do cluster criado
    public Cluster getCluster() {
        return cluster;
    }
}
