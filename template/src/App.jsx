import React, { useEffect } from 'react';
import {
  Routes,
  Route,
  useLocation
} from 'react-router-dom';

import './css/style.css';

import './charts/ChartjsConfig';

// Import pages
import Dashboard from './pages/Dashboard';
import BuscarHc from './pages/BuscarHc'
import RegistrarHc from './pages/RegistrarHc'
import { ToastContainer } from 'react-toastify';

function App() {

  const location = useLocation();

  useEffect(() => {
    document.querySelector('html').style.scrollBehavior = 'auto'
    window.scroll({ top: 0 })
    document.querySelector('html').style.scrollBehavior = ''
  }, [location.pathname]); // triggered on route change

  return (
    <>
      <Routes>
        <Route exact path="/" element={<Dashboard />} />
        <Route exact path="/buscar-hc" element={<BuscarHc/>}/>
        <Route exact path="/registrar-hc" element={<RegistrarHc/>}/>
      </Routes>

        <ToastContainer
        position="top-right" 
        autoClose={4000} 
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored" /* Puedes usar 'light', 'dark' o 'colored' */
      />

    </>
  );
}

export default App;
