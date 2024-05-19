package br.com.vulpicula.aws_project01.model;

import jakarta.persistence.*;

// Anotação para definir a tabela no banco de dados e suas restrições
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Entity // Anotação para indicar que essa classe é uma entidade JPA
public class Product {

    @Id // Anotação para identificar chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia de autoincremento para PostgreSQL
    private Long id; // Identificador único do produto

    @Column(length = 32, nullable = false) // Anotação para configurar a coluna 'name' no banco de dados
    private String name; // Nome do produto

    @Column(length = 24, nullable = false) // Anotação para configurar a coluna 'model' no banco de dados
    private String model; // Modelo do produto

    @Column(length = 8, nullable = false) // Anotação para configurar a coluna 'code' no banco de dados
    private String code; // Código do produto, com restrição de unicidade

    private float price; // Preço do produto

    @Column(length = 16, nullable = false) // Anotação para configurar a coluna 'color' no banco de dados
    private String color; // Cor do produto

    // Métodos getters e setters para acessar e modificar os atributos privados da entidade
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
