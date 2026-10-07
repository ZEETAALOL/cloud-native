import { useMsal } from '@azure/msal-react';
import { loginRequest } from '../authConfig';

function Login() {
  const { instance } = useMsal();

  const handleLogin = async () => {
    try {
      await instance.loginRedirect(loginRequest);
    } catch (error) {
      console.error('Error al iniciar sesión:', error);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'radial-gradient(ellipse at center, #1a0f2e 0%, #0a0a0f 50%)',
      padding: '20px'
    }}>
      <div className="card" style={{
        maxWidth: '500px',
        width: '100%',
        padding: '3rem',
        textAlign: 'center',
        background: 'linear-gradient(145deg, #13131a 0%, #1a1a24 100%)',
        border: '2px solid #7c3aed',
        boxShadow: '0 0 40px rgba(124, 58, 237, 0.3)',
      }}>
        <div style={{ marginBottom: '2rem' }}>
          <div style={{ 
            fontSize: '4rem', 
            marginBottom: '1rem',
            animation: 'float 3s ease-in-out infinite'
          }}>
            📦
          </div>
          <h1 style={{
            fontFamily: 'Orbitron, sans-serif',
            fontSize: '2.5rem',
            background: 'linear-gradient(135deg, #a855f7 0%, #7c3aed 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            marginBottom: '0.5rem',
            fontWeight: '800'
          }}>
            PEDIDOS360
          </h1>
          <p style={{ 
            color: '#a0a0b0', 
            fontSize: '1.1rem',
            fontFamily: 'Rajdhani, sans-serif',
            letterSpacing: '1px'
          }}>
            Sistema de Gestión Cloud-Native
          </p>
        </div>

        <div style={{
          padding: '1.5rem',
          background: 'rgba(124, 58, 237, 0.1)',
          borderRadius: '12px',
          marginBottom: '2rem',
          border: '1px solid rgba(124, 58, 237, 0.3)'
        }}>
          <p style={{ 
            color: '#ffffff', 
            marginBottom: '0.5rem',
            fontWeight: '600'
          }}>
            🔐 Autenticación Segura
          </p>
          <p style={{ 
            color: '#a0a0b0', 
            fontSize: '0.95rem',
            marginBottom: 0
          }}>
            Inicia sesión con tu cuenta de Microsoft Azure
          </p>
        </div>

        <button
          onClick={handleLogin}
          className="btn btn-primary w-100"
          style={{
            fontSize: '1.1rem',
            padding: '1rem 2rem',
            fontFamily: 'Orbitron, sans-serif',
            fontWeight: '700',
            letterSpacing: '1px',
            background: 'linear-gradient(135deg, #a855f7 0%, #7c3aed 100%)',
            border: 'none',
            boxShadow: '0 0 30px rgba(124, 58, 237, 0.5)',
            transition: 'all 0.3s ease'
          }}
        >
          🚀 INICIAR SESIÓN CON MICROSOFT
        </button>

        <div style={{
          marginTop: '2rem',
          padding: '1rem',
          background: 'rgba(19, 19, 26, 0.5)',
          borderRadius: '8px',
          fontSize: '0.85rem'
        }}>
          <p style={{ color: '#7c7c8c', marginBottom: '0.5rem' }}>
            <strong>Arquitectura Implementada:</strong>
          </p>
          <div style={{ 
            display: 'flex', 
            gap: '0.5rem', 
            flexWrap: 'wrap', 
            justifyContent: 'center'
          }}>
            <span className="badge bg-primary" style={{ fontSize: '0.7rem' }}>
              OAuth2
            </span>
            <span className="badge bg-success" style={{ fontSize: '0.7rem' }}>
              Microservicios
            </span>
            <span className="badge bg-info" style={{ fontSize: '0.7rem' }}>
              RabbitMQ
            </span>
            <span className="badge bg-secondary" style={{ fontSize: '0.7rem' }}>
              Circuit Breaker
            </span>
          </div>
        </div>
      </div>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-20px); }
        }
      `}</style>
    </div>
  );
}

export default Login;
