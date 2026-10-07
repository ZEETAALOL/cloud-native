package cl.duoc.notify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;
    private String recipient;
    private String channel;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}
