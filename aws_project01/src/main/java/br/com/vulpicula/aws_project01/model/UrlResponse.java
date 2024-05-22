package br.com.vulpicula.aws_project01.model;

public class UrlResponse {

    private String url;

    private long experationTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getExperationTime() {
        return experationTime;
    }

    public void setExperationTime(long experationTime) {
        this.experationTime = experationTime;
    }
}
