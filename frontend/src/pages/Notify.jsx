import { useState } from 'react';
import apiService from '../services/api';

function Notify() {
  const [formData, setFormData] = useState({
    to: '',
    subject: '',
    message: '',
  });
  const [sending, setSending] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setSending(true);
      setError(null);
      setResult(null);

      const response = await apiService.sendNotification(formData);
      setResult(response);
      
      // Limpiar formulario
      setFormData({
        to: '',
        subject: '',
        message: '',
      });
    } catch (err) {
      setError('Error al enviar notificación. Verifica la conexión con el backend.');
      console.error(err);
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="container mt-4">
      <h1 className="mb-4">Enviar Notificación</h1>

      <div className="row">
        <div className="col-md-8">
          <div className="card">
            <div className="card-header">
              <h5>Formulario de Notificación</h5>
            </div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="to" className="form-label">
                    Destinatario (Email)
                  </label>
                  <input
                    type="email"
                    className="form-control"
                    id="to"
                    name="to"
                    value={formData.to}
                    onChange={handleChange}
                    placeholder="ejemplo@correo.com"
                    required
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="subject" className="form-label">
                    Asunto
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="subject"
                    name="subject"
                    value={formData.subject}
                    onChange={handleChange}
                    placeholder="Asunto del mensaje"
                    required
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="message" className="form-label">
                    Mensaje
                  </label>
                  <textarea
                    className="form-control"
                    id="message"
                    name="message"
                    rows="5"
                    value={formData.message}
                    onChange={handleChange}
                    placeholder="Escribe tu mensaje aquí..."
                    required
                  ></textarea>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={sending}
                >
                  {sending ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                      Enviando...
                    </>
                  ) : (
                    'Enviar Notificación'
                  )}
                </button>
              </form>

              {result && (
                <div className="alert alert-success mt-3" role="alert">
                  <h5>✅ Notificación Enviada</h5>
                  <p className="mb-0">
                    <strong>Destinatario:</strong> {result.recipient}
                  </p>
                  <p className="mb-0">
                    <strong>Estado:</strong> {result.status}
                  </p>
                  <p className="mb-0">
                    <strong>ID:</strong> {result.orderId}
                  </p>
                  <small className="text-muted">{result.message}</small>
                </div>
              )}

              {error && (
                <div className="alert alert-danger mt-3" role="alert">
                  {error}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card">
            <div className="card-header">
              <h5>Información</h5>
            </div>
            <div className="card-body">
              <p>
                <strong>Sistema de Mensajería Asíncrona</strong>
              </p>
              <ul className="list-unstyled">
                <li>✅ BFF recibe la petición</li>
                <li>✅ Publica mensaje en RabbitMQ</li>
                <li>✅ Notify Service procesa el mensaje</li>
                <li>✅ Se envía la notificación</li>
              </ul>
              <hr />
              <p className="mb-0">
                <small className="text-muted">
                  Este sistema demuestra el patrón Event-Driven Architecture
                  con RabbitMQ como Message Broker.
                </small>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Notify;
