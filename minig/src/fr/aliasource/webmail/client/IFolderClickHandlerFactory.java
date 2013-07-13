package fr.aliasource.webmail.client;

import com.google.gwt.event.dom.client.ClickHandler;

import fr.aliasource.webmail.client.shared.Folder;

public interface IFolderClickHandlerFactory {

	ClickHandler createHandler(Folder f);
	
}
