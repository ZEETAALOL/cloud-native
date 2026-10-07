package cl.duoc.api.repository;

import cl.duoc.api.dto.ClienteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
public class ClienteRepository {

    private final RestClient restClient;

    public ClienteRepository(
        RestClient.Builder restClientBuilder,
        @Value("${microservices.clientes.base-url}")
        String baseUrl) {
        this.restClient = restClientBuilder
            .baseUrl(baseUrl)
            .build();
    }

    public ClienteResponse obtenerCliente() {
        return restClient
            .get()
            .uri("/clientes")
            .retrieve()
            .body(ClienteResponse.class);
    }
}
