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
        
        // Datos hardcodeados según Tutorial Parte 2
        ClienteResponse cliente = new ClienteResponse(
            "12345678-9",
            "Wacoldo",
            "Soto",
            "Calle Falsa 123",
            "Santiago"
        );
        
        log.info("Retornando cliente: {} {}", cliente.nombre(), cliente.apellido());
        return cliente;
    }
}
