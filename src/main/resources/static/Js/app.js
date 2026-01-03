// Función principal para cargar datos desde un endpoint
async function cargarDatos(endpoint) {
    const resultado = document.getElementById("resultado");
    resultado.innerHTML = '<div class="mensaje">Cargando datos...</div>';

    try {
        const response = await fetch(`/${endpoint}`);
        if (!response.ok) {
            throw new Error("Error al obtener los datos");
        }

        const data = await response.json();

        if (!Array.isArray(data) || data.length === 0) {
            resultado.innerHTML = '<div class="mensaje">No hay datos para mostrar</div>';
            return;
        }

        resultado.innerHTML = generarTabla(data);

    } catch (error) {
        resultado.innerHTML = `<div class="mensaje">Error: ${error.message}</div>`;
    }
}

// Función para generar la tabla HTML
function generarTabla(data) {
    const columnas = Object.keys(data[0]);

    let html = '<table>';
    html += '<thead><tr>';
    columnas.forEach(col => html += `<th>${col}</th>`);
    html += '</tr></thead><tbody>';

    data.forEach(fila => {
        html += '<tr>';
        columnas.forEach(col => html += `<td>${formatearSiEsFecha(fila[col])}</td>`);
        html += '</tr>';
    });

    html += '</tbody></table>';
    return html;
}

// Función estricta: solo formatea valores que son fechas ISO 8601
function formatearSiEsFecha(valor) {
    if (valor === null || valor === undefined) return '';

    // Detecta solo strings que coincidan con formato ISO 8601 de timestamp
    if (typeof valor === 'string' && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(valor)) {
        const fecha = new Date(valor);
        return fecha.toLocaleString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    // Si no es fecha, se devuelve tal cual
    return valor;
}
