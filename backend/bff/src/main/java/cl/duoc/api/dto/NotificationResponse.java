package cl.duoc.api.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private String id;
    private String recipient;
    private String channel;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;

    public NotificationResponse() {
    }

    public NotificationResponse(String id, String recipient, String channel, 
                               String subject, String message, String status, LocalDateTime sentAt) {
        this.id = id;
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
