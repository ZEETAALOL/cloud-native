package cl.duoc.orders.dto;

public record ClienteResponse(
    String rut,
    String nombre,
    String apellido,
    String direccion,
    String comuna
) {}
