import { useState, useEffect } from 'react';
import apiService from '../services/api';

function Audit() {
  const [auditEvents, setAuditEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadAudit();
  }, []);

  const loadAudit = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await apiService.getAudit();
      setAuditEvents(data);
    } catch (err) {
      setError('Error al cargar eventos de auditoría.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleString('es-CL');
  };

  const getActionBadge = (action) => {
    const badges = {
      LOGIN: 'primary',
      CREATE_ORDER: 'success',
      UPDATE_PROFILE: 'info',
      DELETE_PRODUCT: 'danger',
    };
    return badges[action] || 'secondary';
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
        <button className="btn btn-primary" onClick={loadAudit}>
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>Auditoría del Sistema</h1>
        <button className="btn btn-success" onClick={loadAudit}>
          <i className="bi bi-arrow-clockwise"></i> Actualizar
        </button>
      </div>

      <div className="card">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Usuario</th>
                  <th>Acción</th>
                  <th>Entidad</th>
                  <th>Detalles</th>
                  <th>IP</th>
                  <th>Fecha/Hora</th>
                </tr>
              </thead>
              <tbody>
                {auditEvents.map((event) => (
                  <tr key={event.id}>
                    <td>{event.id}</td>
                    <td>
                      <strong>{event.userId}</strong>
                    </td>
                    <td>
                      <span className={`badge bg-${getActionBadge(event.action)}`}>
                        {event.action}
                      </span>
                    </td>
                    <td>{event.entity}</td>
                    <td>{event.details}</td>
                    <td>
                      <code>{event.ipAddress}</code>
                    </td>
                    <td>{formatDate(event.timestamp)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {auditEvents.length === 0 && (
            <div className="alert alert-info" role="alert">
              No hay eventos de auditoría registrados.
            </div>
          )}
        </div>
      </div>

      <div className="mt-3">
        <small className="text-muted">
          Total de eventos: <strong>{auditEvents.length}</strong>
        </small>
      </div>
    </div>
  );
}

export default Audit;
