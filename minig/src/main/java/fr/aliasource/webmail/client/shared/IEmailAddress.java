package fr.aliasource.webmail.client.shared;

public interface IEmailAddress {

    public abstract String getDisplayName();

    public abstract String getEmail();

    public abstract void setDisplayName(String display);

    public abstract void setEmail(String email);

}