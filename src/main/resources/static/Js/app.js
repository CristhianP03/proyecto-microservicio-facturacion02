// ================================================================
// CONSTANTES
// ================================================================
// URL base para imagenes de producto.
// El navegador llama directamente a esta IP — no pasa por el backend.
const IMG_BASE = 'http://40.82.168.11:8090';
const IMG_URL  = (id) => `${IMG_BASE}/api/productos/${id}/portada/render`;

// ================================================================
// DATOS DE PRUEBA
// ================================================================
const CLIENTES_TEST = [
    { cedula: "0912345678", nombre: "Carlos Mendoza Perez",  direccion: "Av. Quito 123, Quevedo", telefono: "0991234567" },
    { cedula: "1234567890", nombre: "Maria Torres Alvarado", direccion: "Calle 7 y Av. 3",        telefono: "0987654321" },
    { cedula: "0987654321", nombre: "Luis Ramirez Castro",   direccion: "",                        telefono: ""           },
    { cedula: "1098765432", nombre: "Ana Gonzalez Mora",     direccion: "Calle Las Flores 45",     telefono: "0923456789" },
    { cedula: "0876543219", nombre: "Roberto Silva Vera",    direccion: "Urb. El Paraiso Mz 5",   telefono: ""           },
];

const PRODUCTOS_TEST = [
    { id_producto: 1,  nombre: "Ryzen 5 5600",          precio: 40.00,  stock: 30  },
    { id_producto: 2,  nombre: "Core i5-12400F",         precio: 30.00,  stock: 15  },
    { id_producto: 3,  nombre: "RTX 3060 12GB",          precio: 40.00,  stock: 10  },
    { id_producto: 4,  nombre: "SSD NVMe 1TB",           precio: 79.99,  stock: 20  },
    { id_producto: 7,  nombre: "Intel I7 12700k",        precio: 670.56, stock: 120 },
    { id_producto: 8,  nombre: "Samsung SSD 1TB",        precio: 230.00, stock: 560 },
    { id_producto: 11, nombre: "EpsonL3250",             precio: 30.00,  stock: 40  },
    { id_producto: 13, nombre: "Producto merengue",      precio: 19.99,  stock: 120 },
    { id_producto: 15, nombre: "Mouse Gamer RGB",        precio: 35.99,  stock: 50  },
    { id_producto: 16, nombre: "TECLADO",                precio: 75.00,  stock: 17  },
];

// ================================================================
// ESTADO GLOBAL
// ================================================================
let modoTest      = false;
let cajero        = null;
let clienteAct    = null;
let prodSeleccion = null;
let carrito       = [];
let selIdx        = -1;
let ventaAct      = null;
let cliData       = [];
let prodData      = [];
let facturaObj    = null;

let dropIdx = { cli: -1, prod: -1 };

// ================================================================
// LOGIN
// Reglas estrictas:
//   - Boton "Ingresar al Sistema": cualquier cajero EXCEPTO cajero_prueba
//   - Boton "Modo Prueba":         SOLO cajero_prueba / 1234
//   - Ambos botones requieren escribir credenciales (no hay auto-login)
// ================================================================
document.addEventListener('keydown', e => {
    if (document.getElementById('ls').style.display !== 'none' && e.key === 'Enter') {
        doLogin(false);
    }
});

async function doLogin(esModoTest) {
    const errEl = document.getElementById('lerr');
    errEl.style.display = 'none';

    const username = document.getElementById('lu').value.trim();
    const password = document.getElementById('lp2').value.trim();

    if (!username || !password) {
        errEl.textContent = esModoTest
            ? 'Para el modo prueba escribe: cajero_prueba / 1234'
            : 'Ingrese usuario y contrasena.';
        errEl.style.display = 'block';
        return;
    }

    if (!esModoTest && username === 'cajero_prueba') {
        errEl.textContent = 'cajero_prueba solo puede ingresar usando el boton Modo Prueba.';
        errEl.style.display = 'block';
        return;
    }

    if (esModoTest && username !== 'cajero_prueba') {
        errEl.textContent = 'El modo prueba es exclusivo para cajero_prueba.';
        errEl.style.display = 'block';
        return;
    }

    try {
        const res = await fetch('/cajeros/login', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ username, password })
        });

        if (res.ok) {
            cajero   = await res.json();
            modoTest = esModoTest;
            await iniciarApp();
        } else {
            errEl.textContent = 'Usuario o contrasena incorrectos.';
            errEl.style.display = 'block';
        }
    } catch {
        errEl.textContent = 'Error de conexion con el servidor.';
        errEl.style.display = 'block';
    }
}

async function iniciarApp() {
    document.getElementById('ls').style.display = 'none';
    document.getElementById('ms').style.display = 'block';

    const ini = cajero.nombreCompleto
        .split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    document.getElementById('uav').textContent   = ini;
    document.getElementById('uname').textContent = cajero.nombreCompleto;
    document.getElementById('cch').textContent   = 'CAJA ' + cajero.numeroCaja;

    if (modoTest) {
        document.getElementById('tb').style.display     = 'block';
        document.getElementById('tch').style.display    = 'inline-flex';
        document.getElementById('bborra').style.display = 'block';
        cliData  = CLIENTES_TEST;
        prodData = PRODUCTOS_TEST;
    } else {
        document.getElementById('tb').style.display     = 'none';
        document.getElementById('tch').style.display    = 'none';
        document.getElementById('bborra').style.display = 'none';
        await cargarDatosReales();
    }

    renderDropClientes('');
    renderDropProductos('');
}

// ================================================================
// CARGA DE DATOS REALES
// ================================================================
async function cargarDatosReales() {
    try {
        const r = await fetch('/externos/clientes');
        if (r.ok) {
            const raw = await r.json();
            cliData = raw.map(c => ({
                cedula:    c.cedula,
                nombre:    (c.nombre + ' ' + c.apellido).trim(),
                direccion: c.direccion || '',
                telefono:  c.telefono  || ''
            }));
        } else { showToast('No se pudo cargar clientes.', 'warn'); cliData = []; }
    } catch { showToast('Error al conectar con microservicio de clientes.', 'warn'); cliData = []; }

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
        } else { showToast('No se pudo cargar productos.', 'warn'); prodData = []; }
    } catch { showToast('Error al conectar con microservicio de productos.', 'warn'); prodData = []; }
}

function doLogout() {
    cajero = null; clienteAct = null; prodSeleccion = null;
    carrito = []; selIdx = -1; ventaAct = null; facturaObj = null;
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
    if (!e.target.closest('.ubtn'))     closeDD();
    if (!e.target.closest('#sdd-cli'))  cerrarDrop('cli');
    if (!e.target.closest('#sdd-prod')) cerrarDrop('prod');
});

// ================================================================
// SEARCHABLE DROPDOWN — logica generica
// ================================================================
function abrirDrop(key) {
    const val = document.getElementById('inp-' + key).value;
    if (key === 'cli')  renderDropClientes(val);
    if (key === 'prod') renderDropProductos(val);
    document.getElementById('list-' + key).classList.add('open');
    dropIdx[key] = -1;
}

function cerrarDrop(key) {
    document.getElementById('list-' + key).classList.remove('open');
    dropIdx[key] = -1;
}

function navDrop(event, key) {
    const list  = document.getElementById('list-' + key);
    const items = list.querySelectorAll('.sdd-item');
    if (!items.length) return;
    if (event.key === 'ArrowDown') {
        event.preventDefault();
        dropIdx[key] = Math.min(dropIdx[key] + 1, items.length - 1);
        actualizarActivosDrop(key, items);
    } else if (event.key === 'ArrowUp') {
        event.preventDefault();
        dropIdx[key] = Math.max(dropIdx[key] - 1, 0);
        actualizarActivosDrop(key, items);
    } else if (event.key === 'Enter') {
        event.preventDefault();
        if (dropIdx[key] >= 0 && items[dropIdx[key]]) items[dropIdx[key]].click();
    } else if (event.key === 'Escape') {
        cerrarDrop(key);
    }
}

function actualizarActivosDrop(key, items) {
    items.forEach((it, i) => {
        it.classList.toggle('active', i === dropIdx[key]);
        if (i === dropIdx[key]) it.scrollIntoView({ block: 'nearest' });
    });
}

// ================================================================
// CLIENTES — dropdown muestra solo cedula
// ================================================================
function filtrarClientes() {
    const val = document.getElementById('inp-cli').value.trim();
    if (clienteAct && val !== clienteAct.cedula) {
        clienteAct = null;
        document.getElementById('clicard').style.display = 'none';
        document.getElementById('inp-cli').classList.remove('selected');
    }
    renderDropClientes(val);
    abrirDrop('cli');
}

function renderDropClientes(filtro) {
    const list = document.getElementById('list-cli');
    const q    = filtro.toLowerCase().trim();
    const res  = q
        ? cliData.filter(c => c.cedula.includes(q) || c.nombre.toLowerCase().includes(q))
        : cliData;
    if (!res.length) {
        list.innerHTML = '<div class="sdd-empty">No se encontraron clientes</div>';
        return;
    }
    list.innerHTML = res.map(c =>
        `<div class="sdd-item" onclick="seleccionarCliente('${c.cedula}')">${c.cedula}</div>`
    ).join('');
}

function seleccionarCliente(cedula) {
    clienteAct = cliData.find(c => c.cedula === cedula);
    if (!clienteAct) return;
    const inp = document.getElementById('inp-cli');
    inp.value = clienteAct.cedula;
    inp.classList.add('selected');
    cerrarDrop('cli');
    document.getElementById('cnomb').textContent = clienteAct.nombre    || '-';
    document.getElementById('cced').textContent  = clienteAct.cedula    || '-';
    document.getElementById('cdir').textContent  = clienteAct.direccion || '-';
    document.getElementById('ctel').textContent  = clienteAct.telefono  || '-';
    document.getElementById('clicard').style.display = 'grid';
}

// ================================================================
// PRODUCTOS — dropdown con imagen + nombre + precio
// La imagen se carga desde /api/productos/{id}/portada/render
// Si falla (404, sin imagen) se muestra un placeholder gris con icono.
// ================================================================
function filtrarProductos() {
    const val = document.getElementById('inp-prod').value.trim();
    if (prodSeleccion && val !== prodSeleccion.nombre) {
        prodSeleccion = null;
        document.getElementById('inp-prod').classList.remove('selected');
    }
    renderDropProductos(val);
    abrirDrop('prod');
}

function renderDropProductos(filtro) {
    const list = document.getElementById('list-prod');
    const q    = filtro.toLowerCase().trim();
    const res  = q ? prodData.filter(p => p.nombre.toLowerCase().includes(q)) : prodData;

    if (!res.length) {
        list.innerHTML = '<div class="sdd-empty">No se encontraron productos</div>';
        return;
    }

    list.innerHTML = res.map(p => `
        <div class="sdd-item sdd-item-prod" onclick="seleccionarProducto(${p.id_producto})">
            <img
                class="sdd-prod-img"
                src="${IMG_URL(p.id_producto)}"
                alt="${p.nombre}"
                onerror="this.style.display='none';this.nextElementSibling.style.display='flex';"
            />
            <div class="sdd-prod-placeholder" style="display:none">
                <span>?</span>
            </div>
            <div class="sdd-prod-info">
                <span class="sdd-prod-nombre">${p.nombre}</span>
                <span class="sdd-prod-precio">$${p.precio.toFixed(2)}</span>
            </div>
        </div>`
    ).join('');
}

function seleccionarProducto(idProducto) {
    prodSeleccion = prodData.find(p => p.id_producto === idProducto);
    if (!prodSeleccion) return;
    const inp = document.getElementById('inp-prod');
    inp.value = prodSeleccion.nombre;
    inp.classList.add('selected');
    cerrarDrop('prod');
    document.getElementById('icant').max   = prodSeleccion.stock;
    document.getElementById('icant').value = 1;
    document.getElementById('cerr').style.display = 'none';
}

// ================================================================
// FORMA DE PAGO
// ================================================================
function onFormaPago() {
    const fp = document.getElementById('spago').value;
    if (fp === 'EFECTIVO') {
        document.getElementById('bloque-pago').style.display = 'block';
        document.getElementById('ipago').value               = '';
        document.getElementById('perr').style.display        = 'none';
    } else {
        document.getElementById('bloque-pago').style.display = 'none';
        document.getElementById('ipago').value               = '';
        document.getElementById('perr').style.display        = 'none';
    }
}

function valPago() {
    const v   = parseFloat(document.getElementById('ipago').value) || 0;
    const tot = getTotal();
    document.getElementById('perr').style.display = (v > 0 && v < tot) ? 'block' : 'none';
}

// ================================================================
// CARRITO — con columna de imagen
// ================================================================
function addProd() {
    const cant = parseInt(document.getElementById('icant').value) || 0;
    const ee   = document.getElementById('cerr');
    ee.style.display = 'none';

    if (!prodSeleccion) { showToast('Seleccione un producto.', 'err'); return; }
    if (cant <= 0)      { showToast('La cantidad debe ser mayor a 0.', 'err'); return; }

    const p         = prodSeleccion;
    const enCarrito = carrito.find(x => x.idProducto === p.id_producto);
    const totalCant = enCarrito ? enCarrito.cantidad + cant : cant;

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

    document.getElementById('inp-prod').value = '';
    document.getElementById('inp-prod').classList.remove('selected');
    prodSeleccion = null;
    document.getElementById('icant').value = 1;
    renderCarrito();
    recalc();
}

function delProd() {
    if (selIdx < 0) return;
    carrito.splice(selIdx, 1);
    selIdx = -1;
    document.getElementById('bdel').disabled = true;
    renderCarrito();
    recalc();
}

// La tabla ahora tiene 6 columnas: Imagen | ID | Producto | P.Unit. | Cant. | Total
function renderCarrito() {
    const tbody = document.getElementById('ctbody');
    tbody.innerHTML = '';

    if (!carrito.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="te">No hay productos agregados</td></tr>';
        return;
    }

    carrito.forEach((p, i) => {
        const tr = document.createElement('tr');
        if (i === selIdx) tr.classList.add('rsel');

        tr.innerHTML = `
            <td class="mc td-img">
                <img
                    class="cart-prod-img"
                    src="${IMG_URL(p.idProducto)}"
                    alt="${p.nombreProducto}"
                    onerror="this.style.display='none';this.nextElementSibling.style.display='flex';"
                />
                <div class="cart-prod-placeholder" style="display:none"><span>?</span></div>
            </td>
            <td class="mc">${p.idProducto}</td>
            <td>${p.nombreProducto}</td>
            <td class="mc">$${p.precioUnitario.toFixed(2)}</td>
            <td class="mc">${p.cantidad}</td>
            <td class="mc">$${p.total.toFixed(2)}</td>`;

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

// ================================================================
// VENTA
// ================================================================
function showConf() {
    if (!clienteAct)     { showToast('Seleccione un cliente.', 'err'); return; }
    if (!carrito.length) { showToast('Agregue al menos un producto.', 'err'); return; }
    const fp = document.getElementById('spago').value;
    if (!fp)             { showToast('Seleccione la forma de pago.', 'err'); return; }

    const tot = getTotal();
    let vp;
    if (fp === 'EFECTIVO') {
        vp = parseFloat(document.getElementById('ipago').value) || 0;
        if (vp < tot) { showToast('El valor entregado no puede ser menor al total.', 'err'); return; }
    } else {
        vp = tot;
    }

    const sub    = +carrito.reduce((a, p) => a + p.total, 0).toFixed(2);
    const iva    = +(sub * 0.15).toFixed(2);
    const vuelto = +(vp - tot).toFixed(2);

    const filaVuelto = (fp === 'EFECTIVO')
        ? `<div class="mir" style="background:#ecfdf5;border-radius:6px;padding:6px 10px;margin-top:6px">
               <span style="font-weight:700;color:#065f46">Vuelto / Cambio</span>
               <span style="font-weight:800;color:#059669;font-family:'JetBrains Mono',monospace;font-size:15px">
                   $${vuelto.toFixed(2)}</span></div>` : '';

    const testBadge = modoTest
        ? `<div style="margin-top:10px;padding:8px;background:#fffbeb;border-radius:6px;
               font-size:11px;color:#92400e;font-weight:600;text-align:center">
               VENTA DE PRUEBA - no afecta inventario ni stock</div>` : '';

    document.getElementById('minfo').innerHTML = `
        <div class="mir"><span>Cliente</span><span>${clienteAct.nombre}</span></div>
        <div class="mir"><span>Cedula</span><span>${clienteAct.cedula}</span></div>
        <div class="mir"><span>Productos</span><span>${carrito.length} item(s)</span></div>
        <div class="mir"><span>Subtotal</span><span>$${sub.toFixed(2)}</span></div>
        <div class="mir"><span>IVA 15%</span><span>$${iva.toFixed(2)}</span></div>
        <div class="mir"><span>Forma de pago</span><span>${fp}</span></div>
        <div class="mir mtr"><span>Total</span><span>$${tot.toFixed(2)}</span></div>
        ${filaVuelto}${testBadge}`;
    document.getElementById('modal').classList.add('open');
}

function closeM(id) { document.getElementById(id).classList.remove('open'); }

async function confVenta() {
    closeM('modal');

    const sub = +carrito.reduce((a, p) => a + p.total, 0).toFixed(2);
    const iva = +(sub * 0.15).toFixed(2);
    const tot = +(sub + iva).toFixed(2);
    const fp  = document.getElementById('spago').value;
    const vp  = (fp === 'EFECTIVO')
        ? parseFloat(document.getElementById('ipago').value)
        : tot;

    const body = {
        idCajero:         cajero.idCajero,
        cedulaCliente:    clienteAct.cedula,
        nombreCliente:    clienteAct.nombre,
        direccionCliente: clienteAct.direccion || '',
        telefonoCliente:  clienteAct.telefono  || '',
        subtotal: sub, impuestos: iva, total: tot,
        formaPago: fp, valorPagado: vp,
        esModoTest: modoTest,
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
    } catch { showToast('Error de conexion con el servidor.', 'err'); }
}

// ================================================================
// FACTURA
// ================================================================
async function genFac() {
    if (!ventaAct) return;
    document.getElementById('bfac').disabled = true;

    try {
        const res = await fetch(`/ventas/${ventaAct.idVenta}/generar-factura`, {
            method: 'POST', headers: { 'Content-Type': 'application/json' }
        });

        if (res.ok) {
            let f = await res.json();

            // Fallback 1: GET /ventas/{id}/factura
            if (!f.detalles || f.detalles.length === 0) {
                try {
                    const r2 = await fetch(`/ventas/${ventaAct.idVenta}/factura`);
                    if (r2.ok) {
                        const f2 = await r2.json();
                        if (f2.detalles && f2.detalles.length > 0) f = f2;
                    }
                } catch { /* ignora */ }
            }

            // Fallback 2: GET /facturas/{id}
            if ((!f.detalles || f.detalles.length === 0) && f.idFactura) {
                try {
                    const r3 = await fetch(`/facturas/${f.idFactura}`);
                    if (r3.ok) {
                        const f3 = await r3.json();
                        if (f3.detalles && f3.detalles.length > 0) f = f3;
                    }
                } catch { /* ignora */ }
            }

            facturaObj = f;
            renderFacturaPrevia(f);

            document.getElementById('bpdf').style.display   = 'block';
            document.getElementById('bnueva').style.display = 'block';
            document.getElementById('bfac').style.display   = 'none';

            showToast('Factura generada. Descarga el PDF o inicia una nueva venta.', 'ok');
        } else {
            document.getElementById('bfac').disabled = false;
            showToast('Error al generar la factura.', 'err');
        }
    } catch {
        document.getElementById('bfac').disabled = false;
        showToast('Error de conexion con el servidor.', 'err');
    }
}

function renderFacturaPrevia(f) {
    const detalles   = f.detalles || [];
    const isEfectivo = (f.formaPago || '').toUpperCase() === 'EFECTIVO';
    const fpTexto    = isEfectivo
        ? 'SIN UTILIZACION DEL SISTEMA FINANCIERO'
        : 'CON UTILIZACION DEL SISTEMA FINANCIERO';
    const valorPagado = parseFloat(f.valorPagado || 0);
    const totalFac    = parseFloat(f.total       || 0);
    const vuelto      = isEfectivo ? +(valorPagado - totalFac).toFixed(2) : 0;

    const filas = detalles.length
        ? detalles.map(d => `
            <tr>
                <td>${d.cantidad}</td>
                <td>${d.idProducto}</td>
                <td>${d.nombreProducto}</td>
                <td class="r">$${parseFloat(d.precioUnitario).toFixed(2)}</td>
                <td class="r">$${parseFloat(d.total).toFixed(2)}</td>
            </tr>`).join('')
        : '<tr><td colspan="5" style="text-align:center;padding:8px;color:#888">Sin detalles</td></tr>';

    const filaRecibido = isEfectivo
        ? `<div class="ftr" style="border-top:1px dashed #ccc;margin-top:4px;padding-top:4px">
               <span>Valor recibido:</span><span>$${valorPagado.toFixed(2)}</span></div>` : '';
    const filaVuelto = isEfectivo
        ? `<div class="fvuelto"><span>VUELTO / CAMBIO:</span><span>$${vuelto.toFixed(2)}</span></div>` : '';

    const watermark = modoTest
        ? `<div style="text-align:center;color:#d97706;font-weight:900;font-size:10px;
               letter-spacing:3px;padding:4px;background:#fffbeb;margin-bottom:8px;
               border:1px dashed #f59e0b">*** FACTURA DE PRUEBA - NO VALIDA ***</div>` : '';

    document.getElementById('fhtml').innerHTML = `
        ${watermark}
        <div class="fhd">
            <div class="femp">Tienda - Componentes - CompuTech</div>
            <div class="fruc">Quevedo</div>
            <div class="fruc">R.U.C: 9999999999999</div>
        </div>
        <div class="fnbox">
            <div class="fnlbl">FACTURA</div>
            <div class="fnum">${f.numeroFactura || '-'}</div>
        </div>
        <div class="fcli">
            <div class="frow">
                <div class="fcf"><label>Nombre:</label><span>${f.nombreCliente || '-'}</span></div>
                <div class="fcf"><label>Cedula:</label><span>${f.cedulaCliente || '-'}</span></div>
            </div>
            <div class="frow">
                <div class="fcf"><label>Direccion:</label><span>${f.direccionCliente || '-'}</span></div>
                <div class="fcf"><label>Telefono:</label><span>${f.telefonoCliente || '-'}</span></div>
            </div>
        </div>
        <div class="fst">Detalle de Factura</div>
        <table class="ftbl">
            <thead><tr>
                <th>Cant.</th><th>ID</th><th>Descripcion</th>
                <th class="r">P.Unit.</th><th class="r">P.Total</th>
            </tr></thead>
            <tbody>${filas}</tbody>
        </table>
        <div class="fbot">
            <div class="fobs">
                <div><label>Observacion:</label><div class="ov">${f.observacion || fpTexto}</div></div>
                <div><label>Monto $:</label><div class="mv">$${totalFac.toFixed(2)}</div></div>
                <div><label>Forma de pago:</label><div class="fv">${fpTexto}</div></div>
            </div>
            <div>
                <div class="ftr"><span>Subtotal:</span><span>$${parseFloat(f.subtotal||0).toFixed(2)}</span></div>
                <div class="ftr"><span>IVA 15%:</span><span>$${parseFloat(f.impuestos||0).toFixed(2)}</span></div>
                <div class="ftr gd"><span>Total:</span><span>$${totalFac.toFixed(2)}</span></div>
                ${filaRecibido}${filaVuelto}
            </div>
        </div>`;

    document.getElementById('fp').style.display = 'block';
    document.getElementById('fp').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// ================================================================
// EXPORTAR PDF
// ================================================================
function expPDF() {
    if (!facturaObj) { showToast('Primero genera una factura.', 'warn'); return; }

    const f           = facturaObj;
    const isEfectivo  = (f.formaPago || '').toUpperCase() === 'EFECTIVO';
    const valorPagado = parseFloat(f.valorPagado || 0);
    const totalFac    = parseFloat(f.total       || 0);
    const subtotal    = parseFloat(f.subtotal    || 0);
    const impuestos   = parseFloat(f.impuestos   || 0);
    const vuelto      = isEfectivo ? +(valorPagado - totalFac).toFixed(2) : 0;
    const fpTexto     = isEfectivo
        ? 'SIN UTILIZACION DEL SISTEMA FINANCIERO'
        : 'CON UTILIZACION DEL SISTEMA FINANCIERO';

    const fechaEmision = f.fechaEmision
        ? new Date(f.fechaEmision).toLocaleDateString('es-EC',
            { year:'numeric', month:'2-digit', day:'2-digit' })
        : new Date().toLocaleDateString('es-EC');

    const { jsPDF } = window.jspdf;
    const doc = new jsPDF({ orientation:'portrait', unit:'mm', format:'a4' });

    const PW=210; const ML=14; const MR=14; const CW=PW-ML-MR;
    let y=14;

    const NEGRO=[0,0,0]; const GRIS_OS=[50,50,50]; const GRIS_CL=[180,180,180];
    const AZUL=[37,99,235]; const VERDE=[5,150,105]; const ROJO=[220,38,38];

    const sf = (st,sz,c) => { doc.setFont('helvetica',st); doc.setFontSize(sz); doc.setTextColor(...(c||NEGRO)); };
    const ln = (x1,y1,x2,y2,c,g) => { doc.setDrawColor(...(c||GRIS_CL)); doc.setLineWidth(g||0.3); doc.line(x1,y1,x2,y2); };
    const rc = (x,y,w,h,f,s) => { doc.setFillColor(...f); if(s){doc.setDrawColor(...s);doc.setLineWidth(0.4);doc.rect(x,y,w,h,'FD');}else doc.rect(x,y,w,h,'F'); };
    const tx = (t,x,y,o) => doc.text(String(t),x,y,o||{});

    const cH=30;
    rc(ML,y,CW*0.62,cH,[245,247,250],GRIS_CL);
    sf('bold',11,NEGRO); tx('Tienda - Componentes - CompuTech',ML+4,y+7);
    sf('normal',8,GRIS_OS);
    tx('R.U.C: 9999999999999',ML+4,y+13);
    tx('Quevedo - Los Rios - Ecuador',ML+4,y+18);
    tx('Obligado a llevar contabilidad',ML+4,y+23);

    const bx=ML+CW*0.64; const bw=CW*0.36;
    rc(bx,y,bw,cH,AZUL);
    sf('bold',9,[255,255,255]);
    tx('REPRESENTACION GRAFICA DE',bx+bw/2,y+7,{align:'center'});
    tx('FACTURA ELECTRONICA',bx+bw/2,y+12,{align:'center'});
    sf('bold',18,[255,255,255]); tx('FACTURA',bx+bw/2,y+21,{align:'center'});
    sf('bold',8,[255,255,255]);  tx(f.numeroFactura||'001-001-000000001',bx+bw/2,y+27,{align:'center'});
    y+=cH+4;

    sf('normal',8,GRIS_OS); tx('Fecha de Emision: '+fechaEmision,ML+CW*0.64,y); y+=6;

    ln(ML,y,ML+CW,y,GRIS_CL,0.5); y+=4;
    sf('bold',8,GRIS_OS); tx('Nombre:',ML,y);
    sf('normal',8,NEGRO); tx(f.nombreCliente||'-',ML+18,y);
    sf('bold',8,GRIS_OS); tx('Cedula / R.U.C.:',ML+CW*0.55,y);
    sf('normal',8,NEGRO); tx(f.cedulaCliente||'-',ML+CW*0.55+28,y); y+=5;
    sf('bold',8,GRIS_OS); tx('Direccion:',ML,y);
    sf('normal',8,NEGRO); tx(f.direccionCliente||'-',ML+18,y);
    sf('bold',8,GRIS_OS); tx('Telefono:',ML+CW*0.55,y);
    sf('normal',8,NEGRO); tx(f.telefonoCliente||'-',ML+CW*0.55+18,y); y+=5;
    ln(ML,y,ML+CW,y,GRIS_CL,0.5); y+=4;

    const cWs=[18,22,82,28,28]; const cXs=[];
    let cx=ML; cWs.forEach(w=>{cXs.push(cx);cx+=w;});
    rc(ML,y,CW,7,[30,40,60]);
    sf('bold',7.5,[255,255,255]);
    ['Cantidad','Codigo','Descripcion','Precio Un.','Total'].forEach((l,i)=>{
        tx(l, i>=3?cXs[i]+cWs[i]-2:cXs[i]+2, y+4.8, {align:i>=3?'right':'left'});
    });
    y+=7;
    (f.detalles||[]).forEach((d,idx)=>{
        const rH=7; rc(ML,y,CW,rH,idx%2===0?[255,255,255]:[248,250,252]);
        sf('normal',8,NEGRO);
        tx(String(d.cantidad),                              cXs[0]+2,        y+4.8);
        tx(String(d.idProducto),                            cXs[1]+2,        y+4.8);
        const desc=String(d.nombreProducto);
        tx(desc.length>40?desc.substring(0,38)+'..':desc,  cXs[2]+2,        y+4.8);
        tx('$'+parseFloat(d.precioUnitario).toFixed(2),    cXs[3]+cWs[3]-2, y+4.8,{align:'right'});
        tx('$'+parseFloat(d.total).toFixed(2),             cXs[4]+cWs[4]-2, y+4.8,{align:'right'});
        ln(ML,y+rH,ML+CW,y+rH,[220,225,230],0.2); y+=rH;
    });
    y+=3; ln(ML,y,ML+CW,y,[100,100,100],0.5); y+=5;

    const colObs=ML; const wObs=CW*0.52;
    const colTot=ML+CW*0.54; const wTot=CW*0.46;
    sf('bold',7.5,GRIS_OS); tx('Observacion',colObs,y); y+=4;
    sf('normal',8,NEGRO);
    const wrapped=doc.splitTextToSize('$'+totalFac.toFixed(2)+' '+fpTexto,wObs-2);
    doc.text(wrapped,colObs,y); y+=wrapped.length*4+2;
    if (modoTest) { sf('bold',7.5,ROJO); tx('*** FACTURA DE PRUEBA - NO VALIDA ***',colObs,y); y+=5; }

    let ty=y-wrapped.length*4-2-(modoTest?5:0)-4;
    const ft=(label,valor,bold,color)=>{
        bold?sf('bold',9,color||NEGRO):sf('normal',8,color||GRIS_OS);
        tx(label,colTot,ty); tx(valor,colTot+wTot-2,ty,{align:'right'}); ty+=5;
    };
    ft('Subtotal bruto:',  '$'+subtotal.toFixed(2));
    ln(colTot,ty-1,colTot+wTot,ty-1,GRIS_CL,0.3);
    ft('Base IVA 0%:',    '$0.00');
    ft('Base IVA 15%:',   '$'+subtotal.toFixed(2));
    ft('IVA 15%:',        '$'+impuestos.toFixed(2));
    ln(colTot,ty-1,colTot+wTot,ty-1,[80,80,80],0.5);
    ft('TOTAL ->',        '$'+totalFac.toFixed(2),true,NEGRO);
    if (isEfectivo) {
        ty+=2; ln(colTot,ty-1,colTot+wTot,ty-1,GRIS_CL,0.3);
        ft('Valor recibido:', '$'+valorPagado.toFixed(2));
        ft('VUELTO / CAMBIO:','$'+vuelto.toFixed(2),true,VERDE);
    }

    y=Math.max(y,ty)+6;
    ln(ML,y,ML+CW,y,GRIS_CL,0.5); y+=4;
    sf('normal',7,GRIS_OS); tx('Documento generado por el sistema CompuTech POS',PW/2,y,{align:'center'}); y+=4;
    sf('bold',7,GRIS_OS);   tx('Pagina No. 1/1',PW/2,y,{align:'center'});

    doc.save('Factura-'+(f.numeroFactura||'sin-numero').replace(/[^a-zA-Z0-9-]/g,'-')+'.pdf');
    showToast('PDF descargado correctamente.', 'ok');
}

// ================================================================
// NUEVA VENTA
// ================================================================
function nuevaVenta() {
    limpiar();
    window.scrollTo({ top: 0, behavior: 'smooth' });
    showToast('Listo para la siguiente venta.', 'ok');
}

// ================================================================
// BORRAR VENTAS DE PRUEBA
// ================================================================
function showBorrar() { document.getElementById('modal-borrar').classList.add('open'); }

async function confBorrar() {
    closeM('modal-borrar');
    try {
        const res = await fetch('/test/limpiar-ventas', { method: 'DELETE' });
        if (res.ok) {
            limpiar();
            showToast('Ventas de prueba eliminadas. Contador reiniciado.', 'ok');
        } else {
            showToast('Error al borrar las ventas de prueba.', 'err');
        }
    } catch { showToast('Error de conexion con el servidor.', 'err'); }
}

// ================================================================
// LIMPIAR
// ================================================================
function limpiar() {
    clienteAct = null; prodSeleccion = null; carrito = [];
    selIdx = -1; ventaAct = null; facturaObj = null;

    const inpCli = document.getElementById('inp-cli');
    if (inpCli) { inpCli.value = ''; inpCli.classList.remove('selected'); }
    cerrarDrop('cli');
    document.getElementById('clicard').style.display = 'none';

    const inpProd = document.getElementById('inp-prod');
    if (inpProd) { inpProd.value = ''; inpProd.classList.remove('selected'); }
    cerrarDrop('prod');

    document.getElementById('icant').value = 1;
    document.getElementById('ctbody').innerHTML =
        '<tr><td colspan="6" class="te">No hay productos agregados</td></tr>';
    document.getElementById('tsub').textContent = '$0.00';
    document.getElementById('tiva').textContent = '$0.00';
    document.getElementById('ttot').textContent = '$0.00';

    document.getElementById('spago').value               = '';
    document.getElementById('bloque-pago').style.display = 'none';
    document.getElementById('ipago').value                = '';
    document.getElementById('perr').style.display         = 'none';

    document.getElementById('rtag').style.display   = 'none';
    document.getElementById('bfac').style.display   = 'none';
    document.getElementById('bpdf').style.display   = 'none';
    document.getElementById('bnueva').style.display = 'none';
    document.getElementById('bventa').disabled      = false;
    document.getElementById('bfac').disabled        = false;
    document.getElementById('fp').style.display     = 'none';
    document.getElementById('fhtml').innerHTML      = '';
    document.getElementById('bdel').disabled        = true;
    document.getElementById('cerr').style.display   = 'none';
}
// ================================================================
// TOAST
// ================================================================
function showToast(msg, type) {
    const t = document.getElementById('toast');
    t.textContent = msg; t.className = 'toast ' + type; t.style.display = 'block';
    setTimeout(() => { t.style.display = 'none'; }, 3500);
}