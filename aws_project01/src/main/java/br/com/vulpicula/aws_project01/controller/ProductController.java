package br.com.vulpicula.aws_project01.controller;

import br.com.vulpicula.aws_project01.enums.EventType;
import br.com.vulpicula.aws_project01.model.Product;
import br.com.vulpicula.aws_project01.repository.ProductRepository;
import br.com.vulpicula.aws_project01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Indica que esta classe é um controlador REST
@RestController
@RequestMapping("/api/products") // Define o caminho base para todos os endpoints neste controlador
public class ProductController {

    // Indica que a injeção de dependência deve ser feita automaticamente pelo Spring
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductPublisher productPublisher;

    // Define um endpoint HTTP GET para retornar todos os produtos
    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll(); // Chama o método findAll do repository para obter todos os produtos
    }

    // Define um endpoint HTTP GET para retornar um produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id); // Busca um produto por ID no repository
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não for encontrado
        }
    }

    // Define um endpoint HTTP POST para salvar um novo produto
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product); // Salva um novo produto no banco de dados

        // Publica um evento de criação de produto
        productPublisher.publishProductEvents(productCreated, EventType.PRODUCT_CREATED, "matilda");

        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated); // Retorna o produto criado com status 201
    }

    // Define um endpoint HTTP PUT para atualizar um produto por ID
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @RequestBody Product product, @PathVariable("id") long id){
        if (productRepository.existsById(id)) {
            product.setId(id);

            Product productUpdated = productRepository.save(product);

            // Publica um evento de atualização de produto
            productPublisher.publishProductEvents(productUpdated, EventType.PRODUCT_UPDATE, "doralice");

            return new ResponseEntity<Product>(productUpdated,
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Define um endpoint HTTP DELETE para excluir um produto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id); // Busca um produto por ID para exclusão
        if (optProduct.isPresent()) {
            Product product = optProduct.get(); // Obtém o produto encontrado

            // Publica um evento de exclusão de produto
            productPublisher.publishProductEvents(product, EventType.PRODUCT_DELETE, "hannah");

            productRepository.delete(product); // Exclui o produto do banco de dados
            return new ResponseEntity<>(HttpStatus.OK); // Retorna 200 OK após a exclusão
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 se o produto não for encontrado para exclusão
        }
    }

    // Define um endpoint HTTP GET para retornar um produto por código
    @GetMapping("/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code); // Busca um produto pelo código
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não for encontrado pelo código
        }
    }
}
