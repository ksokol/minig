package fr.aliasource.webmail.client.shared;

public interface ICreateFolderRequest {

    String getFolder();

    void setFolder(String folder);

    String getParentFolder();

    void setParentFolder(String parent);

}
