package br.com.vulpicula.aws_project01.repository;

import br.com.vulpicula.aws_project01.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Anotação para indicar que essa interface é um repositório do Spring
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Método para buscar um produto pelo código, utilizando a convenção de nomenclatura do Spring Data
    Optional<Product> findByCode(String code);
}
