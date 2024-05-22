package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.notifications.SnsDestination;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class InvoiceAppStack extends Stack {
    // Declaração de variáveis de instância para o bucket e a fila
    private final Bucket bucket;
    private final Queue s3InvoiceQueue;

    // Construtor(1) que aceita scope e id, delegando para o outro construtor com props como null
    public InvoiceAppStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    // Construtor(2) principal que inicializa a stack com scope, id e props
    public InvoiceAppStack(final Construct scope, final String id, final StackProps props) {
        // Chama o construtor da classe pai (Stack) para inicializar a stack
        super(scope, id, props);

        // Cria um tópico SNS para eventos de faturas do S3
        SnsTopic s3InvoiceTopic = SnsTopic.Builder.create(Topic.Builder.create(this,"S3InvoiceTopic")
                        .topicName("s3-invoice-events")
                        .build())
                .build();

        // Cria um bucket S3 para armazenar as faturas
        bucket = Bucket.Builder.create(this, "S301")
                .bucketName("pcs201-invoice")
                .removalPolicy(RemovalPolicy.DESTROY) // Define a política de remoção como "destruir" para o bucket
                .build();

        // Adiciona uma notificação de evento ao bucket para eventos de criação de objeto (PUT)
        bucket.addEventNotification(EventType.OBJECT_CREATED_PUT, new SnsDestination(s3InvoiceTopic.getTopic()));

        // Cria uma fila morta (dead-letter queue) para eventos de faturas do S3
        Queue s3InvoiceDlq = Queue.Builder.create(this, "S3InvoiceDlq")
                .queueName("s3-invoice-events-dlq")
                .build();

        // Configura a fila morta como fila de destino para mensagens que falharam no processamento
        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(s3InvoiceDlq)
                .maxReceiveCount(3) // Define o número máximo de recebimentos antes de uma mensagem ser enviada para a fila morta
                .build();

        // Cria uma fila SQS para eventos de faturas do S3
        s3InvoiceQueue = Queue.Builder.create(this, "S3InvoiceQueue")
                .queueName("s3-invoice-events")
                .deadLetterQueue(deadLetterQueue) // Associa a fila morta à fila principal
                .build();

        // Cria uma assinatura SQS para a fila de eventos de faturas do S3 no tópico SNS
        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(s3InvoiceQueue).build();
        s3InvoiceTopic.getTopic().addSubscription(sqsSubscription); // Adiciona a assinatura ao tópico SNS
    }

    // Métodos de acesso para o bucket e a fila
    public Bucket getBucket() {
        return bucket;
    }

    public Queue getS3InvoiceQueue() {
        return s3InvoiceQueue;
    }
}
