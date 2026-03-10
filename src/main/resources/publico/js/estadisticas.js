async function cargarEstadisticas() {

    const eventoId = window.__EVENTO_ID__;

    const res = await fetch(`/api/eventos/${eventoId}/estadisticas`);

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        mostrarError(err.error || `Error ${res.status} al cargar estadísticas.`);
        ocultarSpinner('spinnerInscripciones');
        ocultarSpinner('spinnerAsistencia');
        return;
    }

    const data = await res.json();

    actualizarInfoEvento(data.evento);
    actualizarTarjetas(data);
    renderBarras('graficoInscripciones', 'spinnerInscripciones', 'placeholderInscripciones', data.inscripcionesPorDia);
    renderLinea('graficoAsistencia', 'spinnerAsistencia', 'placeholderAsistencia', data.asistenciaPorHora);
}

function actualizarInfoEvento(ev) {
    document.getElementById('eventoTitulo').textContent      = ev.titulo;
    document.getElementById('eventoDescripcion').textContent = ev.descripcion;
    document.getElementById('eventoFecha').textContent       = ev.fecha;
    document.getElementById('eventoHora').textContent        = ev.hora;
    document.getElementById('eventoLugar').textContent       = ev.lugar;
    document.getElementById('eventoOrganizador').textContent = ev.organizador;
}

function actualizarTarjetas(data) {
    document.getElementById('totalInscritos').textContent  = data.totalInscritos;
    document.getElementById('totalAsistentes').textContent = data.totalAsistentes;
    document.getElementById('pctAsistencia').textContent   = data.porcentajeAsistencia + '%';
    document.getElementById('pctOcupacion').textContent    = data.porcentajeOcupacion  + '%';
}

function renderBarras(canvasId, spinnerId, placeholderId, datos) {
    ocultarSpinner(spinnerId);

    const labels = Object.keys(datos);
    const values = Object.values(datos);

    if (labels.length === 0) {
        document.getElementById(placeholderId).classList.remove('d-none');
        return;
    }

    const canvas = document.getElementById(canvasId);
    canvas.classList.remove('d-none');

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: 'rgba(13, 110, 253, 0.7)',
                borderColor: 'rgb(13, 110, 253)',
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } },
                x: { grid: { display: false } }
            }
        }
    });
}

function renderLinea(canvasId, spinnerId, placeholderId, datos) {
    ocultarSpinner(spinnerId);

    const labels = Object.keys(datos);
    const values = Object.values(datos);

    if (labels.length === 0) {
        document.getElementById(placeholderId).classList.remove('d-none');
        return;
    }

    const canvas = document.getElementById(canvasId);
    canvas.classList.remove('d-none');

    new Chart(canvas, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                fill: true,
                backgroundColor: 'rgba(25, 135, 84, 0.15)',
                borderColor: 'rgb(25, 135, 84)',
                borderWidth: 2,
                pointBackgroundColor: 'rgb(25, 135, 84)',
                pointRadius: 5,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } },
                x: { grid: { display: false } }
            }
        }
    });
}

function ocultarSpinner(id) {
    document.getElementById(id).classList.add('d-none');
}

function mostrarError(mensaje) {
    document.getElementById('mensajeError').textContent = mensaje;
    document.getElementById('alertaError').classList.remove('d-none');
}

document.addEventListener('DOMContentLoaded', cargarEstadisticas);