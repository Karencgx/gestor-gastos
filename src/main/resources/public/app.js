const API = '/api';

document.addEventListener('DOMContentLoaded', () => {
    cargarCategorias();
    cargarGastos();
    document.getElementById('fecha').valueAsDate = new Date();
    document.getElementById('form-gasto').addEventListener('submit', registrarGasto);
});

async function cargarCategorias() {
    const response = await fetch(`${API}/categorias`);
    const categorias = await response.json();
    const select = document.getElementById('categoria');

    categorias.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat;
        option.textContent = formatearCategoria(cat);
        select.appendChild(option);
    });
}

async function cargarGastos() {
    const response = await fetch(`${API}/gastos`);
    const gastos = await response.json();

    renderizarTabla(gastos);
    actualizarResumen(gastos);
    cargarTotalesPorCategoria();
}

function renderizarTabla(gastos) {
    const tbody = document.getElementById('gastos-body');
    const emptyState = document.getElementById('empty-state');

    tbody.innerHTML = '';

    if (gastos.length === 0) {
        emptyState.classList.add('visible');
        return;
    }

    emptyState.classList.remove('visible');

    gastos.sort((a, b) => b.id - a.id).forEach(gasto => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${gasto.id}</td>
            <td>${escapeHtml(gasto.descripcion)}</td>
            <td>$${Number(gasto.monto).toLocaleString('es-CO')}</td>
            <td><span class="badge">${formatearCategoria(gasto.categoria)}</span></td>
            <td>${formatearFecha(gasto.fecha)}</td>
            <td><button class="btn btn-danger" onclick="eliminarGasto(${gasto.id})">Eliminar</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function actualizarResumen(gastos) {
    const total = gastos.reduce((sum, g) => sum + Number(g.monto), 0);
    document.getElementById('total-general').textContent = `$${total.toLocaleString('es-CO')}`;
    document.getElementById('total-cantidad').textContent = gastos.length;
}

async function cargarTotalesPorCategoria() {
    const response = await fetch(`${API}/categorias`);
    const categorias = await response.json();
    const container = document.getElementById('category-totals');
    container.innerHTML = '';

    for (const cat of categorias) {
        const res = await fetch(`${API}/gastos/total/${cat}`);
        const data = await res.json();

        const chip = document.createElement('div');
        chip.className = 'category-chip';
        chip.innerHTML = `
            <span class="chip-name">${formatearCategoria(cat)}</span>
            <span class="chip-value">$${Number(data.total).toLocaleString('es-CO')}</span>
        `;
        container.appendChild(chip);
    }
}

async function registrarGasto(event) {
    event.preventDefault();

    const descripcion = document.getElementById('descripcion').value.trim();
    const monto = document.getElementById('monto').value;
    const categoria = document.getElementById('categoria').value;
    const fecha = document.getElementById('fecha').value;

    if (!descripcion || !monto || !categoria || !fecha) {
        mostrarToast('Completa todos los campos', 'error');
        return;
    }

    const body = { descripcion, monto: Number(monto), categoria, fecha };

    const response = await fetch(`${API}/gastos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

    if (response.ok) {
        mostrarToast('Gasto registrado correctamente', 'success');
        document.getElementById('form-gasto').reset();
        document.getElementById('fecha').valueAsDate = new Date();
        cargarGastos();
    } else {
        const error = await response.json();
        mostrarToast(error.message || 'Error al registrar', 'error');
    }
}

async function eliminarGasto(id) {
    if (!confirm('¿Estás seguro de eliminar este gasto?')) return;

    const response = await fetch(`${API}/gastos/${id}`, { method: 'DELETE' });

    if (response.ok) {
        mostrarToast('Gasto eliminado', 'success');
        cargarGastos();
    } else {
        mostrarToast('Error al eliminar', 'error');
    }
}

function formatearCategoria(cat) {
    return cat.charAt(0) + cat.slice(1).toLowerCase();
}

function formatearFecha(fecha) {
    if (Array.isArray(fecha)) {
        const [y, m, d] = fecha;
        return `${d.toString().padStart(2, '0')}/${m.toString().padStart(2, '0')}/${y}`;
    }
    const parts = fecha.split('-');
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function mostrarToast(mensaje, tipo) {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${tipo}`;
    toast.textContent = mensaje;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}
