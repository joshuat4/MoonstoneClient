package com.moonstone.ezmaps_app;

import android.support.annotation.NonNull;
import com.google.firebase.Timestamp;

import java.util.Date;

public class EzMessage implements Comparable<EzMessage>{

    private String messageId;
    private String text;
    private String toUserId;
    private String fromUserId;
    private Date time;
    private String attachmentUrl;

    public EzMessage(String text, String toUserId, String fromUserId, Date time){
        this.text = text;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.time = time;
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

    public Date getTime() {
        return time;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    @Override
    public int compareTo(@NonNull EzMessage e) {
        return time.compareTo(e.getTime());
    }
}
