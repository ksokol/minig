package fr.aliasource.webmail.client.shared;

public interface IFolder {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getName();

	public abstract Boolean getSubscribed();

	public abstract void setSubscribed(Boolean subscribed);

	public abstract Boolean getEditable();

	public abstract void setEditable(Boolean editable);

	public abstract void setName(String displayName);

	public abstract Boolean getShared();

	public abstract void setShared(Boolean shared);

	public abstract Boolean getTrashFolder();

	public abstract void setTrashFolder(Boolean trashFolder);

	public abstract String getParentFolderId();

	public abstract void setParentFolderId(String parentId);
}