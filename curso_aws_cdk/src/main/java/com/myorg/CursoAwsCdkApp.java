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

        // Cria a pilha SnsStack, que gerencia a criação de um tópico SNS
        SnsStack snsStack = new SnsStack(app, "Sns");
        InvoiceAppStack invoiceAppStack = new InvoiceAppStack(app, "InvoiceApp");

        // Cria a pilha Service01Stack, passando o cluster criado pelo ClusterStack e o tópico SNS do SnsStack
        Service01Stack service01Stack = new Service01Stack(app, "Service01",
                clusterStack.getCluster(), snsStack.getProductEventTopic(),
                invoiceAppStack.getBucket(), invoiceAppStack.getS3InvoiceQueue());

        // Define dependências para garantir que ClusterStack, RdsStack e SnsStack sejam criadas antes de Service01Stack
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsStack);
        service01Stack.addDependency(snsStack);
        service01Stack.addDependency(invoiceAppStack);
        // Cria a pilha DdbStack, que gerencia a criação de uma tabela DynamoDB
        DdbStack ddbStack = new DdbStack(app, "Ddb");

        // Cria a pilha Service02Stack, passando o cluster criado pelo ClusterStack e o tópico SNS do SnsStack
        Service02Stack service02Stack = new Service02Stack(app, "Service02",
                clusterStack.getCluster(), snsStack.getProductEventTopic(), ddbStack.getProductEventDynamoDb());

        // Define dependências para garantir que ClusterStack, SnsStack e DdbStack sejam criadas antes de Service02Stack
        service02Stack.addDependency(clusterStack);
        service02Stack.addDependency(snsStack);
        service02Stack.addDependency(ddbStack);

        // Sintetiza a aplicação CDK
        app.synth();
    }
}
