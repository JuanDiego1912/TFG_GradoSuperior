import { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/Welcome.css';

function Welcome() {
  const [noticias, setNoticias] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const obtenerNoticias = async () => {
      try {
        const options = {
          method: 'GET',
          url: 'https://noticias-economia-espanol.p.rapidapi.com/news',
          headers: {
            'X-RapidAPI-Key': '',
            'X-RapidAPI-Host': 'noticias-economia-espanol.p.rapidapi.com'
          }
        };
        const response = await axios.request(options);
        setNoticias(response.data.articles);
      } catch (err) {
        setError('Error al cargar las noticias económicas.');
        console.error(err);
      }
    };

    obtenerNoticias();
  }, []);

  return (
    <div className="welcome-container">
      <h1>Bienvenido a un Banco Simulado</h1>
      <p>Noticias bancarias del día:</p>
      {error && <p className="error">{error}</p>}
      <ul className="news-list">
        {noticias.slice(0, 5).map((noticia, index) => (
          <li key={index}>
            <a href={noticia.link} target="_blank" rel="noopener noreferrer">
              {noticia.title}
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Welcome;