package br.com.vulpicula.aws_project01.controller;

import br.com.vulpicula.aws_project01.model.Invoice;
import br.com.vulpicula.aws_project01.model.UrlResponse;
import br.com.vulpicula.aws_project01.repository.InvoiceRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.Response;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    // Obtém o nome do bucket S3 do arquivo de propriedades
    @Value(("${s3-invoice-events}"))
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private InvoiceRepository invoiceRepository;

    // Endpoint para criar uma URL de upload de fatura com tempo de expiração de 5 minutos
    @PostMapping
    public ResponseEntity<UrlResponse>createInvoiceUrl(){
        UrlResponse urlResponse = new UrlResponse();
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(5));
        String processId = UUID.randomUUID().toString();

        // Define a URL gerada e o tempo de expiração na resposta
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processId)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(Date.from(expirationTime));

        urlResponse.setExperationTime(expirationTime.getEpochSecond());
        urlResponse.setUrl(amazonS3.generatePresignedUrl(
                generatePresignedUrlRequest).toString());


        return new ResponseEntity<UrlResponse>(urlResponse, HttpStatus.OK);
    }

    // Endpoint para buscar todas as faturas
    @GetMapping
    public Iterable<Invoice> findAll(){
        return invoiceRepository.findAll();
    }

    // Endpoint para buscar faturas pelo nome do cliente
    @GetMapping(path = "/bycustomername")
    public Iterable<Invoice> findByCustomerName(@RequestParam String customerName){
         return invoiceRepository.findAllByCustomerName(customerName);
    }
}
