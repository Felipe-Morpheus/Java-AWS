package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;
import software.constructs.Construct;

public class SnsStack extends Stack {
    private final SnsTopic productEventTopic;


    // Construtor que aceita scope e id, delegando para o outro construtor com props como null
    public SnsStack(final Construct scope, final String id) {
        this(scope, id, null);}

    // Construtor principal que inicializa a stack com scope, id e props
    public SnsStack(final Construct scope, final String id, final StackProps props){
            // Chama o construtor da classe pai (Stack) para inicializar a stack
            super(scope, id, props);

            productEventTopic = SnsTopic.Builder.create(Topic.Builder.create(this, "ProductEventTopic")
                     .topicName("product-events")
                     .build())
                     .build();

            productEventTopic.getTopic().addSubscription(EmailSubscription.Builder.create("decoder.felix@gmail.com")
                .json(true)
                .build());
    }
       public SnsTopic getProductEventTopic() {
        return productEventTopic;

    }
}