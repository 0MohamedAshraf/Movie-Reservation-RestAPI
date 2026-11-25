const API_BASE = '/api/v1/admin';
const LOGIN_URL = '/api/v1/auth/login';
const TOKEN_KEY = 'movieReservationAdminToken';

const state = {
    summary: null,
    users: [],
    theaters: [],
    movies: [],
    schedules: [],
    scheduleSeats: [],
    reservations: []
};

const loginView = document.getElementById('loginView');
const dashboardView = document.getElementById('dashboardView');
const loginForm = document.getElementById('loginForm');
const loginMessage = document.getElementById('loginMessage');
const message = document.getElementById('message');
const roleLabel = document.getElementById('roleLabel');
const lastUpdated = document.getElementById('lastUpdated');
const refreshButton = document.getElementById('refreshButton');
const logoutButton = document.getElementById('logoutButton');
const statusFilter = document.getElementById('reservationStatusFilter');
const availabilityFilter = document.getElementById('seatAvailabilityFilter');

let token = null;
let activeSection = 'overview';

try {
    init();
} catch (error) {
    setLoginMessage(`Dashboard failed to initialize: ${error.message}`, 'error');
}

function init() {
    loginForm.addEventListener('submit', handleLogin);
    logoutButton.addEventListener('click', signOut);
    refreshButton.addEventListener('click', loadAll);
    statusFilter.addEventListener('change', () => renderReservations());
    availabilityFilter.addEventListener('change', () => renderScheduleSeats());

    document.querySelectorAll('.nav-button').forEach(button => {
        button.addEventListener('click', () => selectSection(button.dataset.section));
    });

    document.querySelectorAll('[data-load]').forEach(button => {
        button.addEventListener('click', () => loadSection(button.dataset.load));
    });

    const savedToken = sessionStorage.getItem(TOKEN_KEY);
    if (savedToken) {
        if (!isAdminToken(savedToken)) {
            clearToken();
            setLoginMessage('The saved session is not an ADMIN account. Please sign in again.', 'error');
            return;
        }
        showDashboard(savedToken);
    }
}

/* ---------- Authentication ---------- */

async function handleLogin(event) {
    event.preventDefault();
    setLoginMessage('');

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(LOGIN_URL, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({email, password})
        });

        if (!response.ok) {
            const detail = await response.text();
            throw new Error(detail || 'Invalid email or password');
        }

        const payload = await response.json();
        if (!payload || !payload.token) {
            throw new Error('Login response did not include a token');
        }
        if (!isAdminToken(payload.token)) {
            throw new Error('This account does not have the ADMIN role');
        }

        sessionStorage.setItem(TOKEN_KEY, payload.token);
        loginForm.reset();
        setLoginMessage('');
        showDashboard(payload.token);
    } catch (error) {
        setLoginMessage(error.message || 'Unable to sign in', 'error');
    }
}

function decodeJwtPayload(jwt) {
    try {
        const part = jwt.split('.')[1];
        if (!part) return {};
        const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
        const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
        return JSON.parse(atob(padded));
    } catch (error) {
        return {};
    }
}

function isAdminToken(jwt) {
    return decodeJwtPayload(jwt).role === 'ADMIN';
}

function showDashboard(newToken) {
    token = newToken;
    loginView.hidden = true;
    dashboardView.hidden = false;
    roleLabel.textContent = 'Signed in as ADMIN';
    loadAll();
}

function signOut() {
    clearToken();
    dashboardView.hidden = true;
    loginView.hidden = false;
    setMessage('');
    setLoginMessage('Signed out.');
}

function clearToken() {
    token = null;
    sessionStorage.removeItem(TOKEN_KEY);
}

function setLoginMessage(text, type = '') {
    loginMessage.textContent = text;
    loginMessage.className = `message ${type}`;
}

function setMessage(text, type = '') {
    message.textContent = text;
    message.className = `message ${type}`;
}

/* ---------- API access ---------- */

async function api(path, options = {}) {
    const headers = {...(options.headers || {})};
    headers.Authorization = `Bearer ${token}`;
    if (options.body) {
        headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(`${API_BASE}${path}`, {...options, headers});
    if (!response.ok) {
        const detail = await response.text();
        const error = new Error(detail || `Request failed with status ${response.status}`);
        error.status = response.status;
        throw error;
    }

    const contentType = response.headers.get('content-type') || '';
    return contentType.includes('json') ? response.json() : response.text();
}

function handleLoadError(error) {
    if (error.status === 401 || error.status === 403) {
        signOut();
        setLoginMessage('Your session is no longer authorized. Please sign in again.', 'error');
    } else {
        setMessage(error.message || 'Unable to load dashboard data', 'error');
    }
}

async function request(path) {
    try {
        const data = await api(path);
        setMessage('');
        return data;
    } catch (error) {
        handleLoadError(error);
        return null;
    }
}

async function loadSection(key) {
    const loaders = {
        summary: loadSummary,
        users: loadUsers,
        theaters: loadTheaters,
        movies: loadMovies,
        schedules: loadSchedules,
        scheduleSeats: loadScheduleSeats,
        reservations: loadReservations
    };

    const data = await loaders[key]();
    if (data === null) return;

    renderSection(key);
}

async function loadSummary() {
    state.summary = await request('/summary');
    return state.summary;
}

async function loadUsers() {
    state.users = (await request('/users')) || [];
    return state.users;
}

async function loadTheaters() {
    state.theaters = (await request('/theaters')) || [];
    return state.theaters;
}

async function loadMovies() {
    state.movies = (await request('/movies')) || [];
    return state.movies;
}

async function loadSchedules() {
    state.schedules = (await request('/schedules')) || [];
    return state.schedules;
}

async function loadScheduleSeats() {
    state.scheduleSeats = (await request('/schedule-seats')) || [];
    return state.scheduleSeats;
}

async function loadReservations() {
    state.reservations = (await request('/reservations')) || [];
    return state.reservations;
}

async function loadAll() {
    if (!token) return;
    setBusy(refreshButton, true);

    try {
        await Promise.all([
            loadSummary(),
            loadUsers(),
            loadTheaters(),
            loadMovies(),
            loadSchedules(),
            loadScheduleSeats(),
            loadReservations()
        ]);
        renderAll();
        lastUpdated.textContent = `Updated ${new Date().toLocaleTimeString()}`;
    } finally {
        setBusy(refreshButton, false);
    }
}

function setBusy(button, busy) {
    if (!button.dataset.originalText) {
        button.dataset.originalText = button.textContent;
    }
    button.disabled = busy;
    button.textContent = busy ? 'Loading…' : button.dataset.originalText;
}

/* ---------- Rendering ---------- */

function selectSection(key) {
    activeSection = key;
    document.querySelectorAll('.nav-button').forEach(button => {
        button.classList.toggle('active', button.dataset.section === key);
    });

    document.querySelectorAll('.panel').forEach(panel => {
        const isActive = panel.id === `section-${key}`;
        panel.hidden = !isActive;
        panel.classList.toggle('active', isActive);
    });
}

function renderAll() {
    renderSummary();
    renderUsers();
    renderTheaters();
    renderMovies();
    renderSchedules();
    renderScheduleSeats();
    renderReservations();
}

function renderSection(key) {
    const renderers = {
        summary: renderSummary,
        users: renderUsers,
        theaters: renderTheaters,
        movies: renderMovies,
        schedules: renderSchedules,
        scheduleSeats: renderScheduleSeats,
        reservations: renderReservations
    };
    renderers[key]();
}

function renderSummary() {
    const container = document.getElementById('summaryCards');
    container.replaceChildren();

    const cards = [
        ['Users', state.summary?.users, ''],
        ['Theaters', state.summary?.theaters, ''],
        ['Movies', state.summary?.movies, ''],
        ['Schedules', state.summary?.schedules, ''],
        ['Schedule seats', state.summary?.scheduleSeats, ''],
        ['Reservations', state.summary?.reservations, ''],
        ['Pending approval', state.summary?.pendingReservations, 'pending']
    ];

    cards.forEach(([label, value, cssClass]) => {
        const card = document.createElement('div');
        card.className = `card ${cssClass}`.trim();

        const labelEl = document.createElement('div');
        labelEl.className = 'label';
        labelEl.textContent = label;

        const valueEl = document.createElement('div');
        valueEl.className = 'value';
        valueEl.textContent = value == null ? '—' : String(value);

        card.append(labelEl, valueEl);
        container.appendChild(card);
    });
}

function renderUsers() {
    renderTable('usersTable', [
        {label: 'ID', key: 'id'},
        {label: 'First name', key: 'firstName'},
        {label: 'Last name', key: 'lastName'},
        {label: 'Email', key: 'email'},
        {label: 'City', key: 'city'},
        {label: 'Role', render: td => renderBadge(td, td._row?.role)}
    ], state.users);
}

function renderTheaters() {
    renderTable('theatersTable', [
        {label: 'ID', key: 'id'},
        {label: 'Name', key: 'name'},
        {label: 'City', key: 'city'},
        {label: 'Total seats', key: 'totalSeats'}
    ], state.theaters);
}

function renderMovies() {
    renderTable('moviesTable', [
        {label: 'ID', key: 'id'},
        {label: 'Title', key: 'title'},
        {label: 'Category', key: 'category'},
        {label: 'Rating', key: 'rating'},
        {label: 'Release date', key: 'releaseDate'},
        {label: 'Duration (min)', key: 'duration'}
    ], state.movies);
}

function renderSchedules() {
    renderTable('schedulesTable', [
        {label: 'ID', key: 'id'},
        {label: 'Date', key: 'date'},
        {label: 'Start', render: td => setText(td, formatDateTime(td._row?.startTime))},
        {label: 'End', render: td => setText(td, formatDateTime(td._row?.endTime))},
        {label: 'Movie', key: 'movieTitle'},
        {label: 'Theater', key: 'theaterName'},
        {label: 'City', key: 'theaterCity'},
        {label: 'Price', key: 'price'}
    ], state.schedules);
}

function renderScheduleSeats() {
    const filter = availabilityFilter.value;
    const rows = filter
        ? state.scheduleSeats.filter(row => filter === 'available' ? row.available === true : row.available !== true)
        : state.scheduleSeats;

    renderTable('scheduleSeatsTable', [
        {label: 'ID', key: 'id'},
        {label: 'Showtime', render: td => setText(td, `${td._row?.movieTitle || '—'} · ${td._row?.theaterName || '—'}`)},
        {label: 'Date', key: 'date'},
        {label: 'Start', render: td => setText(td, formatDateTime(td._row?.startTime))},
        {label: 'Seat', key: 'seatLabel'},
        {label: 'Class', key: 'seatClass'},
        {label: 'Availability', render: td => renderBadge(td, td._row?.available ? 'AVAILABLE' : 'UNAVAILABLE')},
        {label: 'Reservation', key: 'reservationId'},
        {label: 'Status', render: td => renderBadge(td, td._row?.reservationStatus)}
    ], rows);
}

function renderReservations() {
    const filter = statusFilter.value;
    const rows = filter
        ? state.reservations.filter(row => row.status === filter)
        : state.reservations;

    renderTable('reservationsTable', [
        {label: 'ID', key: 'id'},
        {label: 'Customer', key: 'userName'},
        {label: 'Email', key: 'userEmail'},
        {label: 'Movie', key: 'movieTitle'},
        {label: 'Theater', key: 'theaterName'},
        {label: 'Showtime', render: td => setText(td, formatDateTime(td._row?.startTime))},
        {label: 'Seats', render: td => setText(td, (td._row?.seatLabels || []).join(', ') || '—')},
        {label: 'Total', key: 'totalPrice'},
        {label: 'Payment', key: 'paymentMethod'},
        {label: 'Status', render: td => renderBadge(td, td._row?.status)},
        {label: 'Actions', render: td => renderReservationActions(td, td._row)}
    ], rows);
}

function renderTable(tableId, columns, rows) {
    const table = document.getElementById(tableId);
    table.replaceChildren();

    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    columns.forEach(column => {
        const th = document.createElement('th');
        th.textContent = column.label;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);

    const tbody = document.createElement('tbody');
    if (!rows || rows.length === 0) {
        const tr = document.createElement('tr');
        tr.className = 'empty-row';
        const td = document.createElement('td');
        td.colSpan = columns.length;
        td.textContent = 'No records found.';
        tr.appendChild(td);
        tbody.appendChild(tr);
    } else {
        rows.forEach(row => {
            const tr = document.createElement('tr');
            columns.forEach(column => {
                const td = document.createElement('td');
                td._row = row;
                if (column.render) {
                    column.render(td);
                } else if (column.key) {
                    setText(td, row[column.key]);
                }
                tr.appendChild(td);
            });
            tbody.appendChild(tr);
        });
    }

    table.append(thead, tbody);
}

function setText(element, value) {
    element.textContent = value === null || value === undefined || value === '' ? '—' : String(value);
}

function renderBadge(element, value) {
    if (!value) {
        setText(element, value);
        return;
    }
    const badge = document.createElement('span');
    badge.className = `badge ${String(value).toUpperCase()}`;
    badge.textContent = String(value).toUpperCase();
    element.appendChild(badge);
}

function formatDateTime(value) {
    if (!value) return '—';
    return String(value).replace('T', ' ');
}

/* ---------- Reservation actions ---------- */

function renderReservationActions(element, reservation) {
    if (!reservation) return;

    const approve = createActionButton('Approve', 'approve', () => changeReservation(reservation, 'approve'));
    approve.disabled = reservation.status !== 'PENDING';

    const cancel = createActionButton('Cancel', 'cancel', () => changeReservation(reservation, 'cancel'));
    cancel.disabled = reservation.status === 'CANCELLED' || reservation.status === 'EXPIRED';

    element.append(approve, cancel);
}

function createActionButton(label, cssClass, onClick) {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = `action ${cssClass}`;
    button.textContent = label;
    button.addEventListener('click', onClick);
    return button;
}

async function changeReservation(reservation, action) {
    const verb = action === 'approve' ? 'approve' : 'cancel';
    if (!window.confirm(`${verb.charAt(0).toUpperCase() + verb.slice(1)} reservation #${reservation.id}?`)) {
        return;
    }

    try {
        const result = await api(`/reservations/${reservation.id}/${action}`, {method: 'POST'});
        const pastTense = action === 'approve' ? 'approved' : 'cancelled';
        setMessage(`Reservation #${reservation.id} ${pastTense}. ${result || ''}`.trim(), 'success');

        await Promise.all([loadReservations(), loadScheduleSeats(), loadSummary()]);
        renderReservations();
        renderScheduleSeats();
        renderSummary();
    } catch (error) {
        if (error.status === 401 || error.status === 403) {
            handleLoadError(error);
        } else {
            setMessage(error.message || `Unable to ${verb} reservation`, 'error');
        }
    }
}
