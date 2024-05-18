package br.com.vulpicula.aws_project01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Annotation que indica que esta é uma classe de configuração Spring Boot
@SpringBootApplication
public class AwsProject01Application {

	// Método principal que inicia a aplicação Spring Boot
	public static void main(String[] args) {
		// Executa o método SpringApplication.run para iniciar a aplicação
		SpringApplication.run(AwsProject01Application.class, args);
	}

}
