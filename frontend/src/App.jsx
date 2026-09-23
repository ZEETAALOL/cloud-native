import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { useEffect } from 'react';
import { setMsalInstance } from './services/api';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Audit from './pages/Audit';
import Notify from './pages/Notify';
import Login from './pages/Login';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  const isAuthenticated = useIsAuthenticated();
  const { instance } = useMsal();
  
  // Configurar la instancia de MSAL en el servicio API
  useEffect(() => {
    setMsalInstance(instance);
  }, [instance]);
  
  // MODO DEMO: Deshabilitar autenticación temporalmente para AWS
  const DEMO_MODE = window.location.hostname !== 'localhost';

  // Si no está autenticado y NO está en modo demo, mostrar login
  if (!isAuthenticated && !DEMO_MODE) {
    return <Login />;
  }

  // Si está autenticado o en modo demo, mostrar la aplicación completa
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/products" element={<Products />} />
          <Route path="/audit" element={<Audit />} />
          <Route path="/notify" element={<Notify />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
