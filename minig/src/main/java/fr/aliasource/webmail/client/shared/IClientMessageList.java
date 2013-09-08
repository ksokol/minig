package fr.aliasource.webmail.client.shared;

import java.util.List;

public interface IClientMessageList {

    public int getFullLength();

    public int getPage();

    public List<IClientMessage> getMailList();

    public void setMailList(List<IClientMessage> mailList);

}