package br.com.vulpicula.aws_project02.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

// Classe que representa a chave primária composta de um item na tabela DynamoDB
public class ProductEventKey {
    public ProductEventKey() {}

    // Atributo que representa a chave de partição (partition key)
    private String pk;

    // Atributo que representa a chave de classificação (sort key)
    private String sk;



    //GETTERS E SETTERS
    @DynamoDBHashKey(attributeName = "pk") // Anotação que indica que este método representa a chave de partição na tabela DynamoDB
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDBRangeKey(attributeName = "sk") // Anotação que indica que este método representa a chave de classificação na tabela DynamoDB
    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
}
