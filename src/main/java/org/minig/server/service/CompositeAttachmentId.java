package org.minig.server.service;

public class CompositeAttachmentId extends CompositeId {

    private String fileName;

    public CompositeAttachmentId() {
    }

    public CompositeAttachmentId(String id) {
        setId(id);
    }

    public CompositeAttachmentId(String folder, String messageId, String fileName) {
        super(folder, messageId);
        this.fileName = fileName;
        buildId();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (fileName != null) {
            this.fileName = fileName;
        }
    }

    public void setCompositeAttachmentId(CompositeAttachmentId id) {
        setId(id.getId());
        setMessageId(id.getMessageId());
        setFolder(id.getFolder());
    }

    @Override
    public void setId(String id) {
        super.setId(id);

        if (id != null && fileName == null) {
            String[] split = id.split("\\" + SEPARATOR);

            if (split != null && split.length > 2) {
                fileName = split[2];
                buildId();
            }
        }
    }

    protected void buildId() {
        if (id == null && fileName != null) {
            super.buildId();

            if (id != null) {
                id = id + SEPARATOR + fileName;
            }
        }
    }

    @Override
    public String toString() {
        return getId();
    }
}
