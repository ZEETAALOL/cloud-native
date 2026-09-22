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
          <p className="mt-3" style={{ color: '#a0a0a0' }}>Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="mb-5">
        <h1 className="mb-2" style={{ 
          fontSize: '2.5rem', 
          fontWeight: 'bold',
          background: 'linear-gradient(135deg, #7c3aed 0%, #a78bfa 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent'
        }}>
          Dashboard - Pedidos360
        </h1>
        <p style={{ color: '#a0a0a0', fontSize: '1.1rem' }}>
          Sistema de Gestión de Pedidos Cloud-Native
        </p>
      </div>
      
      <div className="row mb-4">
        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%)',
            border: 'none'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: '3rem' }}>📦</div>
              <h2 className="mt-2" style={{ fontSize: '3rem', fontWeight: 'bold' }}>{stats.products}</h2>
              <h5 className="card-title">Productos en Catálogo</h5>
              <p className="card-text" style={{ opacity: 0.9 }}>
                Inventario total disponible
              </p>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
            border: 'none'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: '3rem' }}>📊</div>
              <h2 className="mt-2" style={{ fontSize: '3rem', fontWeight: 'bold' }}>{stats.auditEvents}</h2>
              <h5 className="card-title">Eventos de Auditoría</h5>
              <p className="card-text" style={{ opacity: 0.9 }}>
                Acciones registradas en el sistema
              </p>
            </div>
          </div>
        </div>

        <div className="col-md-4 mb-3">
          <div className="card h-100" style={{ 
            background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
            border: 'none'
          }}>
            <div className="card-body text-center">
              <div style={{ fontSize: '3rem' }}>👤</div>
              <h2 className="mt-2" style={{ fontSize: '2rem', fontWeight: 'bold' }}>
                {data?.nombre || 'Sistema'}
              </h2>
              <h5 className="card-title">Usuario Activo</h5>
              <p className="card-text" style={{ opacity: 0.9 }}>
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
              <p style={{ color: '#a0a0a0', marginBottom: '1.5rem' }}>
                Backend desplegado en <strong style={{ color: '#7c3aed' }}>AWS EC2</strong> con arquitectura de microservicios
              </p>
              <div className="row">
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
                    ✅ BFF (Backend for Frontend)
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
                    ✅ Orders Service
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
                    ✅ Catalog Service
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
                    ✅ Audit Service
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
                    ✅ Report Service
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #7c3aed' }}>
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
              <p style={{ color: '#a0a0a0', marginBottom: '1.5rem' }}>
                Stack tecnológico completo para arquitectura cloud-native
              </p>
              <div className="row">
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
                    🔹 RabbitMQ (Message Broker)
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
                    🔹 PostgreSQL Database
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
                    🔹 MongoDB Database
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
                    🔹 Circuit Breaker (Resilience4j)
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
                    🔹 OAuth2 / Azure AD
                  </div>
                </div>
                <div className="col-6 mb-2">
                  <div style={{ padding: '8px', backgroundColor: '#2d2d2d', borderRadius: '6px', borderLeft: '3px solid #10b981' }}>
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
