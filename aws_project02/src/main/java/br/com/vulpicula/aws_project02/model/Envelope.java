package br.com.vulpicula.aws_project02.model;

import br.com.vulpicula.aws_project02.enums.EventyType;

public class Envelope {

    private EventyType eventyType;
    private String data;

    public EventyType getEventyType() {
        return eventyType;
    }

    public void setEventyType(EventyType eventyType) {
        this.eventyType = eventyType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
