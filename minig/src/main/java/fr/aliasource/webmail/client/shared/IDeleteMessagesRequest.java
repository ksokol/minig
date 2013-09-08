package fr.aliasource.webmail.client.shared;

import java.util.List;

public interface IDeleteMessagesRequest {

    List<String> getMessageIdList();

    void setMessageIdList(List<String> messageIdList);

}
