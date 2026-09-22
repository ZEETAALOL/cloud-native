package cl.duoc.orders.controller;

import cl.duoc.orders.dto.ClienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    
    @GetMapping
    public ClienteResponse obtenerCliente() {
        log.info("Solicitud recibida en /clientes");
        
        // Cliente de ejemplo del sistema Pedidos360
        ClienteResponse cliente = new ClienteResponse(
            "20456789-3",
            "Bastián",
            "Martínez",
            "Av. Providencia 2594, Providencia",
            "Santiago"
        );
        
        log.info("Retornando cliente: {} {}", cliente.nombre(), cliente.apellido());
        return cliente;
    }
}
