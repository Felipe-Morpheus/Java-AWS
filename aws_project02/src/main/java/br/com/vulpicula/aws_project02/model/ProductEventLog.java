package br.com.vulpicula.aws_project02.model;

import br.com.vulpicula.aws_project02.enums.EventType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import org.springframework.data.annotation.Id;

// Anotação que mapeia a classe para a tabela DynamoDB chamada "product-events"
@DynamoDBTable(tableName = "product-events")
public class ProductEventLog {
    public ProductEventLog() {}

    // O campo que representa a chave primária da tabela
    @Id
    private ProductEventKey productEventKey;

    // Enumeração convertida para tipo DynamoDB
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "eventType")
    private EventType eventType;

    // Atributo do produto
    @DynamoDBAttribute(attributeName = "productId")
    private long productId;

    // Nome do usuário associado ao evento
    @DynamoDBAttribute(attributeName = "username")
    private String username;

    // Carimbo de data/hora do evento
    @DynamoDBAttribute(attributeName = "timestamp")
    private String timestamp;

    // Tempo de vida do registro na tabela
    @DynamoDBAttribute(attributeName = "ttl")
    private long ttl;

    // Método getter para o atributo "pk" (partition key)
    @DynamoDBAttribute(attributeName = "pk")
    public String getPk() {
        return this.productEventKey != null ? this.productEventKey.getPk() : null;
    }

    // Método setter para o atributo "pk" (partition key)
    public void setPk(String pk) {
        if (this.productEventKey == null) {
            this.productEventKey = new ProductEventKey();
        }
        this.productEventKey.setPk(pk);
    }

    // Método getter para o atributo "sk" (sort key)
    @DynamoDBAttribute(attributeName = "sk")
    public String getSk() {
        return this.productEventKey != null ? this.productEventKey.getSk() : null;
    }

    // Método setter para o atributo "sk" (sort key)
    public void setSk(String sk) {
        if (this.productEventKey == null) {
            this.productEventKey = new ProductEventKey();
        }
        this.productEventKey.setPk(sk);
    }

    //GETTERS E SETTERS
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
