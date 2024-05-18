package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class VpcStack extends Stack {
    private Vpc vpc;

    // Construtor que aceita scope e id, delegando para o outro construtor com props como null
    public VpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    // Construtor principal que inicializa a stack com scope, id e props
    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Cria uma VPC com configurações padrão
        vpc = Vpc.Builder.create(this, "Vpc01")
                .maxAzs(3) // Define o número máximo de zonas de disponibilidade
                .build();
    }

    // Método para obter a VPC criada nesta stack
    public Vpc getVpc() {
        return vpc;
    }
}
