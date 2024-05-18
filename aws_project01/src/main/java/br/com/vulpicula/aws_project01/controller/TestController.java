package br.com.vulpicula.aws_project01.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Define que esta classe é um controlador REST
@RestController
// Define o mapeamento de requisições para '/api/test' neste controlador
@RequestMapping("/api/test")
public class TestController {
    // Define um logger estático para a classe usando SLF4J
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    // Define um endpoint HTTP GET para '/api/test/dog/{name}'
    @GetMapping("/dog/{name}")
    // Define que o valor de 'name' na URL é um parâmetro a ser injetado no método
    public ResponseEntity<?> dogTest(@PathVariable(name = "name") String dogName) {
        // Loga uma mensagem com o nome do cachorro recebido
        LOG.info("Test controller - name: {}", dogName);

        // Retorna uma resposta HTTP 200 OK com uma mensagem contendo o nome do cachorro
        return ResponseEntity.ok("Name: " + dogName);
    }
}
