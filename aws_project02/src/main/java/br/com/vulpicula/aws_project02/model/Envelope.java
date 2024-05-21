package br.com.vulpicula.aws_project02.model;

import br.com.vulpicula.aws_project02.enums.EventType;

public class Envelope {

    private EventType eventType;
    private String data;

    public EventType EventType() {
        return eventType;
    }

    public void EventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
