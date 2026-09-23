import { useState, useEffect } from 'react';
import apiService from '../services/api';

function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    stock: '',
    category: '',
    active: true
  });
  const [saving, setSaving] = useState(false);

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

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validaciones básicas
    if (!formData.name || !formData.price || !formData.stock) {
      alert('Por favor completa los campos obligatorios');
      return;
    }

    try {
      setSaving(true);
      const productData = {
        ...formData,
        price: parseFloat(formData.price),
        stock: parseInt(formData.stock)
      };
      
      await apiService.createProduct(productData);
      
      // Cerrar modal y resetear formulario
      setShowModal(false);
      setFormData({
        name: '',
        description: '',
        price: '',
        stock: '',
        category: '',
        active: true
      });
      
      // Recargar productos
      await loadProducts();
      
      alert('Producto creado exitosamente');
    } catch (err) {
      console.error('Error al crear producto:', err);
      alert('Error al crear el producto. Verifica la conexión con el backend.');
    } finally {
      setSaving(false);
    }
  };

  const openModal = () => {
    setFormData({
      name: '',
      description: '',
      price: '',
      stock: '',
      category: '',
      active: true
    });
    setShowModal(true);
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
        <div>
          <button className="btn btn-success me-2" onClick={openModal}>
            <i className="bi bi-plus-circle"></i> Crear Producto
          </button>
          <button className="btn btn-outline-primary" onClick={loadProducts}>
            <i className="bi bi-arrow-clockwise"></i> Actualizar
          </button>
        </div>
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

      {/* Modal para crear producto */}
      {showModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Crear Nuevo Producto</h5>
                <button 
                  type="button" 
                  className="btn-close" 
                  onClick={() => setShowModal(false)}
                  disabled={saving}
                ></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Nombre *</label>
                      <input
                        type="text"
                        className="form-control"
                        name="name"
                        value={formData.name}
                        onChange={handleInputChange}
                        required
                        placeholder="Ej: Notebook HP Pavilion"
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Categoría</label>
                      <input
                        type="text"
                        className="form-control"
                        name="category"
                        value={formData.category}
                        onChange={handleInputChange}
                        placeholder="Ej: Electrónica"
                      />
                    </div>
                  </div>
                  
                  <div className="mb-3">
                    <label className="form-label">Descripción</label>
                    <textarea
                      className="form-control"
                      name="description"
                      value={formData.description}
                      onChange={handleInputChange}
                      rows="3"
                      placeholder="Descripción del producto..."
                    ></textarea>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Precio (CLP) *</label>
                      <input
                        type="number"
                        className="form-control"
                        name="price"
                        value={formData.price}
                        onChange={handleInputChange}
                        required
                        min="0"
                        step="0.01"
                        placeholder="599990"
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Stock *</label>
                      <input
                        type="number"
                        className="form-control"
                        name="stock"
                        value={formData.stock}
                        onChange={handleInputChange}
                        required
                        min="0"
                        placeholder="50"
                      />
                    </div>
                  </div>

                  <div className="form-check">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      name="active"
                      id="activeCheck"
                      checked={formData.active}
                      onChange={handleInputChange}
                    />
                    <label className="form-check-label" htmlFor="activeCheck">
                      Producto activo
                    </label>
                  </div>
                </div>
                <div className="modal-footer">
                  <button 
                    type="button" 
                    className="btn btn-secondary" 
                    onClick={() => setShowModal(false)}
                    disabled={saving}
                  >
                    Cancelar
                  </button>
                  <button 
                    type="submit" 
                    className="btn btn-primary"
                    disabled={saving}
                  >
                    {saving ? 'Guardando...' : 'Crear Producto'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Products;
