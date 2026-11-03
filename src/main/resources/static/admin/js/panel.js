// Define permission bitmasks in JavaScript matching your Java backend constants
const PERMISSIONS = {
    uPermSystemParameters: 1n << 0n,
    uPermReadAudits: 1n << 1n,
    uPermRegisterUser: 1n << 2n,
    uPermUpdateUser: 1n << 3n,
    uPermDeactivateUser: 1n << 4n,
    uPermSetUserPermissions: 1n << 5n,
    uPermRegisterSector: 1n << 6n,
    uPermViewSector: 1n << 7n,
    uPermUpdateSector: 1n << 8n,
    uPermRegisterRoom: 1n << 9n,
    uPermViewRoom: 1n << 10n,
    uPermUpdateRoom: 1n << 11n,
    uPermRegisterBed: 1n << 12n,
    uPermViewBed: 1n << 13n,
    uPermUpdateBed: 1n << 14n,
    uPermAllocateBed: 1n << 15n,
    uPermRegisterPatient: 1n << 16n,
    uPermViewPatient: 1n << 17n,
    uPermFindPatient: 1n << 18n,
    uPermTransferPatient: 1n << 19n,
    uPermInternPatient: 1n << 20n,
    uPermFreePatient: 1n << 21n,
    uPermViewIndicators: 1n << 22n,
    uPermCreateReport: 1n << 23n
};

// Define Role templates
const ROLES = {
    admin: -1n, // All bits set (~0L)
    management: PERMISSIONS.uPermViewSector | PERMISSIONS.uPermViewRoom | PERMISSIONS.uPermViewBed | PERMISSIONS.uPermViewPatient | PERMISSIONS.uPermViewIndicators | PERMISSIONS.uPermCreateReport,
    reception: PERMISSIONS.uPermRegisterPatient | PERMISSIONS.uPermFindPatient | PERMISSIONS.uPermInternPatient | PERMISSIONS.uPermAllocateBed | PERMISSIONS.uPermViewBed | PERMISSIONS.uPermFreePatient,
    nurse: PERMISSIONS.uPermViewBed | PERMISSIONS.uPermViewPatient | PERMISSIONS.uPermTransferPatient | PERMISSIONS.uPermUpdateBed,
    medic: PERMISSIONS.uPermViewPatient | PERMISSIONS.uPermViewSector | PERMISSIONS.uPermViewBed
};

// Helper to auto-check permissions when selecting a Role Preset
function applyRolePreset(roleKey) {
    if (!roleKey || !ROLES[roleKey]) return;
    const targetMask = ROLES[roleKey];
    const checkboxes = document.querySelectorAll('input[name="perm-checkbox"]');
    
    checkboxes.forEach(cb => {
        const val = BigInt(cb.value);
        if (targetMask === -1n) {
            cb.checked = true; // Admin gets all
        } else {
            cb.checked = (targetMask & val) !== 0n;
        }
    });
}

let db = {
    usuarios: [], // Populated dynamically from the backend API
    setores: [],
    quartos: [
        { id: 1, numero: "Quarto 101", setor: "UTI Geral", tipo: "Isolamento" },
        { id: 2, numero: "Quarto 102", setor: "UTI Geral", tipo: "UTI Padrão" },
        { id: 3, numero: "Quarto 201", setor: "Enfermaria Norte", tipo: "Enfermaria Coletiva" }
    ],
    leitos: [
        { id: 1, numero: "Leito 01", quarto: "Quarto 101", status: "Disponível" },
        { id: 2, numero: "Leito 02", quarto: "Quarto 102", status: "Ocupado" },
        { id: 3, numero: "Leito 03", quarto: "Quarto 201", status: "Manutenção" }
    ]
};

// Fetch actual users from the Spring Boot backend API
async function loadUsersFromApi() {
    try {
        const response = await fetch('/api/admin/get_users');
        if (response.ok) {
            const users = await response.json();
            db.usuarios = Array.isArray(users) ? users : [];
            refreshUI();
        } else {
            console.error('Falha ao carregar usuários do servidor.');
        }
    } catch (error) {
        console.error('Erro de rede ao buscar usuários:', error);
    }
}

function saveDB() {
    localStorage.setItem('cmi_admin_setores', JSON.stringify(db.setores));
    localStorage.setItem('cmi_admin_quartos', JSON.stringify(db.quartos));
    localStorage.setItem('cmi_admin_leitos', JSON.stringify(db.leitos));
    refreshUI();
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = type === 'success' ? 'toast-success' : 'toast-danger';
    toast.style.display = 'block';
    setTimeout(() => {
        toast.style.display = 'none';
    }, 3000);
}

let currentTab = 'dashboard';
function switchTab(tabId) {
    currentTab = tabId;
    const tabs = ['dashboard', 'usuarios', 'setores', 'quartos', 'leitos'];
    
    tabs.forEach(t => {
        const section = document.getElementById(`tab-${t}`);
        const navBtn = document.getElementById(`nav-${t}`);
        if (t === tabId) {
            section.classList.remove('hidden');
            navBtn.classList.add('active-nav');
        } else {
            section.classList.add('hidden');
            navBtn.classList.remove('active-nav');
        }
    });

    const titles = {
        dashboard: 'Dashboard Geral',
        usuarios: 'Gerenciamento de Usuários',
        setores: 'Gerenciamento de Setores',
        quartos: 'Gerenciamento de Quartos',
        leitos: 'Gerenciamento de Leitos'
    };
    document.getElementById('page-title').textContent = titles[tabId];
    refreshUI();
}

function refreshUI() {
    document.getElementById('stat-usuarios').textContent = db.usuarios.length;
    document.getElementById('stat-setores').textContent = db.setores.length;
    document.getElementById('stat-quartos').textContent = db.quartos.length;
    document.getElementById('stat-leitos').textContent = db.leitos.length;

    const dispCount = db.leitos.filter(l => l.status === 'Disponível').length;
    const ocupCount = db.leitos.filter(l => l.status === 'Ocupado').length;
    const manutCount = db.leitos.filter(l => l.status === 'Manutenção').length;

    document.getElementById('stat-leitos-disp').textContent = dispCount;
    document.getElementById('stat-leitos-ocup').textContent = ocupCount;
    document.getElementById('stat-leitos-manut').textContent = manutCount;

    renderUsuariosTable();
    renderSetoresTable();
    renderQuartosTable();
    renderLeitosTable();
}

// Global variable to hold search/filter state if desired, or read directly from DOM
function filterUsersTable() {
    renderUsuariosTable();
}

function renderUsuariosTable() {
    const tbody = document.getElementById('table-usuarios-body');
    const searchVal = document.getElementById('user-search-input')?.value.toLowerCase() || '';
    const statusVal = document.getElementById('user-filter-status')?.value || '';

    // Filter from the already loaded db.usuarios array without making network calls
    const filteredUsers = db.usuarios.filter(u => {
        const name = (u.userName || '').toLowerCase();
        const email = (u.userEmail || '').toLowerCase();
        const matchesSearch = name.includes(searchVal) || email.includes(searchVal);

        const isUserActive = u.userActive !== false && u.userActive !== 0;
        let matchesStatus = true;
        if (statusVal === 'active') matchesStatus = isUserActive;
        if (statusVal === 'inactive') matchesStatus = !isUserActive;

        return matchesSearch && matchesStatus;
    });

    tbody.innerHTML = filteredUsers.map(u => {
        const name = u.userName || 'Usuário Sem Nome';
        const email = u.userEmail || '';
        
        let profile = 'Usuário Padrão';
        if (u.userPermissions && (u.userPermissions & 1) !== 0) {
            profile = 'Administrador';
        }

        const isUserActive = u.userActive !== false && u.userActive !== 0;
        const statusBadge = isUserActive 
            ? '<span class="badge-green">Ativo</span>' 
            : '<span class="badge-red">Inativo</span>';

        const userId = u.userID || '';

        return `
            <tr>
                <td class="td-primary">${name}</td>
                <td class="td-muted">${email}</td>
                <td><span class="badge-blue">${profile}</span></td>
                <td>${statusBadge}</td>
                <td class="td-actions">
                    <button onclick="openModal('usuarios', '${userId}')" class="action-btn-edit" title="Editar">Editar <i class="fa-solid fa-pen-to-square"></i></button>
                </td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="5" style="padding: 1.5rem; text-align: center; color: #666666;">Nenhum usuário encontrado.</td></tr>`;
}

function renderSetoresTable() {
    const tbody = document.getElementById('table-setores-body');
    if (!tbody) return;

    tbody.innerHTML = db.setores.map(s => {
        const name = s.sectorName || s.nome || 'Setor Sem Nome';
        const desc = s.sectorDescription || s.descricao || '';
        const sectorId = s.sectorID || s.id || '';

        return `
            <tr>
                <td class="td-primary">${name}</td>
                <td class="td-muted">${desc}</td>
                <td class="td-actions">
                    <button onclick="openModal('setores', '${sectorId}')" class="action-btn-edit" title="Editar">Editar <i class="fa-solid fa-pen-to-square"></i></button>
                </td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="3" style="padding: 1.5rem; text-align: center; color: #666666;">Nenhum setor cadastrado.</td></tr>`;
}

function renderQuartosTable() {
    const tbody = document.getElementById('table-quartos-body');
    tbody.innerHTML = db.quartos.map(q => `
        <tr>
            <td class="td-primary">${q.numero}</td>
            <td><span class="badge-gray">${q.setor}</span></td>
            <td class="td-primary">${q.tipo}</td>
            <td class="td-actions">
                <button onclick="openModal('quartos', ${q.id})" class="action-btn-edit" title="Editar"><i class="fa-solid fa-pen-to-square"></i></button>
                <button onclick="deleteRecord('quartos', ${q.id})" class="action-btn-delete" title="Excluir"><i class="fa-solid fa-trash"></i></button>
            </td>
        </tr>
    `).join('') || `<tr><td colspan="4" style="padding: 1.5rem; text-align: center; color: #666666;">Nenhum quarto cadastrado.</td></tr>`;
}

function renderLeitosTable() {
    const tbody = document.getElementById('table-leitos-body');
    tbody.innerHTML = db.leitos.map(l => {
        let badgeClass = 'badge-green';
        if (l.status === 'Ocupado') badgeClass = 'badge-red';
        if (l.status === 'Manutenção') badgeClass = 'badge-yellow';

        return `
            <tr>
                <td class="td-primary">${l.numero}</td>
                <td class="td-muted">${l.quarto}</td>
                <td><span class="${badgeClass}">${l.status}</span></td>
                <td class="td-actions">
                    <button onclick="openModal('leitos', ${l.id})" class="action-btn-edit" title="Editar"><i class="fa-solid fa-pen-to-square"></i></button>
                    <button onclick="deleteRecord('leitos', ${l.id})" class="action-btn-delete" title="Excluir"><i class="fa-solid fa-trash"></i></button>
                </td>
            </tr>
        `;
    }).join('') || `<tr><td colspan="4" style="padding: 1.5rem; text-align: center; color: #666666;">Nenhum leito cadastrado.</td></tr>`;
}

function openModal(entity, id = null) {
    const container = document.getElementById('modal-container');
    const titleEl = document.getElementById('modal-title');
    const entityInput = document.getElementById('form-entity');
    const idInput = document.getElementById('form-id');
    const fieldsContainer = document.getElementById('form-fields-container');

    entityInput.value = entity;
    idInput.value = id !== null ? id : '';

    let item = null;
    if (id !== null) {
        item = db[entity].find(i => (i.userID || i.id) == id);
        titleEl.textContent = `Editar ${entity.slice(0, -1)}`;
    } else {
        titleEl.textContent = `Adicionar ${entity.slice(0, -1)}`;
    }

    let html = '';
    if (entity === 'usuarios') {
        const userPerms = item && item.userPermissions !== undefined ? BigInt(item.userPermissions) : 0n;
        const isUserActive = item ? (item.userActive !== false) : true;

        // Automatically detect which role preset matches the user's permissions bitmask
        let matchedRole = '';
        if (item && item.userPermissions !== undefined) {
            if (userPerms === -1n || userPerms === 4294967295n || (userPerms & PERMISSIONS.uPermSystemParameters) !== 0n && userPerms.toString(2).length > 20) {
                matchedRole = 'admin';
            } else if (userPerms === ROLES.management) {
                matchedRole = 'management';
            } else if (userPerms === ROLES.reception) {
                matchedRole = 'reception';
            } else if (userPerms === ROLES.nurse) {
                matchedRole = 'nurse';
            } else if (userPerms === ROLES.medic) {
                matchedRole = 'medic';
            }
        }

        html = `
            <div class="form-field">
                <label>Nome Completo</label>
                <input type="text" id="field-nome" class="input-custom" value="${item ? (item.userName || '') : ''}" required placeholder="Nome do usuário">
            </div>
            <div class="form-field">
                <label>E-mail</label>
                <input type="email" id="field-email" class="input-custom" value="${item ? (item.userEmail || '') : ''}" required placeholder="email@cmi.com">
            </div>
            <div class="form-field">
                <label>Senha ${item ? '(Deixe em branco para manter)' : ''}</label>
                <input type="password" id="field-senha" class="input-custom" ${item ? '' : 'required'} placeholder="******">
            </div>
            <div class="form-field">
                <label>Status do Usuário</label>
                <select id="field-ativo" class="input-custom">
                    <option value="true" ${isUserActive ? 'selected' : ''}>Ativo</option>
                    <option value="false" ${!isUserActive ? 'selected' : ''}>Inativo</option>
                </select>
            </div>
            <div class="form-field">
                <label>Tipos de usuario</label>
                <select id="field-role-preset" class="input-custom" onchange="applyRolePreset(this.value)">
                    <option value="" ${matchedRole === '' ? 'selected' : ''}>Personalizado</option>
                    <option value="admin" ${matchedRole === 'admin' ? 'selected' : ''}>Administrador (Todas as permissões)</option>
                    <option value="management" ${matchedRole === 'management' ? 'selected' : ''}>Gestão</option>
                    <option value="reception" ${matchedRole === 'reception' ? 'selected' : ''}>Recepção</option>
                    <option value="nurse" ${matchedRole === 'nurse' ? 'selected' : ''}>Enfermeiro(a)</option>
                    <option value="medic" ${matchedRole === 'medic' ? 'selected' : ''}>Médico</option>
                </select>
            </div>
            <div class="form-field" style="max-height: 200px; overflow-y: auto; border: 1px solid #e5e7eb; padding: 0.75rem; border-radius: 0.375rem; background: #f9fafb;">
                <label style="font-weight: 600; margin-bottom: 0.5rem; display: block;">Permissões Granulares</label>
                <div style="display: grid; grid-template-columns: 1fr; gap: 0.375rem;" id="permissions-checkboxes">
                    ${Object.entries(PERMISSIONS).map(([key, val]) => {
                        const isChecked = (userPerms & val) !== 0n;
                        const labelName = key.replace('uPerm', '');
                        return `
                            <label style="font-size: 0.8rem; display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                                <input type="checkbox" name="perm-checkbox" value="${val.toString()}" ${isChecked ? 'checked' : ''}>
                                ${labelName}
                            </label>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    } else if (entity === 'setores') {
        const isSectorActive = item ? (item.sectorActive !== 0 && item.sectorActive !== false) : true;

        html = `
            <div class="form-field">
                <label>Nome do Setor</label>
                <input type="text" id="field-nome" class="input-custom" value="${item ? (item.sectorName || item.nome || '') : ''}" required placeholder="Ex: UTI Pediátrica">
            </div>
            <div class="form-field">
                <label>Descrição</label>
                <input type="text" id="field-descricao" class="input-custom" value="${item ? (item.sectorDescription || item.descricao || '') : ''}" required placeholder="Breve descrição do setor">
            </div>
            <div class="form-field">
                <label>Status do Setor</label>
                <select id="field-ativo" class="input-custom">
                    <option value="true" ${isSectorActive ? 'selected' : ''}>Ativo</option>
                    <option value="false" ${!isSectorActive ? 'selected' : ''}>Inativo</option>
                </select>
            </div>
        `;
    } else if (entity === 'quartos') {
        const setoresOptions = db.setores.map(s => `<option value="${s.nome}" ${item && item.setor === s.nome ? 'selected' : ''}>${s.nome}</option>`).join('');
        html = `
            <div class="form-field">
                <label>Número do Quarto</label>
                <input type="text" id="field-numero" class="input-custom" value="${item ? item.numero : ''}" required placeholder="Ex: Quarto 302">
            </div>
            <div class="form-field">
                <label>Setor</label>
                <select id="field-setor" class="input-custom" required>
                    <option value="">Selecione o setor...</option>
                    ${setoresOptions}
                </select>
            </div>
            <div class="form-field">
                <label>Tipo</label>
                <input type="text" id="field-tipo" class="input-custom" value="${item ? item.tipo : ''}" required placeholder="Ex: UTI Padrão, Isolamento">
            </div>
        `;
    } else if (entity === 'leitos') {
        const quartosOptions = db.quartos.map(q => `<option value="${q.numero}" ${item && item.quarto === q.numero ? 'selected' : ''}>${q.numero} (${q.setor})</option>`).join('');
        html = `
            <div class="form-field">
                <label>Identificação do Leito</label>
                <input type="text" id="field-numero" class="input-custom" value="${item ? item.numero : ''}" required placeholder="Ex: Leito 05">
            </div>
            <div class="form-field">
                <label>Quarto Associado</label>
                <select id="field-quarto" class="input-custom" required>
                    <option value="">Selecione o quarto...</option>
                    ${quartosOptions}
                </select>
            </div>
            <div class="form-field">
                <label>Status Operacional</label>
                <select id="field-status" class="input-custom">
                    <option value="Disponível" ${item && item.status === 'Disponível' ? 'selected' : ''}>Disponível</option>
                    <option value="Ocupado" ${item && item.status === 'Ocupado' ? 'selected' : ''}>Ocupado</option>
                    <option value="Manutenção" ${item && item.status === 'Manutenção' ? 'selected' : ''}>Manutenção</option>
                </select>
            </div>
        `;
    }

    fieldsContainer.innerHTML = html;
    container.classList.remove('hidden');
}

function closeModal() {
    document.getElementById('modal-container').classList.add('hidden');
    document.getElementById('generic-form').reset();
}

async function handleFormSubmit(event) {
    event.preventDefault();
    const entity = document.getElementById('form-entity').value;

    if (entity === 'usuarios') {
        // Calculate the combined bitmask from all checked permission checkboxes
        let totalPermissions = 0n;
        const checkboxes = document.querySelectorAll('input[name="perm-checkbox"]:checked');
        checkboxes.forEach(cb => {
            totalPermissions |= BigInt(cb.value);
        });

        const userId = document.getElementById('form-id').value;
        const payload = {
            userID: userId !== '' ? userId : null,
            userName: document.getElementById('field-nome').value,
            userEmail: document.getElementById('field-email').value,
            userPassword: document.getElementById('field-senha').value,
            userPermissions: Number(totalPermissions),
            userActive: document.getElementById('field-ativo').value === 'true' ? 1 : 0 // Added activation flag
        };

        const endpoint = userId !== '' ? '/api/admin/update_user' : '/api/admin/register_user';

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast(resultText || 'Usuário salvo com sucesso!');
                await loadUsersFromApi();
                closeModal();
            } else {
                showToast(resultText || 'Erro ao salvar usuário.', 'danger');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            showToast('Erro de conexão com o servidor.', 'danger');
        }
        return;
    }

    // Local storage handling for other entities (setores, quartos, leitos)
    const idVal = document.getElementById('form-id').value;
    const id = idVal !== '' ? parseInt(idVal) : null;
    let newData = {};

    if (entity === 'setores') {
        const sectorId = document.getElementById('form-id').value;
        
        const payload = {
            sectorID: sectorId !== '' ? sectorId : null,
            sectorName: document.getElementById('field-nome').value,
            sectorDescription: document.getElementById('field-descricao').value,
            sectorActive: document.getElementById('field-ativo').value === 'true' ? 1 : 0
        };

        const endpoint = sectorId !== '' ? '/api/admin/update_sector' : '/api/admin/register_sector';

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast(resultText || 'Setor salvo com sucesso!');
                await loadSectorsFromApi();
                closeModal();
            } else {
                showToast(resultText || 'Erro ao salvar setor.', 'danger');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            showToast('Erro de conexão com o servidor.', 'danger');
        }
        return;
    } else if (entity === 'quartos') {
        newData = {
            numero: document.getElementById('field-numero').value,
            setor: document.getElementById('field-setor').value,
            tipo: document.getElementById('field-tipo').value
        };
    } else if (entity === 'leitos') {
        newData = {
            numero: document.getElementById('field-numero').value,
            quarto: document.getElementById('field-quarto').value,
            status: document.getElementById('field-status').value
        };
    }

    if (id !== null) {
        const index = db[entity].findIndex(i => i.id === id);
        if (index !== -1) {
            db[entity][index] = { id, ...newData };
            showToast('Registro atualizado com sucesso!');
        }
    } else {
        const newId = db[entity].length > 0 ? Math.max(...db[entity].map(i => i.id)) + 1 : 1;
        db[entity].push({ id: newId, ...newData });
        showToast('Registro adicionado com sucesso!');
    }

    saveDB();
    closeModal();
}

function deleteRecord(entity, id) {
    if (entity === 'usuarios') {
        alert('A exclusão de usuários via API pode ser acoplada aqui.');
        return;
    }
    if (confirm('Tem certeza que deseja excluir este registro?')) {
        db[entity] = db[entity].filter(i => i.id !== id);
        saveDB();
        showToast('Registro excluído com sucesso!', 'danger');
    }
}

window.onload = async function() {
    await loadUsersFromApi();
    await loadSectorsFromApi();
    switchTab('dashboard');
};

document.getElementById('logout-btn').addEventListener('click', async function(e) {
    e.preventDefault(); // Prevent default link behavior

    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Since the backend uses sendRedirect, fetch will follow it to /index.html
        if (response.ok || response.redirected) {
            window.location.href = response.url;
        } else {
            // Fallback redirect if something went wrong
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Erro ao realizar logout:', error);
        // Force redirect to login even if network fails
        window.location.href = '/login';
    }
});

// Fetch actual sectors from the Spring Boot backend API
async function loadSectorsFromApi() {
    try {
        const response = await fetch('/api/admin/get_sectors');
        if (response.ok) {
            const sectors = await response.json();
            db.setores = Array.isArray(sectors) ? sectors : [];
            refreshUI();
        } else {
            console.error('Falha ao carregar setores do servidor.');
        }
    } catch (error) {
        console.error('Erro de rede ao buscar setores:', error);
    }
}
