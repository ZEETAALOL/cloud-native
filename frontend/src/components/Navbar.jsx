import { Link, useLocation } from 'react-router-dom';
import { useMsal } from '@azure/msal-react';

function Navbar() {
  const location = useLocation();
  const { instance, accounts } = useMsal();

  const isActive = (path) => {
    return location.pathname === path;
  };

  const handleLogout = () => {
    instance.logoutPopup();
  };

  const userName = accounts[0]?.name || 'Usuario';

  return (
    <nav className="navbar navbar-expand-lg" style={{
      backgroundColor: '#1a1a1a',
      borderBottom: '2px solid #7c3aed',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.3)'
    }}>
      <div className="container-fluid px-4">
        <Link className="navbar-brand d-flex align-items-center" to="/" style={{
          fontSize: '1.5rem',
          fontWeight: 'bold',
          background: 'linear-gradient(135deg, #7c3aed 0%, #a78bfa 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          textDecoration: 'none'
        }}>
          <span style={{ marginRight: '8px' }}>📦</span>
          Pedidos360
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          style={{ borderColor: '#7c3aed' }}
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto align-items-center">
            <li className="nav-item mx-2">
              <Link 
                className="nav-link" 
                to="/"
                style={{
                  color: isActive('/') ? '#a78bfa' : '#ffffff',
                  fontWeight: isActive('/') ? 'bold' : 'normal',
                  borderBottom: isActive('/') ? '2px solid #7c3aed' : 'none',
                  paddingBottom: '4px',
                  transition: 'all 0.3s'
                }}
              >
                🏠 Dashboard
              </Link>
            </li>
            <li className="nav-item mx-2">
              <Link 
                className="nav-link" 
                to="/products"
                style={{
                  color: isActive('/products') ? '#a78bfa' : '#ffffff',
                  fontWeight: isActive('/products') ? 'bold' : 'normal',
                  borderBottom: isActive('/products') ? '2px solid #7c3aed' : 'none',
                  paddingBottom: '4px',
                  transition: 'all 0.3s'
                }}
              >
                📦 Productos
              </Link>
            </li>
            <li className="nav-item mx-2">
              <Link 
                className="nav-link" 
                to="/audit"
                style={{
                  color: isActive('/audit') ? '#a78bfa' : '#ffffff',
                  fontWeight: isActive('/audit') ? 'bold' : 'normal',
                  borderBottom: isActive('/audit') ? '2px solid #7c3aed' : 'none',
                  paddingBottom: '4px',
                  transition: 'all 0.3s'
                }}
              >
                📊 Auditoría
              </Link>
            </li>
            <li className="nav-item mx-2">
              <Link 
                className="nav-link" 
                to="/notify"
                style={{
                  color: isActive('/notify') ? '#a78bfa' : '#ffffff',
                  fontWeight: isActive('/notify') ? 'bold' : 'normal',
                  borderBottom: isActive('/notify') ? '2px solid #7c3aed' : 'none',
                  paddingBottom: '4px',
                  transition: 'all 0.3s'
                }}
              >
                🔔 Notificaciones
              </Link>
            </li>
            <li className="nav-item mx-3" style={{ 
              color: '#a0a0b0',
              fontSize: '0.9rem',
              fontFamily: 'Rajdhani, sans-serif'
            }}>
              👤 {userName}
            </li>
            <li className="nav-item">
              <button
                onClick={handleLogout}
                className="btn btn-sm"
                style={{
                  background: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
                  color: 'white',
                  border: 'none',
                  padding: '0.4rem 1rem',
                  fontFamily: 'Orbitron, sans-serif',
                  fontSize: '0.8rem',
                  fontWeight: '600',
                  letterSpacing: '0.5px'
                }}
              >
                🚪 SALIR
              </button>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
