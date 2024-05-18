package br.com.vulpicula.aws_project01.controller;

import br.com.vulpicula.aws_project01.model.Product;
import br.com.vulpicula.aws_project01.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/api/products") // Define o caminho base para as requisições deste controlador
public class ProductController {

    private ProductRepository productRepository; // Injeção de dependência do repositório

    @Autowired // Indica que a injeção de dependência deve ser feita automaticamente pelo Spring
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping // Mapeia requisições GET para a raiz do caminho base (/api/products)
    public Iterable<Product> findAll() {
        return productRepository.findAll(); // Retorna todos os produtos do banco de dados
    }

    @GetMapping("/{id}") // Mapeia requisições GET para o caminho /api/products/{id}
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado com status 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found se o produto não for encontrado
        }
    }

    @PostMapping // Mapeia requisições POST para o caminho /api/products
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product); // Salva o produto no banco de dados

        return ResponseEntity.status(HttpStatus.CREATED).body(productCreated); // Retorna o produto criado com status 201 Created
    }

    @PutMapping("/{id}") // Mapeia requisições PUT para o caminho /api/products/{id}
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
        if (productRepository.existsById(id)) {
            product.setId(id); // Define o ID do produto com o ID da requisição

            Product productUpdated = productRepository.save(product); // Atualiza o produto no banco de dados

            return ResponseEntity.ok(productUpdated); // Retorna o produto atualizado com status 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found se o produto não for encontrado
        }
    }

    @DeleteMapping(path = "/{id}") // Mapeia requisições DELETE para o caminho /api/products/{id}
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();

            productRepository.delete(product); // Deleta o produto do banco de dados

            return new ResponseEntity<Product>(product, HttpStatus.OK); // Retorna o produto deletado com status 200 OK
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna status 404 Not Found se o produto não for encontrado
        }
    }

    @GetMapping(path = "/bycode") // Mapeia requisições GET para o caminho /api/products/bycode
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code); // Busca um produto pelo código
        if (optProduct.isPresent()) {
            return ResponseEntity.ok(optProduct.get()); // Retorna o produto encontrado com status 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found se o produto não for encontrado
        }
    }
}
