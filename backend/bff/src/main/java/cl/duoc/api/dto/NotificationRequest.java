package cl.duoc.api.dto;

public class NotificationRequest {
    private String recipient;
    private String channel;
    private String subject;
    private String message;

    public NotificationRequest() {
    }

    public NotificationRequest(String recipient, String channel, String subject, String message) {
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.message = message;
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
}
