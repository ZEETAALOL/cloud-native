import { useState, useEffect } from 'react';
import apiService from '../services/api';

function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await apiService.getProducts();
      setProducts(data);
    } catch (err) {
      setError('Error al cargar productos. Verifica la conexión con el backend.');
      console.error(err);
    } finally {
      setLoading(false);
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

  if (error) {
    return (
      <div className="container mt-4">
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
        <button className="btn btn-primary" onClick={loadProducts}>
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Catálogo de Productos</h1>
        <button className="btn btn-success" onClick={loadProducts}>
          <i className="bi bi-arrow-clockwise"></i> Actualizar
        </button>
      </div>

      <div className="row">
        {products.map((product) => (
          <div key={product.id} className="col-md-4 mb-4">
            <div className="card h-100">
              <div className="card-body">
                <h5 className="card-title">{product.name}</h5>
                <p className="card-text">{product.description}</p>
                <div className="mb-2">
                  <span className="badge bg-secondary">{product.category}</span>
                  {product.active && (
                    <span className="badge bg-success ms-2">Activo</span>
                  )}
                </div>
                <div className="mt-3">
                  <p className="mb-1">
                    <strong>Precio:</strong> ${product.price}
                  </p>
                  <p className="mb-1">
                    <strong>Stock:</strong> {product.stock} unidades
                  </p>
                </div>
              </div>
              <div className="card-footer">
                <button className="btn btn-primary btn-sm w-100">
                  Ver Detalles
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="alert alert-info" role="alert">
          No hay productos disponibles.
        </div>
      )}
    </div>
  );
}

export default Products;
