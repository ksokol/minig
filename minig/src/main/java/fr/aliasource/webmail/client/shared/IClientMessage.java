package fr.aliasource.webmail.client.shared;

import java.util.Date;
import java.util.List;

public interface IClientMessage {

    public abstract String getId();

    public abstract String getFolder();

    public abstract String getSubject();

    public abstract IBody getBody();

    public abstract IEmailAddress getSender();

    public abstract Date getDate();

    public abstract void setSubject(String subject);

    public abstract void setFolder(String folder);

    public abstract void setId(String id);

    public abstract void setMessageId(String messageId);

    public abstract void setBody(IBody body);

    public abstract void setSender(IEmailAddress sender);

    public abstract void setDate(Date date);

    public abstract String getMailer();

    public abstract void setMailer(String mailer);

    public abstract String getFolderName();

    public abstract void setFolderName(String folderName);

    public abstract Boolean getForwarded();

    public abstract List<IEmailAddress> getTo();

    public abstract void setTo(List<IEmailAddress> to);

    public abstract List<IEmailAddress> getCc();

    public abstract void setCc(List<IEmailAddress> cc);

    public abstract List<IEmailAddress> getBcc();

    public abstract void setBcc(List<IEmailAddress> bcc);

    public abstract void setRead(boolean read);

    public abstract Boolean getRead();

    public abstract Boolean getStarred();

    public abstract void setStarred(boolean starred);

    public abstract Boolean getAnswered();

    public abstract void setAnswered(boolean answered);

    public abstract Boolean getHighPriority();

    public abstract void setHighPriority(boolean highPriority);

    public abstract boolean isLoaded();

    public abstract void setLoaded(boolean loaded);

    public abstract List<IEmailAddress> getDispositionNotification();

    public abstract void setDispositionNotification(List<IEmailAddress> dispositionNotification);

    public boolean isAskForDispositionNotification();

    public void setAskForDispositionNotification(boolean askForDispositionNotification);

    public abstract List<AttachmentId> getAttachments();

    public abstract void setAttachments(List<AttachmentId> attachments);

    public abstract void setReceipt(Boolean receipt);

    public abstract Boolean getReceipt();

    public abstract Boolean getDeleted();

}