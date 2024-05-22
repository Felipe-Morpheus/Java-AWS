package br.com.vulpicula.aws_project01.consumer;

import br.com.vulpicula.aws_project01.model.Invoice;
import br.com.vulpicula.aws_project01.model.SnsMessage;
import br.com.vulpicula.aws_project01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class InvoiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(
            InvoiceConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private AmazonS3 amazonS3;

    // Método responsável por ouvir mensagens do SQS e processar eventos do S3
    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receiverS3Event(TextMessage textMessage)
            throws JMSException, IOException {
        // Converte a mensagem do SQS em um objeto SnsMessage
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(),
                SnsMessage.class);

        // Converte a mensagem SNS em um objeto S3EventNotification
        S3EventNotification s3EventNotification = objectMapper
                .readValue(snsMessage.getMessage(), S3EventNotification.class);

        // Processa as notificações de fatura do S3
        processInvoiceNotification(s3EventNotification);
    }

    // Método para processar notificações de fatura do S3
    private void processInvoiceNotification(S3EventNotification s3EventNotification)
            throws IOException {
        for (S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord : s3EventNotification.getRecords()) {
            // Obtém informações sobre o bucket e a chave do objeto do evento S3
            S3EventNotification.S3Entity s3Entity = s3EventNotificationRecord.getS3();
            String bucketName = s3Entity.getBucket().getName();
            String objectKey = s3Entity.getObject().getKey();

            // Baixa o arquivo do S3 e converte para uma string
            String invoiceFile = downloadObject(bucketName, objectKey);

            // Converte a string do arquivo em um objeto Invoice
            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            log.info("Invoice received: {}", invoice.getInvoiceNumber());

            // Salva a fatura no repositório
            invoiceRepository.save(invoice);

            // Deleta o objeto do S3 após o processamento
            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    // Método para baixar um objeto do S3 e convertê-lo para uma string
    private String downloadObject(String bucketName, String objectKey)
            throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent()));
        String content = null;
        while ((content = bufferedReader.readLine()) != null) {
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }
}
