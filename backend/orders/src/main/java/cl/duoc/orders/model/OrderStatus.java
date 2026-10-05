package cl.duoc.orders.model;

/**
 * Estados del pedido según especificación del caso
 * Flujo: CREADO -> ACEPTADO -> EN_PREPARACION -> DESPACHADO -> ENTREGADO
 * En cualquier momento (excepto ENTREGADO) puede pasar a CANCELADO
 */
public enum OrderStatus {
    CREADO,
    ACEPTADO,
    EN_PREPARACION,
    DESPACHADO,
    ENTREGADO,
    CANCELADO
}
