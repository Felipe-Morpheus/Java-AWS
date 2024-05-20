package br.com.vulpicula.aws_project01.model;

import br.com.vulpicula.aws_project01.enums.EventType;

// Classe para representar um envelope que encapsula dados de um evento
public class Envelope {
    private EventType eventType; // Tipo de evento associado aos dados encapsulados
    private String data; // Dados encapsulados

    // Método getter para obter o tipo de evento
    public EventType getEventType() {
        return eventType;
    }

    // Método setter para definir o tipo de evento
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    // Método getter para obter os dados encapsulados
    public String getData() {
        return data;
    }

    // Método setter para definir os dados encapsulados
    public void setData(String data) {
        this.data = data;
    }
}
