import { useState, useEffect } from 'react';
import apiService from '../services/api';

function Dashboard() {
  const [data, setData] = useState(null);
  const [stats, setStats] = useState({
    products: 0,
    auditEvents: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [dataResponse, productsResponse, auditResponse] = await Promise.all([
        apiService.getData(),
        apiService.getProducts(),
        apiService.getAudit(),
      ]);

      setData(dataResponse);
      setStats({
        products: productsResponse.length,
        auditEvents: auditResponse.length,
      });
    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <div className="spinner-border" role="status" style={{ width: '3rem', height: '3rem' }}>
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p className="mt-3" style={{ color: '#c0c0d0', fontSize: '1.1rem' }}>Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="mb-5">
        <h1 className="mb-2" style={{ 
          fontSize: 'clamp(1.8rem, 5vw, 2.5rem)', 
          fontWeight: 'bold',
          background: 'linear-gradient(135deg, #a855f7 0%, #c4b5fd 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          textShadow: '0 2px 4px rgba(0, 0, 0, 0.5)'
        }}>
          Dashboard - Pedidos360
        </h1>
        <p style={{ color: '#c0c0d0', fontSize: 'clamp(0.95rem, 2vw, 1.1rem)' }}>
          Sistema de Gestión de Pedidos Cloud-Native
        </p>
      </div>
      
      <div className="row mb-4">
        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%)',
            border: 'none',
            color: '#ffffff'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: 'clamp(2rem, 5vw, 3rem)' }}>📦</div>
              <h2 className="mt-2" style={{ fontSize: 'clamp(2rem, 6vw, 3rem)', fontWeight: 'bold', color: '#ffffff' }}>{stats.products}</h2>
              <h5 className="card-title" style={{ color: '#ffffff', fontSize: 'clamp(1rem, 2.5vw, 1.25rem)' }}>Productos en Catálogo</h5>
              <p className="card-text" style={{ opacity: 1, color: '#e0e0ff', fontSize: 'clamp(0.85rem, 2vw, 0.95rem)' }}>
                Inventario total disponible
              </p>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
            border: 'none',
            color: '#ffffff'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: 'clamp(2rem, 5vw, 3rem)' }}>📊</div>
              <h2 className="mt-2" style={{ fontSize: 'clamp(2rem, 6vw, 3rem)', fontWeight: 'bold', color: '#ffffff' }}>{stats.auditEvents}</h2>
              <h5 className="card-title" style={{ color: '#ffffff', fontSize: 'clamp(1rem, 2.5vw, 1.25rem)' }}>Eventos de Auditoría</h5>
              <p className="card-text" style={{ opacity: 1, color: '#e0ffe0', fontSize: 'clamp(0.85rem, 2vw, 0.95rem)' }}>
                Acciones registradas en el sistema
              </p>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
            border: 'none',
            color: '#ffffff'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: 'clamp(2rem, 5vw, 3rem)' }}>👤</div>
              <h2 className="mt-2" style={{ fontSize: 'clamp(1.5rem, 4vw, 2rem)', fontWeight: 'bold', color: '#ffffff' }}>
                {data?.nombre || 'Sistema'}
              </h2>
              <h5 className="card-title" style={{ color: '#ffffff', fontSize: 'clamp(1rem, 2.5vw, 1.25rem)' }}>Usuario Activo</h5>
              <p className="card-text" style={{ opacity: 1, color: '#e0e0ff', fontSize: 'clamp(0.85rem, 2vw, 0.95rem)' }}>
                Sesión actual del sistema
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">🏗️ Arquitectura Cloud-Native</h5>
            </div>
            <div className="card-body">
              <p style={{ color: '#c0c0d0', marginBottom: '1.5rem', fontSize: 'clamp(0.9rem, 2vw, 1rem)' }}>
                Backend desplegado en <strong style={{ color: '#ffffff' }}>AWS EC2</strong> con arquitectura de microservicios
              </p>
              <div className="row">
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ BFF (Backend for Frontend)
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ Orders Service
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ Catalog Service
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ Audit Service
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ Report Service
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(124, 58, 237, 0.2)', borderRadius: '6px', borderLeft: '3px solid #a855f7', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    ✅ Notify Service
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">🛠️ Tecnologías Implementadas</h5>
            </div>
            <div className="card-body">
              <p style={{ color: '#c0c0d0', marginBottom: '1.5rem', fontSize: 'clamp(0.9rem, 2vw, 1rem)' }}>
                Stack tecnológico completo para arquitectura cloud-native
              </p>
              <div className="row">
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 RabbitMQ (Message Broker)
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 PostgreSQL Database
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 MongoDB Database
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 Circuit Breaker (Resilience4j)
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 OAuth2 / Azure AD
                  </div>
                </div>
                <div className="col-md-6 col-12 mb-2">
                  <div style={{ padding: '10px', backgroundColor: 'rgba(16, 185, 129, 0.2)', borderRadius: '6px', borderLeft: '3px solid #10b981', color: '#ffffff', fontSize: 'clamp(0.85rem, 1.5vw, 0.95rem)' }}>
                    🔹 Docker + AWS EC2
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
