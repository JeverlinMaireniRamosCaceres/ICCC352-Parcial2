async function cargarResumen() {

    const res = await fetch('/api/admin/resumen');

    if (!res.ok) {
        console.error('Error al cargar el resumen del dashboard');
        return;
    }

    const data = await res.json();

    document.getElementById('totalEventos').textContent        = data.totalEventos;
    document.getElementById('eventosProximos').textContent     = data.eventosProximos;
    document.getElementById('totalUsuarios').textContent       = data.totalUsuarios;
    document.getElementById('inscripciones30dias').textContent = data.inscripciones30dias;
}

document.addEventListener('DOMContentLoaded', cargarResumen);