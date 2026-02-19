const API = "http://localhost:8081";

// =============================
// CREAR VENTA
// =============================
async function crearVenta() {
    const idCliente = document.getElementById("idCliente").value;
    const total = document.getElementById("totalVenta").value;

    const venta = {
        idCliente: parseInt(idCliente),
        total: parseFloat(total)
    };

    const response = await fetch(`${API}/ventas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(venta)
    });

    const data = await response.json();
    mostrarResultado([data]);
}

// =============================
// LISTAR VENTAS
// =============================
async function listarVentas() {
    const response = await fetch(`${API}/ventas`);
    const data = await response.json();
    mostrarResultado(data);
}

// =============================
// GENERAR FACTURA
// =============================
async function generarFactura() {
    const idVenta = document.getElementById("idVentaFactura").value;

    const response = await fetch(`${API}/facturas/generar/${idVenta}`, {
        method: "POST"
    });

    const data = await response.json();
    mostrarResultado([data]);
}

// =============================
// BUSCAR FACTURAS POR VENTA
// =============================
async function buscarFacturasPorVenta() {
    const idVenta = document.getElementById("idVentaBuscar").value;

    const response = await fetch(`${API}/facturas/venta/${idVenta}`);
    const data = await response.json();
    mostrarResultado(data);
}

// =============================
// GENERAR TABLA
// =============================
function mostrarResultado(data) {
    const resultado = document.getElementById("resultado");

    if (!Array.isArray(data) || data.length === 0) {
        resultado.innerHTML = '<div class="mensaje">No hay datos</div>';
        return;
    }

    const columnas = Object.keys(data[0]);

    let html = '<table><thead><tr>';
    columnas.forEach(col => html += `<th>${col}</th>`);
    html += '</tr></thead><tbody>';

    data.forEach(fila => {
        html += '<tr>';
        columnas.forEach(col => {
            html += `<td>${formatearSiEsFecha(fila[col])}</td>`;
        });
        html += '</tr>';
    });

    html += '</tbody></table>';
    resultado.innerHTML = html;
}

// =============================
// FORMATEO FECHA
// =============================
function formatearSiEsFecha(valor) {
    if (!valor) return "";

    if (typeof valor === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(valor)) {
        return new Date(valor).toLocaleString();
    }

    return valor;
}
