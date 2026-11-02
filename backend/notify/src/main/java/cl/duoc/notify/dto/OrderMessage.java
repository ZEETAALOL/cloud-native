package cl.duoc.notify.dto;

import java.io.Serializable;

public class OrderMessage implements Serializable {
    
    private String type;
    private String orderId;
    private String recipient;
    private String subject;
    private String body;
    private String timestamp;

    public OrderMessage() {
    }

    public OrderMessage(String type, String orderId, String recipient, String subject, String body, String timestamp) {
        this.type = type;
        this.orderId = orderId;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
