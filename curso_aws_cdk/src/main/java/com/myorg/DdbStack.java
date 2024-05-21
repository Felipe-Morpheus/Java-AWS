package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class DdbStack extends Stack {

    private final Table productEventsDdb;

    // Construtor(1) que aceita scope e id, delegando para o outro construtor com props como null
    public DdbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    // Construtor(2) principal que inicializa a stack com scope, id e props
    public DdbStack(final Construct scope, final String id, final StackProps props) {
        // Chama o construtor da classe pai (Stack) para inicializar a stack
        super(scope, id, props);

        // Cria uma tabela DynamoDB para eventos de produtos
        productEventsDdb = Table.Builder.create(this, "ProductEventsDynamoDb")
                .tableName("product-events-")
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .partitionKey(Attribute.builder()
                        .name("pk")
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name("sk")
                        .type(AttributeType.STRING)
                        .build())
                .timeToLiveAttribute("ttl")
                .removalPolicy(RemovalPolicy.DESTROY) // Política de remoção: destruir a tabela quando o stack é destruído
                .build();
    }

    // Método para obter a tabela DynamoDB de eventos de produtos
    public Table getProductEventDynamoDb() {
        return productEventsDdb;
    }
}
