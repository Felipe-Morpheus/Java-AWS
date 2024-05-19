package br.com.vulpicula.aws_project01.controller;

import br.com.vulpicula.aws_project01.model.Product;
import br.com.vulpicula.aws_project01.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController// Indica que esta classe é um controlador REST
@RequestMapping("/api/products") // Define o caminho base para todos os endpoints neste controlador
public class ProductController {

    @Autowired// Indica que a injeção de dependência deve ser feita automaticamente pelo Spring
    private ProductRepository productRepository; // Injeta uma instância de ProductRepository para acessar os dados do banco

    @Autowired// Indica que a injeção de dependência deve ser feita automaticamente pelo Spring
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository; // Injeta a instância de ProductRepository no construtor
    }

    @GetMapping // Define um endpoint HTTP GET para retornar todos os produtos
    public Iterable<Product> findAll() {
        return productRepository.findAll(); // Chama o método findAll do repository para obter todos os produtos
    }

    @GetMapping("/{id}") // Define um endpoint HTTP GET para retornar um produto por ID
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id); // Busca um produto por ID no repository
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não for encontrado
        }
    }

    @PostMapping // Define um endpoint HTTP POST para salvar um novo produto
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product); // Salva um novo produto no banco de dados
        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated); // Retorna o produto criado com status 201
    }

    @PutMapping("/{id}") // Define um endpoint HTTP PUT para atualizar um produto por ID
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
        Optional<Product> existingProduct = productRepository.findByCode(product.getCode()); // Busca um produto pelo código
        if (existingProduct.isPresent() && existingProduct.get().getId() != id) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna 409 Conflict se o código já existir para outro produto
        }

        if (productRepository.existsById(id)) {
            product.setId(id); // Define o ID do produto a ser atualizado
            Product productUpdated = productRepository.save(product); // Atualiza o produto no banco de dados
            return ResponseEntity.ok(productUpdated); // Retorna o produto atualizado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não for encontrado para atualização
        }
    }

    @DeleteMapping("/{id}") // Define um endpoint HTTP DELETE para excluir um produto por ID
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id); // Busca um produto por ID para exclusão
        if (optProduct.isPresent()) {
            Product product = optProduct.get(); // Obtém o produto encontrado
            productRepository.delete(product); // Exclui o produto do banco de dados
            return new ResponseEntity<>(HttpStatus.OK); // Retorna 200 OK após a exclusão
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 se o produto não for encontrado para exclusão
        }
    }

    @GetMapping("/bycode") // Define um endpoint HTTP GET para retornar um produto por código
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code); // Busca um produto pelo código
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não for encontrado pelo código
        }
    }
}
