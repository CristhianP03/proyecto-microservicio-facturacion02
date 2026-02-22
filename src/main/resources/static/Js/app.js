const App = {
    API_BASE: "", // 🔥 CORRECCIÓN DEFINITIVA

    ui: {
        resultado: document.getElementById("resultado"),
        status: document.getElementById("status-badge"),
        forms: {
            venta: document.getElementById("formCrearVenta"),
            factura: document.getElementById("formGenerarFactura"),
            pago: document.getElementById("formRegistrarPago")
        }
    },

    async fetchAPI(endpoint, options = {}) {
        this.setStatus("Procesando...", "warning");
        try {
            const response = await fetch(`${this.API_BASE}${endpoint}`, options);

            if (!response.ok) {
                let errorText = `Error ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorText = errorData.message || errorText;
                } catch {}
                throw new Error(errorText);
            }

            this.setStatus("Sincronizado", "success");
            return await response.json();

        } catch (error) {
            this.notify(error.message, "error");
            this.setStatus("Error", "error");
            return null;
        }
    },

    setStatus(text, type) {
        this.ui.status.innerText = text;
        this.ui.status.className = `badge ${type}`;
    },

    notify(msg, type) {
        this.ui.resultado.innerHTML = `<div class="mensaje ${type}">${msg}</div>`;
    }
};

const crearVenta = async (e) => {
    e.preventDefault();
    const payload = {
        idCliente: parseInt(document.getElementById("idCliente").value),
        total: parseFloat(document.getElementById("totalVenta").value)
    };

    const data = await App.fetchAPI("/ventas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (data) renderTabla([data]);
};

const generarFactura = async (e) => {
    e.preventDefault();
    const idVenta = document.getElementById("idVentaFactura").value;

    const data = await App.fetchAPI(`/ventas/${idVenta}/generar-factura`, {
        method: "POST"
    });

    if (data) renderTabla([data]);
};

const registrarPago = async (e) => {
    e.preventDefault();
    const idFactura = document.getElementById("idFacturaPago").value;

    const payload = {
        monto: parseFloat(document.getElementById("montoPago").value),
        metodoPago: "TRANSFERENCIA"
    };

    const data = await App.fetchAPI(`/facturas/${idFactura}/pagos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (data) {
        App.notify("Pago registrado con éxito.", "info");
        setTimeout(() => listarFacturas(), 2000);
    }
};

const listarVentas = async () => {
    const data = await App.fetchAPI("/ventas");
    if (data) renderTabla(data);
};

const listarFacturas = async () => {
    const data = await App.fetchAPI("/facturas");
    if (data) renderTabla(data);
};

function renderTabla(data) {
    if (!data || data.length === 0)
        return App.notify("No hay registros.", "info");

    const columnas = Object.keys(data[0]).filter(
        key => typeof data[0][key] !== "object"
    );

    let html = `<table><thead><tr>${
        columnas.map(c => `<th>${c.toUpperCase()}</th>`).join("")
    }</tr></thead><tbody>`;

    data.forEach(row => {
        html += `<tr>${
            columnas.map(c => `<td>${row[c]}</td>`).join("")
        }</tr>`;
    });

    html += `</tbody></table>`;
    App.ui.resultado.innerHTML = html;
}

document.addEventListener("DOMContentLoaded", () => {
    App.ui.forms.venta.addEventListener("submit", crearVenta);
    App.ui.forms.factura.addEventListener("submit", generarFactura);
    App.ui.forms.pago.addEventListener("submit", registrarPago);

    document.getElementById("btnListarVentas")
        .addEventListener("click", listarVentas);

    document.getElementById("btnListarFacturas")
        .addEventListener("click", listarFacturas);
});