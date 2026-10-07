package cl.duoc.notify.dto;

import java.io.Serializable;

public class EmailMessage implements Serializable {
    private String type;
    private String recipient;
    private String subject;
    private String body;
    private String orderId;
    private String timestamp;

    public EmailMessage() {
    }

    public EmailMessage(String type, String recipient, String subject, String body, String orderId, String timestamp) {
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.orderId = orderId;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "type='" + type + '\'' +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", orderId='" + orderId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
