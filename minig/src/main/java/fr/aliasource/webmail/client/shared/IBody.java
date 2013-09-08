package fr.aliasource.webmail.client.shared;

public interface IBody {

    public abstract String getHtml();

    public abstract void setHtml(String html);

    public abstract String getPlain();

    public abstract void setPlain(String plain);

}