// ═══════════════════════════════════════════════════════════════
// DATOS DE PRUEBA
// Los id_producto coinciden exactamente con los del microservicio
// de productos (http://40.82.168.11:8090/api/productos).
// El stock se pone en 99 para que no bloquee las pruebas.
// ═══════════════════════════════════════════════════════════════
const CLIENTES_TEST = [
    { cedula: "0912345678", nombre: "Carlos Mendoza Pérez",   direccion: "Av. Quito 123, Quevedo", telefono: "0991234567" },
    { cedula: "1234567890", nombre: "María Torres Alvarado",  direccion: "Calle 7 y Av. 3",        telefono: "0987654321" },
    { cedula: "0987654321", nombre: "Luis Ramírez Castro",    direccion: "",                        telefono: ""           },
    { cedula: "1098765432", nombre: "Ana González Mora",      direccion: "Calle Las Flores 45",     telefono: "0923456789" },
    { cedula: "0876543219", nombre: "Roberto Silva Vera",     direccion: "Urb. El Paraíso Mz 5",   telefono: ""           },
];

const PRODUCTOS_TEST = [
    { id_producto: 27, nombre: "AMD Ryzen 7 7800X3D",      precio: 383.99, stock: 99 },
    { id_producto: 26, nombre: "AMD Ryzen 9 9950X3D",      precio: 675.49, stock: 99 },
    { id_producto: 25, nombre: "Intel Core Ultra 9 285K",  precio: 559.77, stock: 99 },
    { id_producto: 24, nombre: "AMD Ryzen 5 9600X",        precio: 186.85, stock: 99 },
    { id_producto: 23, nombre: "AMD Ryzen 9 9900X",        precio: 375.76, stock: 99 },
    { id_producto: 21, nombre: "AMD Ryzen 7 9800X3D",      precio: 464.36, stock: 99 },
    { id_producto: 20, nombre: "AMD Ryzen 7 5800XT",       precio: 207.95, stock: 99 },
    { id_producto: 19, nombre: "AMD Ryzen 5 5500",         precio: 84.49,  stock: 99 },
];

// ═══════════════════════════════════════════════════════════════
// ESTADO GLOBAL
// ═══════════════════════════════════════════════════════════════
let modoTest   = false;
let cajero     = null;
let clienteAct = null;
let carrito    = [];
let selIdx     = -1;
let ventaAct   = null;
let cliData    = [];
let prodData   = [];

// ═══════════════════════════════════════════════════════════════
// LOGIN
// ═══════════════════════════════════════════════════════════════
document.addEventListener('keydown', e => {
    if (document.getElementById('ls').style.display !== 'none' && e.key === 'Enter') {
        doLogin(false);
    }
});

async function doLogin(test) {
    const errEl = document.getElementById('lerr');
    errEl.style.display = 'none';

    let username, password;

    if (test) {
        // Modo prueba: auto-login con cajero_prueba sin pedir credenciales
        username = 'cajero_prueba';
        password = '1234';
    } else {
        username = document.getElementById('lu').value.trim();
        password = document.getElementById('lp2').value.trim();
        if (!username || !password) {
            errEl.textContent = 'Ingrese usuario y contraseña.';
            errEl.style.display = 'block';
            return;
        }
    }

    try {
        const res = await fetch('/cajeros/login', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ username, password })
        });

        if (res.ok) {
            cajero  = await res.json();
            modoTest = test;
            await iniciarApp();
        } else {
            errEl.textContent = test
                ? 'Error iniciando modo prueba. Verifique que el servidor esté corriendo.'
                : 'Usuario o contraseña incorrectos.';
            errEl.style.display = 'block';
        }
    } catch {
        errEl.textContent = 'Error de conexión con el servidor.';
        errEl.style.display = 'block';
    }
}

async function iniciarApp() {
    document.getElementById('ls').style.display = 'none';
    document.getElementById('ms').style.display = 'block';

    // Iniciales para el avatar
    const ini = cajero.nombreCompleto
        .split(' ')
        .map(n => n[0])
        .join('')
        .substring(0, 2)
        .toUpperCase();

    document.getElementById('uav').textContent  = ini;
    document.getElementById('uname').textContent = cajero.nombreCompleto;
    document.getElementById('cch').textContent   = 'CAJA ' + cajero.numeroCaja;

    if (modoTest) {
        document.getElementById('tb').style.display    = 'block';
        document.getElementById('tch').style.display   = 'inline-flex';
        document.getElementById('bborra').style.display = 'block';
        cliData  = CLIENTES_TEST;
        prodData = PRODUCTOS_TEST;
    } else {
        document.getElementById('tb').style.display    = 'none';
        document.getElementById('tch').style.display   = 'none';
        document.getElementById('bborra').style.display = 'none';
        await cargarDatosReales();
    }

    llenarSelectClientes();
    llenarSelectProductos();
}

// ═══════════════════════════════════════════════════════════════
// CARGA DE DATOS REALES (modo normal)
// El frontend llama a su propio backend (/externos/...)
// y el backend llama al microservicio externo.
// Esto evita problemas de CORS y mantiene las URLs en el servidor.
// ═══════════════════════════════════════════════════════════════
async function cargarDatosReales() {
    // Clientes
    try {
        const r = await fetch('/externos/clientes');
        if (r.ok) {
            cliData = await r.json();
        } else {
            showToast('No se pudo cargar clientes del microservicio.', 'warn');
            cliData = [];
        }
    } catch {
        showToast('Error conectando al microservicio de clientes.', 'warn');
        cliData = [];
    }

    // Productos — el backend llama a http://40.82.168.11:8090/api/productos
    // El JSON devuelve id_producto (no id), precio, stock, nombre
    try {
        const r = await fetch('/externos/productos');
        if (r.ok) {
            const raw = await r.json();
            prodData = raw.map(p => ({
                id_producto: p.id_producto,
                nombre:      p.nombre,
                precio:      parseFloat(p.precio),
                stock:       parseInt(p.stock)
            }));
        } else {
            showToast('No se pudo cargar productos del microservicio.', 'warn');
            prodData = [];
        }
    } catch {
        showToast('Error conectando al microservicio de productos.', 'warn');
        prodData = [];
    }
}

function doLogout() {
    cajero = null; clienteAct = null; carrito = [];
    selIdx = -1;   ventaAct   = null;
    limpiar();
    document.getElementById('ms').style.display = 'none';
    document.getElementById('ls').style.display = 'flex';
    document.getElementById('lu').value  = '';
    document.getElementById('lp2').value = '';
    closeDD();
}

function toggleDD() { document.getElementById('udd').classList.toggle('open'); }
function closeDD()  { document.getElementById('udd').classList.remove('open'); }
document.addEventListener('click', e => {
    if (!e.target.closest('.ubtn')) closeDD();
});

// ═══════════════════════════════════════════════════════════════
// CLIENTES
// ═══════════════════════════════════════════════════════════════
function llenarSelectClientes() {
    const sel = document.getElementById('scli');
    sel.innerHTML = '<option value="">— Seleccionar cliente —</option>';
    cliData.forEach(c => {
        const o = document.createElement('option');
        o.value       = c.cedula;
        o.textContent = c.cedula + ' — ' + c.nombre;
        sel.appendChild(o);
    });
}

function onCli() {
    const ced = document.getElementById('scli').value;
    const cc  = document.getElementById('clicard');
    if (!ced) { clienteAct = null; cc.style.display = 'none'; return; }

    clienteAct = cliData.find(c => c.cedula === ced);
    if (clienteAct) {
        document.getElementById('cnomb').textContent = clienteAct.nombre     || '—';
        document.getElementById('cced').textContent  = clienteAct.cedula     || '—';
        document.getElementById('cdir').textContent  = clienteAct.direccion  || '—';
        document.getElementById('ctel').textContent  = clienteAct.telefono   || '—';
        cc.style.display = 'grid';
    }
}

// ═══════════════════════════════════════════════════════════════
// PRODUCTOS
// IMPORTANTE: el campo identificador es id_producto (no id).
// El JSON del microservicio usa ese nombre de campo.
// ═══════════════════════════════════════════════════════════════
function llenarSelectProductos() {
    const sel = document.getElementById('sprod');
    sel.innerHTML = '<option value="">— Seleccionar producto —</option>';
    prodData.forEach(p => {
        const o = document.createElement('option');
        o.value       = p.id_producto;
        o.textContent = p.nombre;
        sel.appendChild(o);
    });
}

function onProd() {
    document.getElementById('cerr').style.display = 'none';
    const id = parseInt(document.getElementById('sprod').value);
    if (!id) return;
    const p = prodData.find(x => x.id_producto === id);
    if (p) {
        document.getElementById('icant').max   = p.stock;
        document.getElementById('icant').value = 1;
    }
}

function addProd() {
    const id   = parseInt(document.getElementById('sprod').value);
    const cant = parseInt(document.getElementById('icant').value) || 0;
    const ee   = document.getElementById('cerr');
    ee.style.display = 'none';

    if (!id)     { showToast('Seleccione un producto.', 'err'); return; }
    if (cant<=0) { showToast('La cantidad debe ser mayor a 0.', 'err'); return; }

    const p  = prodData.find(x => x.id_producto === id);
    if (!p) return;

    const enCarrito    = carrito.find(x => x.idProducto === id);
    const totalCant    = enCarrito ? enCarrito.cantidad + cant : cant;

    if (totalCant > p.stock) {
        ee.textContent   = `Stock disponible: ${p.stock}. Ya tienes ${enCarrito ? enCarrito.cantidad : 0} en el carrito.`;
        ee.style.display = 'block';
        return;
    }

    if (enCarrito) {
        enCarrito.cantidad += cant;
        enCarrito.total     = +(enCarrito.precioUnitario * enCarrito.cantidad).toFixed(2);
    } else {
        carrito.push({
            idProducto:     p.id_producto,
            nombreProducto: p.nombre,
            precioUnitario: p.precio,
            cantidad:       cant,
            total:          +(p.precio * cant).toFixed(2)
        });
    }

    renderCarrito();
    recalc();
    document.getElementById('sprod').value = '';
    document.getElementById('icant').value = 1;
}

function delProd() {
    if (selIdx < 0) return;
    carrito.splice(selIdx, 1);
    selIdx = -1;
    document.getElementById('bdel').disabled = true;
    renderCarrito();
    recalc();
}

function renderCarrito() {
    const tbody = document.getElementById('ctbody');
    tbody.innerHTML = '';

    if (!carrito.length) {
        tbody.innerHTML = '<tr><td colspan="5" class="te">No hay productos agregados</td></tr>';
        return;
    }

    carrito.forEach((p, i) => {
        const tr = document.createElement('tr');
        if (i === selIdx) tr.classList.add('rsel');
        tr.innerHTML = `
      <td class="mc">${p.idProducto}</td>
      <td>${p.nombreProducto}</td>
      <td class="mc">$${p.precioUnitario.toFixed(2)}</td>
      <td class="mc">${p.cantidad}</td>
      <td class="mc">$${p.total.toFixed(2)}</td>
    `;
        tr.addEventListener('click', () => {
            selIdx = i;
            document.getElementById('bdel').disabled = false;
            renderCarrito();
        });
        tbody.appendChild(tr);
    });
}

function recalc() {
    const sub = +carrito.reduce((a, p) => a + p.total, 0).toFixed(2);
    const iva = +(sub * 0.15).toFixed(2);
    const tot = +(sub + iva).toFixed(2);
    document.getElementById('tsub').textContent = '$' + sub.toFixed(2);
    document.getElementById('tiva').textContent = '$' + iva.toFixed(2);
    document.getElementById('ttot').textContent = '$' + tot.toFixed(2);
    document.getElementById('ipago').min = tot.toFixed(2);
}

function getTotal() {
    return +(carrito.reduce((a, p) => a + p.total, 0) * 1.15).toFixed(2);
}

function valPago() {
    const v   = parseFloat(document.getElementById('ipago').value) || 0;
    const tot = getTotal();
    document.getElementById('perr').style.display = (v > 0 && v < tot) ? 'block' : 'none';
}

// ═══════════════════════════════════════════════════════════════
// VENTA
// esModoTest se envía al backend para marcar la venta.
// El backend lo persiste en la columna es_modo_test de la tabla ventas.
// El endpoint /inventario/ventas/{id}/productos-vendidos
// rechazará cualquier venta con esModoTest = true.
// ═══════════════════════════════════════════════════════════════
function showConf() {
    if (!clienteAct)     { showToast('Seleccione un cliente.', 'err'); return; }
    if (!carrito.length) { showToast('Agregue al menos un producto.', 'err'); return; }
    const fp = document.getElementById('spago').value;
    if (!fp)             { showToast('Seleccione la forma de pago.', 'err'); return; }
    const vp  = parseFloat(document.getElementById('ipago').value) || 0;
    const tot = getTotal();
    if (vp < tot)        { showToast('El valor pagado no puede ser menor al total.', 'err'); return; }

    const sub = +carrito.reduce((a, p) => a + p.total, 0).toFixed(2);
    const iva = +(sub * 0.15).toFixed(2);

    const testBadge = modoTest
        ? `<div style="margin-top:10px;padding:8px;background:#fffbeb;border-radius:6px;
         font-size:11px;color:#92400e;font-weight:600;text-align:center">
         🧪 VENTA DE PRUEBA — no afecta inventario ni stock</div>`
        : '';

    document.getElementById('minfo').innerHTML = `
    <div class="mir"><span>Cliente</span><span>${clienteAct.nombre}</span></div>
    <div class="mir"><span>Cédula</span><span>${clienteAct.cedula}</span></div>
    <div class="mir"><span>Productos</span><span>${carrito.length} ítem(s)</span></div>
    <div class="mir"><span>Subtotal</span><span>$${sub.toFixed(2)}</span></div>
    <div class="mir"><span>IVA 15%</span><span>$${iva.toFixed(2)}</span></div>
    <div class="mir"><span>Forma de pago</span><span>${fp}</span></div>
    <div class="mir mtr"><span>Total</span><span>$${tot.toFixed(2)}</span></div>
    ${testBadge}
  `;
    document.getElementById('modal').classList.add('open');
}

function closeM(id) {
    document.getElementById(id).classList.remove('open');
}

async function confVenta() {
    closeM('modal');

    const sub = +carrito.reduce((a, p) => a + p.total, 0).toFixed(2);
    const iva = +(sub * 0.15).toFixed(2);
    const tot = +(sub + iva).toFixed(2);
    const fp  = document.getElementById('spago').value;
    const vp  = parseFloat(document.getElementById('ipago').value);

    const body = {
        idCajero:         cajero.idCajero,
        cedulaCliente:    clienteAct.cedula,
        nombreCliente:    clienteAct.nombre,
        direccionCliente: clienteAct.direccion || '',
        telefonoCliente:  clienteAct.telefono  || '',
        subtotal:         sub,
        impuestos:        iva,
        total:            tot,
        formaPago:        fp,
        valorPagado:      vp,
        esModoTest:       modoTest,   // ← marca la venta como prueba o real
        detalles: carrito.map(p => ({
            idProducto:     p.idProducto,
            nombreProducto: p.nombreProducto,
            precioUnitario: p.precioUnitario,
            cantidad:       p.cantidad
        }))
    };

    try {
        const res = await fetch('/ventas', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(body)
        });

        if (res.ok) {
            ventaAct = await res.json();
            document.getElementById('nreg').textContent   = ventaAct.numeroRegistro;
            document.getElementById('rtag').style.display = 'block';
            document.getElementById('bfac').style.display = 'block';
            document.getElementById('bventa').disabled    = true;
            showToast('Venta registrada correctamente.', 'ok');
        } else {
            showToast('Error al registrar la venta.', 'err');
        }
    } catch {
        showToast('Error de conexión con el servidor.', 'err');
    }
}

// ═══════════════════════════════════════════════════════════════
// FACTURA
// ═══════════════════════════════════════════════════════════════
async function genFac() {
    if (!ventaAct) return;
    try {
        const res = await fetch(`/ventas/${ventaAct.idVenta}/generar-factura`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (res.ok) {
            const f = await res.json();
            renderFactura(f);
            document.getElementById('bpdf').style.display = 'block';
            document.getElementById('bfac').disabled      = true;
            showToast('Factura generada correctamente.', 'ok');
        } else {
            showToast('Error al generar la factura.', 'err');
        }
    } catch {
        showToast('Error de conexión con el servidor.', 'err');
    }
}

function renderFactura(f) {
    const detalles = f.detalles || [];
    const filas = detalles.map(d => `
    <tr>
      <td>${d.cantidad}</td>
      <td>${d.idProducto}</td>
      <td>${d.nombreProducto}</td>
      <td class="r">$${parseFloat(d.precioUnitario).toFixed(2)}</td>
      <td class="r">$${parseFloat(d.total).toFixed(2)}</td>
    </tr>
  `).join('');

    const isEfectivo = (f.formaPago || '').toUpperCase() === 'EFECTIVO';
    const fpTexto    = isEfectivo
        ? 'SIN UTILIZACION DEL SISTEMA FINANCIERO'
        : 'CON UTILIZACION DEL SISTEMA FINANCIERO';

    const watermark = modoTest
        ? `<div style="text-align:center;color:#d97706;font-weight:900;font-size:10px;
         letter-spacing:3px;padding:4px;background:#fffbeb;margin-bottom:8px;
         border:1px dashed #f59e0b">
         *** FACTURA DE PRUEBA — NO VÁLIDA ***</div>`
        : '';

    document.getElementById('fhtml').innerHTML = `
    ${watermark}
    <div class="fhd">
      <div class="femp">Tienda - Componentes - CompuTech</div>
      <div class="fruc">Quevedo</div>
      <div class="fruc">R.U.C: 9999999999999</div>
    </div>
    <div class="fnbox">
      <div class="fnlbl">FACTURA</div>
      <div class="fnum">${f.numeroFactura}</div>
    </div>
    <div class="fcli">
      <div class="frow">
        <div class="fcf"><label>Nombre:</label><span>${f.nombreCliente}</span></div>
        <div class="fcf"><label>Cédula:</label><span>${f.cedulaCliente}</span></div>
      </div>
      <div class="frow">
        <div class="fcf"><label>Dirección:</label><span>${f.direccionCliente || ''}</span></div>
        <div class="fcf"><label>Teléfono:</label><span>${f.telefonoCliente || ''}</span></div>
      </div>
    </div>
    <div class="fst">Detalle de Factura</div>
    <table class="ftbl">
      <thead>
        <tr>
          <th>Cant.</th><th>ID</th><th>Descripción</th>
          <th class="r">P.Unit.</th><th class="r">P.Total</th>
        </tr>
      </thead>
      <tbody>${filas}</tbody>
    </table>
    <div class="fbot">
      <div class="fobs">
        <div><label>Observación:</label><div class="ov">${f.observacion || fpTexto}</div></div>
        <div><label>Monto $:</label><div class="mv">$${parseFloat(f.total).toFixed(2)}</div></div>
        <div><label>Forma de pago:</label><div class="fv">${fpTexto}</div></div>
      </div>
      <div>
        <div class="ftr"><span>Subtotal:</span><span>$${parseFloat(f.subtotal).toFixed(2)}</span></div>
        <div class="ftr"><span>IVA 15%:</span><span>$${parseFloat(f.impuestos).toFixed(2)}</span></div>
        <div class="ftr gd"><span>Total:</span><span>$${parseFloat(f.total).toFixed(2)}</span></div>
      </div>
    </div>
  `;

    document.getElementById('fp').style.display = 'block';
}

function expPDF() { window.print(); }

// ═══════════════════════════════════════════════════════════════
// BORRAR VENTAS DE PRUEBA
// ═══════════════════════════════════════════════════════════════
function showBorrar() {
    document.getElementById('modal-borrar').classList.add('open');
}

async function confBorrar() {
    closeM('modal-borrar');
    try {
        const res = await fetch('/test/limpiar-ventas', { method: 'DELETE' });
        if (res.ok) {
            limpiar();
            showToast('Todas las ventas de prueba fueron eliminadas.', 'ok');
        } else {
            showToast('Error al borrar las ventas de prueba.', 'err');
        }
    } catch {
        showToast('Error de conexión con el servidor.', 'err');
    }
}

// ═══════════════════════════════════════════════════════════════
// UTILIDADES
// ═══════════════════════════════════════════════════════════════
function limpiar() {
    clienteAct = null; carrito = []; selIdx = -1; ventaAct = null;

    document.getElementById('scli').value            = '';
    document.getElementById('clicard').style.display = 'none';
    document.getElementById('sprod').value            = '';
    document.getElementById('icant').value            = 1;
    document.getElementById('ctbody').innerHTML       = '<tr><td colspan="5" class="te">No hay productos agregados</td></tr>';
    document.getElementById('tsub').textContent       = '$0.00';
    document.getElementById('tiva').textContent       = '$0.00';
    document.getElementById('ttot').textContent       = '$0.00';
    document.getElementById('spago').value            = '';
    document.getElementById('ipago').value            = '';
    document.getElementById('rtag').style.display     = 'none';
    document.getElementById('bfac').style.display     = 'none';
    document.getElementById('bpdf').style.display     = 'none';
    document.getElementById('bventa').disabled        = false;
    document.getElementById('fp').style.display       = 'none';
    document.getElementById('bdel').disabled          = true;
    document.getElementById('perr').style.display     = 'none';
    document.getElementById('cerr').style.display     = 'none';
}

function showToast(msg, type) {
    const t         = document.getElementById('toast');
    t.textContent   = msg;
    t.className     = 'toast ' + type;
    t.style.display = 'block';
    setTimeout(() => { t.style.display = 'none'; }, 3500);
}