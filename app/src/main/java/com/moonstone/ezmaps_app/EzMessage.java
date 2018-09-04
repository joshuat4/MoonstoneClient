package com.moonstone.ezmaps_app;

public class EzMessage {

    private String messageId;
    private String text;
    private String toUserId;
    private String fromUserId;
    private String attachmentUrl;

    public EzMessage(String text, String toUserId, String fromUserId, String attachmentUrl){
        this.text = text;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.attachmentUrl = attachmentUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getText() {
        return text;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

}
