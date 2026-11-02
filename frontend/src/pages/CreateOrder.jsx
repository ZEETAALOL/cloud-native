import { useState, useEffect } from 'react';
import apiService from '../services/api';
import './CreateOrder.css';

export default function CreateOrder() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [customerEmail, setCustomerEmail] = useState('cliente@pedidos360.cl');
  const [response, setResponse] = useState(null);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const data = await apiService.getProducts();
      // Filtrar solo productos activos con stock
      const available = data.filter(p => p.active && p.stock > 0);
      setProducts(available);
    } catch (error) {
      console.error('Error cargando productos:', error);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (product) => {
    const existing = cart.find(item => item.id === product.id);
    
    if (existing) {
      if (existing.quantity >= product.stock) {
        alert(`Stock máximo alcanzado (${product.stock} unidades)`);
        return;
      }
      setCart(cart.map(item =>
        item.id === product.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, { ...product, quantity: 1 }]);
    }
  };

  const removeFromCart = (productId) => {
    setCart(cart.filter(item => item.id !== productId));
  };

  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity < 1) {
      removeFromCart(productId);
      return;
    }
    
    const product = products.find(p => p.id === productId);
    if (newQuantity > product.stock) {
      alert(`Stock máximo: ${product.stock} unidades`);
      return;
    }
    
    setCart(cart.map(item =>
      item.id === productId
        ? { ...item, quantity: newQuantity }
        : item
    ));
  };

  const getTotal = () => {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  };

  const createOrder = async () => {
    if (cart.length === 0) {
      alert('El carrito está vacío');
      return;
    }

    if (!customerEmail) {
      alert('Ingresa un email');
      return;
    }

    setSubmitting(true);
    setResponse(null);

    try {
      const orderData = {
        customerEmail,
        total: getTotal(),
        items: cart.map(item => ({
          productId: item.id,
          productName: item.name,
          quantity: item.quantity,
          price: item.price
        })),
        details: cart.map(item => `${item.quantity}x ${item.name}`).join(', ')
      };

      const res = await apiService.createOrder(orderData);
      setResponse(res);
      
      if (res.success) {
        // Limpiar carrito tras éxito
        setCart([]);
      }
    } catch (error) {
      console.error('Error creando pedido:', error);
      setResponse({
        success: false,
        message: error.response?.data?.message || 'Error al crear el pedido'
      });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="create-order-page">
      <div className="container-fluid">
        <div className="row">
          {/* PRODUCTOS DISPONIBLES */}
          <div className="col-md-8">
            <div className="card products-card">
              <div className="card-header">
                <h2>🛍️ Productos Disponibles</h2>
              </div>
              <div className="card-body">
                {products.length === 0 ? (
                  <div className="alert alert-info">
                    No hay productos disponibles en este momento
                  </div>
                ) : (
                  <div className="row">
                    {products.map(product => (
                      <div key={product.id} className="col-md-6 col-lg-4 mb-3">
                        <div className="product-card">
                          <h5 className="product-name">{product.name}</h5>
                          <p className="product-description">{product.description}</p>
                          <div className="product-info">
                            <span className="product-price">${product.price.toLocaleString()}</span>
                            <span className="product-stock">Stock: {product.stock}</span>
                          </div>
                          <button
                            className="btn-add-cart"
                            onClick={() => addToCart(product)}
                          >
                            ➕ Agregar al Carrito
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* CARRITO Y CHECKOUT */}
          <div className="col-md-4">
            <div className="cart-card sticky-top">
              <div className="cart-header">
                <h3>🛒 Carrito de Compras</h3>
                <span className="cart-count">{cart.length} items</span>
              </div>

              <div className="cart-body">
                {cart.length === 0 ? (
                  <div className="empty-cart">
                    <p>Tu carrito está vacío</p>
                    <p className="small-text">Agrega productos para crear un pedido</p>
                  </div>
                ) : (
                  <>
                    {cart.map(item => (
                      <div key={item.id} className="cart-item">
                        <div className="item-info">
                          <h6>{item.name}</h6>
                          <p className="item-price">${item.price.toLocaleString()} c/u</p>
                        </div>
                        <div className="item-controls">
                          <div className="quantity-controls">
                            <button
                              onClick={() => updateQuantity(item.id, item.quantity - 1)}
                              className="btn-qty"
                            >
                              -
                            </button>
                            <span className="quantity">{item.quantity}</span>
                            <button
                              onClick={() => updateQuantity(item.id, item.quantity + 1)}
                              className="btn-qty"
                            >
                              +
                            </button>
                          </div>
                          <button
                            onClick={() => removeFromCart(item.id)}
                            className="btn-remove"
                          >
                            🗑️
                          </button>
                        </div>
                        <div className="item-subtotal">
                          Subtotal: ${(item.price * item.quantity).toLocaleString()}
                        </div>
                      </div>
                    ))}
                  </>
                )}
              </div>

              {cart.length > 0 && (
                <>
                  <div className="cart-total">
                    <h4>Total: ${getTotal().toLocaleString()}</h4>
                  </div>

                  <div className="checkout-section">
                    <div className="form-group">
                      <label>📧 Email del Cliente:</label>
                      <input
                        type="email"
                        value={customerEmail}
                        onChange={(e) => setCustomerEmail(e.target.value)}
                        placeholder="cliente@ejemplo.com"
                        className="form-control"
                      />
                    </div>

                    <button
                      onClick={createOrder}
                      disabled={submitting}
                      className="btn-checkout"
                    >
                      {submitting ? '⏳ Procesando...' : '✅ CREAR PEDIDO'}
                    </button>
                  </div>
                </>
              )}

              {response && (
                <div className={`response-alert ${response.success ? 'success' : 'error'}`}>
                  <h5>{response.success ? '✅ ¡Pedido Creado!' : '❌ Error'}</h5>
                  <p>{response.message}</p>
                  {response.orderId && <p><strong>ID:</strong> {response.orderId}</p>}
                  
                  {response.success && (
                    <div className="rabbit-info">
                      <p><strong>🎉 Publicado a RabbitMQ:</strong></p>
                      <ul>
                        <li>📧 q.cmd.email</li>
                        <li>👨‍🍳 q.cmd.kitchen</li>
                        <li>🧾 q.cmd.invoice</li>
                      </ul>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
