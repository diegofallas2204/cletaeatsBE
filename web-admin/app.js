const API_URL = 'http://localhost:8080/api';
let currentToken = localStorage.getItem('adminToken');
let salesChart = null;

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    if (currentToken) {
        showDashboard();
    }

    // Navigation
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const section = link.getAttribute('data-section');
            switchSection(section);
        });
    });

    // Forms
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('restaurant-form').addEventListener('submit', handleRestaurantSubmit);
    document.getElementById('logout-btn').addEventListener('click', logout);
});

async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('login-error');

    try {
        const response = await fetch(`${API_URL}/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();
        if (result.success) {
            if (result.data.rol !== 'admin') {
                errorDiv.innerText = 'Acceso restringido a administradores';
                return;
            }
            currentToken = result.data.token;
            localStorage.setItem('adminToken', currentToken);
            document.getElementById('admin-name').innerText = result.data.username;
            showDashboard();
        } else {
            errorDiv.innerText = result.error || 'Credenciales inválidas';
        }
    } catch (err) {
        errorDiv.innerText = 'Error de conexión con el servidor';
    }
}

function showDashboard() {
    document.getElementById('login-screen').classList.remove('active');
    document.getElementById('main-dashboard').classList.add('active');
    loadDashboardData();
}

function logout() {
    localStorage.removeItem('adminToken');
    currentToken = null;
    location.reload();
}

function switchSection(sectionId) {
    document.querySelectorAll('.dashboard-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
    
    document.getElementById(`section-${sectionId}`).classList.add('active');
    document.querySelector(`[data-section="${sectionId}"]`).classList.add('active');
    
    document.getElementById('section-title').innerText = sectionId.charAt(0).toUpperCase() + sectionId.slice(1);

    if (sectionId === 'restaurantes') loadRestaurants();
    if (sectionId === 'dashboard') loadDashboardData();
}

// Data Loading
async function loadDashboardData() {
    try {
        const res = await fetch(`${API_URL}/admin/dashboard`, {
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        const data = await res.json();
        
        if (data.success) {
            const stats = data.data;
            document.getElementById('total-sales').innerText = `₡${stats.ventasTotales.toLocaleString()}`;
            
            let totalOrders = 0;
            const labels = [];
            const sales = [];
            
            stats.ventasPorRestaurante.forEach(item => {
                totalOrders += item.cantidadPedidos;
                labels.push(item.nombreRestaurante);
                sales.push(item.totalVendido);
            });
            
            document.getElementById('total-orders').innerText = totalOrders;
            updateChart(labels, sales);
        }
    } catch (err) { console.error(err); }
}

function updateChart(labels, data) {
    const ctx = document.getElementById('salesChart').getContext('2d');
    if (salesChart) salesChart.destroy();
    
    salesChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Ventas por Restaurante (₡)',
                data: data,
                backgroundColor: '#5D4037',
                borderRadius: 5
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } }
        }
    });
}

async function loadRestaurants() {
    const list = document.getElementById('restaurants-list');
    list.innerHTML = '<tr><td colspan="4">Cargando...</td></tr>';
    
    try {
        const res = await fetch(`${API_URL}/admin/restaurantes`, {
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        const data = await res.json();
        
        if (data.success) {
            list.innerHTML = '';
            data.data.forEach(r => {
                list.innerHTML += `
                    <tr>
                        <td>${r.nombre}</td>
                        <td>${r.cedula_juridica}</td>
                        <td>${r.tipo_comida}</td>
                        <td>
                            <button class="btn-edit" onclick='editRestaurant(${JSON.stringify(r)})'><i class="fas fa-edit"></i></button>
                            <button class="btn-delete" onclick="deleteRestaurant(${r.id})"><i class="fas fa-trash"></i></button>
                        </td>
                    </tr>
                `;
            });
        }
    } catch (err) { console.error(err); }
}

// CRUD Actions
function showAddRestaurant() {
    document.getElementById('restaurant-form').reset();
    document.getElementById('res-id').value = '';
    document.getElementById('modal-title').innerText = 'Nuevo Restaurante';
    document.getElementById('modal-restaurante').style.display = 'flex';
}

function editRestaurant(r) {
    document.getElementById('res-id').value = r.id;
    document.getElementById('res-nombre').value = r.nombre;
    document.getElementById('res-cedula').value = r.cedula_juridica;
    document.getElementById('res-direccion').value = r.direccion;
    document.getElementById('res-tipo').value = r.tipo_comida;
    document.getElementById('modal-title').innerText = 'Editar Restaurante';
    document.getElementById('modal-restaurante').style.display = 'flex';
}

async function deleteRestaurant(id) {
    if (!confirm('¿Seguro que desea eliminar este restaurante?')) return;
    
    try {
        const res = await fetch(`${API_URL}/admin/restaurantes/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${currentToken}` }
        });
        const result = await res.json();
        if (result.success) loadRestaurants();
    } catch (err) { alert('Error al eliminar'); }
}

async function handleRestaurantSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('res-id').value;
    const body = {
        nombre: document.getElementById('res-nombre').value,
        cedula_juridica: document.getElementById('res-cedula').value,
        direccion: document.getElementById('res-direccion').value,
        tipo_comida: document.getElementById('res-tipo').value
    };
    if (id) body.id = parseInt(id);

    const method = id ? 'PUT' : 'POST';
    
    try {
        const res = await fetch(`${API_URL}/admin/restaurantes`, {
            method: method,
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify(body)
        });
        const result = await res.json();
        if (result.success) {
            closeModal();
            loadRestaurants();
        }
    } catch (err) { alert('Error al guardar'); }
}

function closeModal() {
    document.getElementById('modal-restaurante').style.display = 'none';
}
