package cl.duoc.api.service;

import cl.duoc.api.dto.NotificationRequest;
import cl.duoc.api.dto.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class NotifyService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotifyService.class);
    
    private final RestClient restClient;
    
    public NotifyService(@Value("${microservices.notify.base-url}") String notifyBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(notifyBaseUrl)
                .build();
        logger.info("NotifyService inicializado con URL: {}", notifyBaseUrl);
    }
    
    public List<NotificationResponse> getAllNotifications() {
        logger.debug("Obteniendo todas las notificaciones desde microservicio Notify");
        return restClient.get()
                .uri("/api/notifications")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NotificationResponse>>() {});
    }
    
    public NotificationResponse getNotificationById(String id) {
        logger.debug("Obteniendo notificación con id: {} desde microservicio Notify", id);
        return restClient.get()
                .uri("/api/notifications/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(NotificationResponse.class);
    }
    
    public List<NotificationResponse> getNotificationsByRecipient(String recipient) {
        logger.debug("Obteniendo notificaciones para destinatario: {} desde microservicio Notify", recipient);
        return restClient.get()
                .uri("/api/notifications/recipient/{recipient}", recipient)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NotificationResponse>>() {});
    }
    
    public List<NotificationResponse> getNotificationsByChannel(String channel) {
        logger.debug("Obteniendo notificaciones por canal: {} desde microservicio Notify", channel);
        return restClient.get()
                .uri("/api/notifications/channel/{channel}", channel)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NotificationResponse>>() {});
    }
    
    public Map<String, String> sendNotification(NotificationRequest request) {
        logger.debug("Enviando notificación a {} por canal {} desde microservicio Notify", 
                     request.getRecipient(), request.getChannel());
        return restClient.post()
                .uri("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});
    }
}
