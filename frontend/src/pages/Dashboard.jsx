import { useState, useEffect } from 'react';
import apiService from '../services/api';

function Dashboard() {
  const [data, setData] = useState(null);
  const [stats, setStats] = useState({
    products: 0,
    auditEvents: 0,
  });
  const [loading, setLoading] = useState(true);
  const [testResults, setTestResults] = useState({});
  const [testing, setTesting] = useState({});

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

  const testEndpoint = async (name, apiCall) => {
    setTesting(prev => ({ ...prev, [name]: true }));
    setTestResults(prev => ({ ...prev, [name]: null }));
    
    try {
      const startTime = Date.now();
      const result = await apiCall();
      const duration = Date.now() - startTime;
      
      // Obtener el token actual de sessionStorage (MSAL lo guarda ahí)
      const accounts = JSON.parse(sessionStorage.getItem('msal.account.keys') || '[]');
      let token = null;
      if (accounts.length > 0) {
        const tokenKey = Object.keys(sessionStorage).find(key => 
          key.includes('accesstoken') && key.includes(accounts[0])
        );
        if (tokenKey) {
          const tokenData = JSON.parse(sessionStorage.getItem(tokenKey));
          token = tokenData?.secret;
        }
      }
      
      setTestResults(prev => ({ 
        ...prev, 
        [name]: { 
          success: true, 
          duration,
          data: result,
          token: token,
          timestamp: new Date().toISOString()
        } 
      }));
    } catch (error) {
      setTestResults(prev => ({ 
        ...prev, 
        [name]: { 
          success: false, 
          error: error.message,
          timestamp: new Date().toISOString()
        } 
      }));
    } finally {
      setTesting(prev => ({ ...prev, [name]: false }));
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

      {/* Sección de Pruebas de Integración */}
      <div className="row mb-4">
        <div className="col-12">
          <div className="card">
            <div className="card-header" style={{ background: 'linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%)', color: '#ffffff' }}>
              <h5 className="mb-0">Pruebas de Integración - Demostración en Vivo</h5>
            </div>
            <div className="card-body">
              <p style={{ color: '#c0c0d0', marginBottom: '1.5rem' }}>
                Haz clic en cada botón para probar la conexión con los microservicios desplegados en AWS
              </p>
              
              <div className="row">
                {/* Test Products API */}
                <div className="col-md-6 mb-3">
                  <div style={{ 
                    padding: '15px', 
                    backgroundColor: 'rgba(124, 58, 237, 0.1)', 
                    borderRadius: '8px',
                    border: '1px solid rgba(168, 85, 247, 0.3)'
                  }}>
                    <h6 style={{ color: '#a855f7', marginBottom: '10px' }}>Catalog Service</h6>
                    <button 
                      className="btn btn-primary w-100"
                      onClick={() => testEndpoint('products', apiService.getProducts)}
                      disabled={testing.products}
                    >
                      {testing.products ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Consultando...
                        </>
                      ) : (
                        'Consultar Productos'
                      )}
                    </button>
                    {testResults.products && (
                      <div className={`mt-2 p-2 rounded ${testResults.products.success ? 'bg-success' : 'bg-danger'}`} style={{ fontSize: '0.85rem' }}>
                        {testResults.products.success ? (
                          <>
                            Éxito - {testResults.products.data.length} productos ({testResults.products.duration}ms)
                            <div className="mt-2" style={{ fontSize: '0.75rem', opacity: 0.9 }}>
                              <small>GET /api/products</small>
                            </div>
                          </>
                        ) : (
                          <>
                            Error: {testResults.products.error}
                          </>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                {/* Test Audit API */}
                <div className="col-md-6 mb-3">
                  <div style={{ 
                    padding: '15px', 
                    backgroundColor: 'rgba(16, 185, 129, 0.1)', 
                    borderRadius: '8px',
                    border: '1px solid rgba(16, 185, 129, 0.3)'
                  }}>
                    <h6 style={{ color: '#10b981', marginBottom: '10px' }}>Audit Service</h6>
                    <button 
                      className="btn btn-success w-100"
                      onClick={() => testEndpoint('audit', apiService.getAudit)}
                      disabled={testing.audit}
                    >
                      {testing.audit ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Consultando...
                        </>
                      ) : (
                        'Consultar Auditoría'
                      )}
                    </button>
                    {testResults.audit && (
                      <div className={`mt-2 p-2 rounded ${testResults.audit.success ? 'bg-success' : 'bg-danger'}`} style={{ fontSize: '0.85rem' }}>
                        {testResults.audit.success ? (
                          <>
                            Éxito - {testResults.audit.data.length} eventos ({testResults.audit.duration}ms)
                            <div className="mt-2" style={{ fontSize: '0.75rem', opacity: 0.9 }}>
                              <small>GET /api/audit</small>
                            </div>
                          </>
                        ) : (
                          <>
                            Error: {testResults.audit.error}
                          </>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                {/* Test Data API */}
                <div className="col-md-6 mb-3">
                  <div style={{ 
                    padding: '15px', 
                    backgroundColor: 'rgba(59, 130, 246, 0.1)', 
                    borderRadius: '8px',
                    border: '1px solid rgba(59, 130, 246, 0.3)'
                  }}>
                    <h6 style={{ color: '#3b82f6', marginBottom: '10px' }}>Orders Service</h6>
                    <button 
                      className="btn btn-info w-100"
                      onClick={() => testEndpoint('data', apiService.getData)}
                      disabled={testing.data}
                    >
                      {testing.data ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Consultando...
                        </>
                      ) : (
                        'Consultar Cliente'
                      )}
                    </button>
                    {testResults.data && (
                      <div className={`mt-2 p-2 rounded ${testResults.data.success ? 'bg-success' : 'bg-danger'}`} style={{ fontSize: '0.85rem' }}>
                        {testResults.data.success ? (
                          <>
                            Éxito - Cliente: {testResults.data.data.nombre} ({testResults.data.duration}ms)
                            <div className="mt-2" style={{ fontSize: '0.75rem', opacity: 0.9 }}>
                              <small>GET /api/data</small>
                            </div>
                          </>
                        ) : (
                          <>
                            Error: {testResults.data.error}
                          </>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                {/* Test All */}
                <div className="col-md-6 mb-3">
                  <div style={{ 
                    padding: '15px', 
                    backgroundColor: 'rgba(234, 179, 8, 0.1)', 
                    borderRadius: '8px',
                    border: '1px solid rgba(234, 179, 8, 0.3)'
                  }}>
                    <h6 style={{ color: '#eab308', marginBottom: '10px' }}>Prueba Completa</h6>
                    <button 
                      className="btn btn-warning w-100"
                      onClick={async () => {
                        await testEndpoint('products', apiService.getProducts);
                        await testEndpoint('audit', apiService.getAudit);
                        await testEndpoint('data', apiService.getData);
                      }}
                      disabled={testing.products || testing.audit || testing.data}
                    >
                      {(testing.products || testing.audit || testing.data) ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Probando...
                        </>
                      ) : (
                        'Probar Todos los Servicios'
                      )}
                    </button>
                    <div className="mt-2" style={{ fontSize: '0.85rem', color: '#c0c0d0' }}>
                      Ejecuta pruebas en todos los microservicios
                    </div>
                  </div>
                </div>
              </div>

              <div className="alert alert-info mt-3" style={{ backgroundColor: 'rgba(59, 130, 246, 0.1)', border: '1px solid rgba(59, 130, 246, 0.3)', color: '#93c5fd' }}>
                <strong>Demostración:</strong> Estos botones consultan los microservicios en AWS EC2 a través del BFF con autenticación JWT de Azure AD.
              </div>

              {/* Panel de Detalles Técnicos */}
              {Object.keys(testResults).length > 0 && Object.values(testResults).some(r => r !== null) && (
                <div className="mt-4">
                  <h6 style={{ color: '#a855f7', marginBottom: '15px' }}>Detalles Técnicos de la Última Prueba</h6>
                  
                  {Object.entries(testResults).map(([key, result]) => {
                    if (!result) return null;
                    
                    return (
                      <div key={key} className="card mb-3" style={{ backgroundColor: 'rgba(30, 30, 50, 0.5)', border: '1px solid rgba(168, 85, 247, 0.3)' }}>
                        <div className="card-header" style={{ backgroundColor: 'rgba(124, 58, 237, 0.2)', borderBottom: '1px solid rgba(168, 85, 247, 0.3)' }}>
                          <strong style={{ color: '#a855f7', textTransform: 'uppercase' }}>{key}</strong>
                          <span style={{ float: 'right', fontSize: '0.85rem', color: '#c0c0d0' }}>
                            {new Date(result.timestamp).toLocaleTimeString()}
                          </span>
                        </div>
                        <div className="card-body" style={{ fontSize: '0.85rem' }}>
                          {result.success ? (
                            <>
                              <div className="mb-3">
                                <strong style={{ color: '#10b981' }}>STATUS:</strong>
                                <span style={{ color: '#ffffff', marginLeft: '10px' }}>200 OK</span>
                                <span style={{ color: '#c0c0d0', marginLeft: '10px' }}>({result.duration}ms)</span>
                              </div>

                              {result.token && (
                                <div className="mb-3">
                                  <strong style={{ color: '#3b82f6' }}>JWT TOKEN:</strong>
                                  <div style={{ 
                                    backgroundColor: 'rgba(0, 0, 0, 0.3)', 
                                    padding: '10px', 
                                    borderRadius: '4px', 
                                    marginTop: '5px',
                                    fontFamily: 'monospace',
                                    fontSize: '0.75rem',
                                    color: '#93c5fd',
                                    wordBreak: 'break-all'
                                  }}>
                                    {result.token.substring(0, 100)}...
                                  </div>
                                  <small style={{ color: '#9ca3af' }}>
                                    Token JWT válido enviado en header Authorization: Bearer
                                  </small>
                                </div>
                              )}

                              <div className="mb-3">
                                <strong style={{ color: '#eab308' }}>RESPONSE DATA:</strong>
                                <div style={{ 
                                  backgroundColor: 'rgba(0, 0, 0, 0.3)', 
                                  padding: '10px', 
                                  borderRadius: '4px', 
                                  marginTop: '5px',
                                  maxHeight: '200px',
                                  overflowY: 'auto',
                                  fontFamily: 'monospace',
                                  fontSize: '0.75rem',
                                  color: '#a3e635'
                                }}>
                                  <pre style={{ margin: 0, color: '#a3e635' }}>
                                    {JSON.stringify(result.data, null, 2)}
                                  </pre>
                                </div>
                              </div>

                              <div>
                                <strong style={{ color: '#a855f7' }}>HEADERS ENVIADOS:</strong>
                                <div style={{ 
                                  backgroundColor: 'rgba(0, 0, 0, 0.3)', 
                                  padding: '10px', 
                                  borderRadius: '4px', 
                                  marginTop: '5px',
                                  fontFamily: 'monospace',
                                  fontSize: '0.75rem',
                                  color: '#c4b5fd'
                                }}>
                                  Content-Type: application/json<br/>
                                  Authorization: Bearer &lt;JWT_TOKEN&gt;<br/>
                                  Accept: application/json
                                </div>
                              </div>
                            </>
                          ) : (
                            <div>
                              <strong style={{ color: '#ef4444' }}>ERROR:</strong>
                              <div style={{ 
                                backgroundColor: 'rgba(0, 0, 0, 0.3)', 
                                padding: '10px', 
                                borderRadius: '4px', 
                                marginTop: '5px',
                                color: '#fca5a5'
                              }}>
                                {result.error}
                              </div>
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
