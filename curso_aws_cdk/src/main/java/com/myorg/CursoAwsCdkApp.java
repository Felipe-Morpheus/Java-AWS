package com.myorg;

import software.amazon.awscdk.App;

public class CursoAwsCdkApp {
    public static void main(final String[] args) {
        // Cria uma nova aplicação CDK
        App app = new App();

        // Cria a pilha VpcStack, que gerencia a criação de uma VPC
        VpcStack vpcStack = new VpcStack(app, "Vpc");

        // Cria a pilha ClusterStack, passando a VPC criada pelo VpcStack
        ClusterStack clusterStack = new ClusterStack(app, "Cluster", vpcStack.getVpc());
        // Define uma dependência para garantir que VpcStack seja criada antes de ClusterStack
        clusterStack.addDependency(vpcStack);

        // Cria a pilha RdsStack, passando a VPC criada pelo VpcStack
        RdsStack rdsStack = new RdsStack(app, "Rds", vpcStack.getVpc());
        // Define uma dependência para garantir que VpcStack seja criada antes de RdsStack
        rdsStack.addDependency(vpcStack);

        // Cria a pilha Service01Stack, passando o cluster criado pelo ClusterStack
        Service01Stack service01Stack = new Service01Stack(app, "Service01", clusterStack.getCluster());
        // Define dependências para garantir que ClusterStack e RdsStack sejam criadas antes de Service01Stack
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsStack);

        // Sintetiza a aplicação CDK (gera o CloudFormation necessário)
        app.synth();
    }
}
