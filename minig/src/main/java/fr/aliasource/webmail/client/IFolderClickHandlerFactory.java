package fr.aliasource.webmail.client;

import com.google.gwt.event.dom.client.ClickHandler;

import fr.aliasource.webmail.client.shared.IFolder;

public interface IFolderClickHandlerFactory {

    ClickHandler createHandler(IFolder f);

}
