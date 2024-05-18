package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class CursoAwsCdkStack extends Stack {
    // Construtor que aceita scope e id, delegando para o outro construtor com props como null
    public CursoAwsCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    // Construtor principal que inicializa a stack com scope, id e props
    public CursoAwsCdkStack(final Construct scope, final String id, final StackProps props) {
        // Chama o construtor da classe pai (Stack) para inicializar a stack
        super(scope, id, props);

        // A partir daqui, você pode adicionar os recursos que deseja na stack
        // Por exemplo, criar instâncias EC2, buckets S3, etc.
    }
}
