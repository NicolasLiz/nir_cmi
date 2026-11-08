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
    setores: [],  // Populated dynamically from the backend API
    quartos: [],
    leitos: []
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

async function loadRoomsFromApi() {
    try {
        const response = await fetch('/api/admin/get_rooms');
        if (response.ok) {
            const rooms = await response.json();
            db.quartos = Array.isArray(rooms) ? rooms : [];
            refreshUI();
        } else {
            console.error('Falha ao carregar quartos do servidor.');
        }
    } catch (error) {
        console.error('Erro de rede ao buscar quartos:', error);
    }
}

async function loadBedsFromApi() {
    try {
        const response = await fetch('/api/admin/get_beds');
        if (response.ok) {
            const beds = await response.json();
            db.leitos = Array.isArray(beds) ? beds : [];
            refreshUI();
        } else {
            console.error('Falha ao carregar leitos do servidor.');
        }
    } catch (error) {
        console.error('Erro de rede ao buscar leitos:', error);
    }
}

window.onload = async function() {
    await loadUsersFromApi();
    await loadSectorsFromApi();
    await loadRoomsFromApi();
    await loadBedsFromApi(); // Added beds loader
    switchTab('dashboard');
};

function saveDB() {
    localStorage.setItem('cmi_admin_quartos', JSON.stringify(db.quartos));
    localStorage.setItem('cmi_admin_leitos', JSON.stringify(db.leitos));
    refreshUI();
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
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
    // Abas atualizadas correspondentes ao HTML unificado
    const tabs = ['dashboard', 'usuarios', 'hospital'];
    
    tabs.forEach(t => {
        const section = document.getElementById(`tab-${t}`);
        const navBtn = document.getElementById(`nav-${t}`);
        
        if (section) {
            if (t === tabId) {
                section.classList.remove('hidden');
            } else {
                section.classList.add('hidden');
            }
        }
        
        if (navBtn) {
            if (t === tabId) {
                navBtn.classList.add('active-nav');
            } else {
                navBtn.classList.remove('active-nav');
            }
        }
    });

    const titles = {
        dashboard: 'Dashboard Geral',
        usuarios: 'Gerenciamento de Usuários',
        hospital: 'Leitos'
    };
    
    const pageTitleEl = document.getElementById('page-title');
    if (pageTitleEl && titles[tabId]) {
        pageTitleEl.textContent = titles[tabId];
    }
    
    refreshUI();
}

function refreshUI() {
    const elStatUsuarios = document.getElementById('stat-usuarios');
    const elStatSetores = document.getElementById('stat-setores');
    const elStatQuartos = document.getElementById('stat-quartos');
    const elStatLeitos = document.getElementById('stat-leitos');

    if (elStatUsuarios) elStatUsuarios.textContent = db.usuarios.length;
    if (elStatSetores) elStatSetores.textContent = db.setores.length;
    if (elStatQuartos) elStatQuartos.textContent = db.quartos.length;
    if (elStatLeitos) elStatLeitos.textContent = db.leitos.length;

    const dispCount = db.leitos.filter(l => l.status === 'Disponível').length;
    const ocupCount = db.leitos.filter(l => l.status === 'Ocupado').length;
    const manutCount = db.leitos.filter(l => l.status === 'Manutenção').length;

    const elDisp = document.getElementById('stat-leitos-disp');
    const elOcup = document.getElementById('stat-leitos-ocup');
    const elManut = document.getElementById('stat-leitos-manut');

    if (elDisp) elDisp.textContent = dispCount;
    if (elOcup) elOcup.textContent = ocupCount;
    if (elManut) elManut.textContent = manutCount;

    renderUsuariosTable();
    renderHospitalHierarchy();
}

function refreshUI() {
    const statUsuarios = document.getElementById('stat-usuarios');
    const statSetores = document.getElementById('stat-setores');
    const statQuartos = document.getElementById('stat-quartos');
    const statLeitos = document.getElementById('stat-leitos');

    if (statUsuarios) statUsuarios.textContent = db.usuarios.length;
    if (statSetores) statSetores.textContent = db.setores.length;
    if (statQuartos) statQuartos.textContent = db.quartos.length;
    if (statLeitos) statLeitos.textContent = db.leitos.length;

    const dispCount = db.leitos.filter(l => l.status === 'Disponível').length;
    const ocupCount = db.leitos.filter(l => l.status === 'Ocupado').length;
    const manutCount = db.leitos.filter(l => l.status === 'Manutenção').length;

    const statDisp = document.getElementById('stat-leitos-disp');
    const statOcup = document.getElementById('stat-leitos-ocup');
    const statManut = document.getElementById('stat-leitos-manut');

    if (statDisp) statDisp.textContent = dispCount;
    if (statOcup) statOcup.textContent = ocupCount;
    if (statManut) statManut.textContent = manutCount;

    renderUsuariosTable();
    renderHospitalHierarchy();
}

// Global filter wrapper for users table
function filterUsersTable() {
    renderUsuariosTable();
}

function renderUsuariosTable() {
    const tbody = document.getElementById('table-usuarios-body');
    if (!tbody) return;

    const searchVal = document.getElementById('user-search-input')?.value.toLowerCase() || '';
    const statusVal = document.getElementById('user-filter-status')?.value || '';

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

function renderHospitalHierarchy() {
    const container = document.getElementById('hospital-hierarchy-container');
    if (!container) return;

    if (!db.setores || db.setores.length === 0) {
        container.innerHTML = `<div style="padding: 2rem; text-align: center; color: #666; background: #fff; border-radius: 0.5rem; border: 1px solid #e5e7eb;">Nenhum setor cadastrado.</div>`;
        return;
    }

    container.innerHTML = db.setores.map(s => {
        const sectorName = s.sectorName || s.nome || 'Setor Sem Nome';
        const sectorDesc = s.sectorDescription || s.descricao || '';
        const sectorId = s.sectorID || s.id || '';
        const isSectorActive = s.sectorActive === true || s.sectorActive === 1 || s.sectorActive === undefined;
        const sectorBadge = isSectorActive ? '<span class="badge-green">Ativo</span>' : '<span class="badge-red">Inativo</span>';

        const sectorQuartos = db.quartos.filter(q => String(q.roomSectorID) === String(sectorId) || q.setor === sectorName);

        return `
            <div style="background: #ffffff; border: 1px solid #e5e7eb; border-radius: 0.5rem; padding: 1.25rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
                <!-- Sector Header -->
                <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #f3f4f6; padding-bottom: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                    <div>
                        <div style="display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap;">
                            <span class="badge-blue" style="font-size: 0.65rem; padding: 0.15rem 0.4rem; letter-spacing: 0.05em;">SETOR</span>
                            <h4 style="margin: 0; font-size: 1.1rem; color: #1f2937;">${sectorName}</h4>
                            ${sectorBadge}
                        </div>
                        <p style="margin: 0.25rem 0 0 0; font-size: 0.85rem; color: #6b7280;">${sectorDesc}</p>
                    </div>
                    <div style="display: flex; gap: 0.35rem; align-items: center;">
                        <button onclick="openModal('quartos', null, '${sectorId}')" class="btn-primary-custom" style="background: #d97706; padding: 0.25rem 0.5rem; font-size: 0.75rem;" title="Adicionar Quarto a este Setor">
                            <i class="fa-solid fa-plus"></i> Quarto
                        </button>
                        <button onclick="openModal('setores', '${sectorId}')" class="action-btn-edit" title="Editar Setor" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Editar</button>
                    </div>
                </div>

                <!-- Rooms (Quartos) Container -->
                <div style="display: flex; flex-direction: column; gap: 0.75rem; padding-left: 1rem;">
                    ${sectorQuartos.length > 0 ? sectorQuartos.map(q => {
                        const roomNum = q.roomIdentification || q.numero || '';
                        const roomType = q.roomType || q.tipo || '';
                        const roomId = q.roomID || q.id || '';
                        
                        const roomLeitos = db.leitos.filter(l => String(l.bedRoomID) === String(roomId) || l.quarto === roomNum);

                        return `
                            <div style="background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 0.375rem; padding: 0.875rem;">
                                <!-- Room Header -->
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; flex-wrap: wrap; gap: 0.5rem;">
                                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                                        <span class="badge-gray" style="font-size: 0.65rem; padding: 0.15rem 0.4rem; letter-spacing: 0.05em;">QUARTO</span>
                                        <span style="font-weight: 600; font-size: 0.95rem; color: #374151;">
                                            ${roomNum} <span style="font-weight: normal; color: #6b7280; font-size: 0.85rem;">(${roomType})</span>
                                        </span>
                                    </div>
                                    <div style="display: flex; gap: 0.35rem; align-items: center;">
                                        <button onclick="openModal('leitos', null, '${roomId}')" class="btn-primary-custom" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" title="Adicionar Leito a este Quarto">
                                            <i class="fa-solid fa-plus"></i> Leito
                                        </button>
                                        <button onclick="openModal('quartos', '${roomId}')" class="action-btn-edit" style="font-size: 0.7rem; padding: 0.2rem 0.4rem;">Editar</button>
                                    </div>
                                </div>

                                <!-- Beds (Leitos) Badges Grid -->
                                <div style="display: flex; flex-wrap: wrap; gap: 0.5rem; margin-top: 0.5rem; padding-top: 0.5rem; border-top: 1px dashed #e5e7eb;">
                                        ${roomLeitos.length > 0 ? roomLeitos.map(l => {
                                        const bedId = l.bedID || l.id || '';
                                        const bedNum = l.bedNumber || l.numero || '';
                                        const rawStatus = l.bedStatus !== undefined ? Number(l.bedStatus) : (l.status !== undefined ? Number(l.status) : 1);

                                        let bedStatusText = 'Disponível';
                                        let badgeStyle = 'background: #dcfce7; color: #166534; border: 1px solid #bbf7d0;';

                                        if ((rawStatus & 2) !== 0) { // bStatBusy
                                            bedStatusText = 'Ocupado';
                                            badgeStyle = 'background: #fee2e2; color: #991b1b; border: 1px solid #fecaca;';
                                        } else if ((rawStatus & 4) !== 0) { // bStatReserved
                                            bedStatusText = 'Reservado';
                                            badgeStyle = 'background: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd;';
                                        } else if ((rawStatus & 8) !== 0) { // bStatHigienization
                                            bedStatusText = 'Higienização';
                                            badgeStyle = 'background: #f3e8ff; color: #6b21a8; border: 1px solid #e9d5ff;';
                                        } else if ((rawStatus & 16) !== 0) { // bStatMaintenance
                                            bedStatusText = 'Manutenção';
                                            badgeStyle = 'background: #fef9c3; color: #854d0e; border: 1px solid #fef08a;';
                                        } else if ((rawStatus & 32) !== 0) { // bStatBlocked
                                            bedStatusText = 'Bloqueado';
                                            badgeStyle = 'background: #f3f4f6; color: #374151; border: 1px solid #d1d5db;';
                                        }

                                        return `
                                            <div style="display: flex; align-items: center; gap: 0.4rem; padding: 0.25rem 0.5rem; border-radius: 0.375rem; font-size: 0.8rem; ${badgeStyle}">
                                                <span style="font-size: 0.6rem; font-weight: 700; background: rgba(0,0,0,0.08); padding: 0.1rem 0.3rem; border-radius: 0.2rem;">LEITO</span>
                                                <span>${bedNum} (${bedStatusText})</span>
                                                <button onclick="openModal('leitos', '${bedId}')" title="Editar Leito" style="background: none; border: none; cursor: pointer; color: inherit; padding: 0; margin-left: 0.25rem;"><i class="fa-solid fa-pen" style="font-size: 0.65rem;"></i></button>
                                                <button onclick="deleteRecord('leitos', '${bedId}')" title="Excluir Leito" style="background: none; border: none; cursor: pointer; color: inherit; padding: 0;"><i class="fa-solid fa-xmark" style="font-size: 0.7rem;"></i></button>
                                            </div>
                                        `;
                                    }).join('') : '<span style="font-size: 0.8rem; color: #9ca3af; font-style: italic;">Nenhum leito cadastrado neste quarto.</span>'}
                                </div>
                            </div>
                        `;
                    }).join('') : '<p style="font-size: 0.85rem; color: #9ca3af; font-style: italic; margin: 0; padding-left: 0.5rem;">Nenhum quarto cadastrado neste setor.</p>'}
                </div>
            </div>
        `;
    }).join('');
}

function openModal(entity, id = null, parentContext = null) {
    const container = document.getElementById('modal-container');
    const titleEl = document.getElementById('modal-title');
    const entityInput = document.getElementById('form-entity');
    const idInput = document.getElementById('form-id');
    const fieldsContainer = document.getElementById('form-fields-container');

    if (!container || !entityInput || !idInput || !fieldsContainer) return;

    entityInput.value = entity;
    
    const cleanId = (id !== null && id !== undefined && id !== 'null' && id !== '') ? id : '';
    idInput.value = cleanId;

    let item = null;
    if (cleanId !== '') {
        // Added bedID to the lookup chain
        item = db[entity].find(i => String(i.bedID || i.roomID || i.sectorID || i.userID || i.id) === String(cleanId));
        if (titleEl) {
            titleEl.textContent = `Editar ${entity === 'usuarios' ? 'Usuário' : entity === 'setores' ? 'Setor' : entity === 'quartos' ? 'Quarto' : 'Leito'}`;
        }
    } else {
        if (titleEl) {
            titleEl.textContent = `Adicionar ${entity === 'usuarios' ? 'Usuário' : entity === 'setores' ? 'Setor' : entity === 'quartos' ? 'Quarto' : 'Leito'}`;
        }
    }

    let html = '';
    if (entity === 'usuarios') {
        const userPerms = item && item.userPermissions !== undefined ? BigInt(item.userPermissions) : 0n;
        const isUserActive = item ? (item.userActive !== false && item.userActive !== 0) : true;

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
        const sectorName = item ? (item.sectorName || item.nome || '') : '';
        const sectorDesc = item ? (item.sectorDescription || item.descricao || '') : '';
        const isSectorActive = item ? (item.sectorActive === true || item.sectorActive === 1 || item.sectorActive === undefined) : true;

        html = `
            <div class="form-field">
                <label>Nome do Setor</label>
                <input type="text" id="field-nome" class="input-custom" value="${sectorName}" required placeholder="Ex: UTI Pediátrica">
            </div>
            <div class="form-field">
                <label>Descrição</label>
                <input type="text" id="field-descricao" class="input-custom" value="${sectorDesc}" required placeholder="Breve descrição do setor">
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
        const roomNum = item ? (item.roomIdentification || item.numero || '') : '';
        const roomType = item ? (item.roomType || item.tipo || '') : '';
        const roomSectorID = item ? (item.roomSectorID || '') : (parentContext || '');
        const isRoomActive = item ? (item.roomActive === true || item.roomActive === 1 || item.roomActive === undefined) : true;

        const setoresOptions = db.setores.map(s => {
            const sId = s.sectorID || s.id;
            const sName = s.sectorName || s.nome;
            const isSelected = String(roomSectorID) === String(sId) || String(roomSectorID) === String(sName);
            return `<option value="${sId}" ${isSelected ? 'selected' : ''}>${sName}</option>`;
        }).join('');

        html = `
            <div class="form-field">
                <label>Número / Identificação do Quarto</label>
                <input type="text" id="field-numero" class="input-custom" value="${roomNum}" required placeholder="Ex: Quarto 302">
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
                <input type="text" id="field-tipo" class="input-custom" value="${roomType}" required placeholder="Ex: UTI Padrão, Isolamento">
            </div>
            <div class="form-field">
                <label>Status do Quarto</label>
                <select id="field-ativo" class="input-custom">
                    <option value="true" ${isRoomActive ? 'selected' : ''}>Ativo</option>
                    <option value="false" ${!isRoomActive ? 'selected' : ''}>Inativo</option>
                </select>
            </div>
        `;
    } else if (entity === 'leitos') {
        const bedNum = item ? (item.bedNumber || item.numero || '') : '';
        const bedType = item && item.bedType !== undefined ? Number(item.bedType) : 1;
        const bedStatus = item && item.bedStatus !== undefined ? item.bedStatus : (item && item.status !== undefined ? item.status : 0);
        const bedObs = item ? (item.bedObservation || '') : '';
        const bedRoomID = item ? (item.bedRoomID || '') : (parentContext || '');

        const quartosOptions = db.quartos.map(q => {
            const qId = q.roomID || q.id;
            const qNum = q.roomIdentification || q.numero;
            const isSelected = String(bedRoomID) === String(qId) || String(bedRoomID) === String(qNum);
            return `<option value="${qId}" ${isSelected ? 'selected' : ''}>${qNum} (${q.setor || ''})</option>`;
        }).join('');

        html = `
            <div class="form-field">
                <label>Número / Identificação do Leito</label>
                <input type="text" id="field-numero" class="input-custom" value="${bedNum}" required placeholder="Ex: Leito 01">
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
                    <option value="1" ${Number(bedStatus) === 1 || bedStatus === 1 ? 'selected' : ''}>Disponível</option>
                    <option value="2" ${Number(bedStatus) === 2 ? 'selected' : ''}>Ocupado</option>
                    <option value="4" ${Number(bedStatus) === 4 ? 'selected' : ''}>Reservado</option>
                    <option value="8" ${Number(bedStatus) === 8 ? 'selected' : ''}>Higienização</option>
                    <option value="16" ${Number(bedStatus) === 16 ? 'selected' : ''}>Manutenção</option>
                    <option value="32" ${Number(bedStatus) === 32 ? 'selected' : ''}>Bloqueado</option>
                </select>
            </div>
            <div class="form-field">
                <label>Observações</label>
                <input type="text" id="field-obs" class="input-custom" value="${bedObs}" placeholder="Observações opcionais">
            </div>
        `;
    }

    fieldsContainer.innerHTML = html;
    container.classList.remove('hidden');
}

function closeModal() {
    const container = document.getElementById('modal-container');
    const form = document.getElementById('generic-form');
    if (container) container.classList.add('hidden');
    if (form) form.reset();
}

async function handleFormSubmit(event) {
    event.preventDefault();
    const entity = document.getElementById('form-entity').value;

    if (entity === 'usuarios') {
        // Calculate the combined bitmask from all checked permission checkboxes
        let totalPermissions = 0n;
        const checkboxes = document.querySelectorAll('input[name="perm-checkbox"]:checked');
        
        // Check if all checkboxes are selected (Admin role)
        const allCheckboxes = document.querySelectorAll('input[name="perm-checkbox"]');
        const isAllChecked = checkboxes.length === allCheckboxes.length && allCheckboxes.length > 0;

        if (isAllChecked) {
            totalPermissions = -1n; // Keeps full admin permissions (-1)
        } else {
            checkboxes.forEach(cb => {
                totalPermissions |= BigInt(cb.value);
            });
        }

        const userId = document.getElementById('form-id').value;
        const payload = {
            userID: userId !== '' ? userId : null,
            userName: document.getElementById('field-nome').value,
            userEmail: document.getElementById('field-email').value,
            userPassword: document.getElementById('field-senha').value,
            // Send as string or number depending on your Spring Boot DTO. 
            // Passing Number(-1) or keeping it as BigInt string conversion ensures precision if parsed as Long.
            userPermissions: totalPermissions === -1n ? -1 : Number(totalPermissions),
            userActive: document.getElementById('field-ativo').value === 'true' ? 1 : 0
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

    if (entity === 'setores') {
        const sectorId = document.getElementById('form-id').value;
        const activeSelect = document.getElementById('field-ativo');
        
        const payload = {
            sectorID: sectorId !== '' ? sectorId : null,
            sectorName: document.getElementById('field-nome').value,
            sectorDescription: document.getElementById('field-descricao').value,
            sectorActive: activeSelect ? (activeSelect.value === 'true' ? 1 : 0) : 1
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
    }

    // Local storage handling for quartos and leitos
    const idVal = document.getElementById('form-id').value;
    const id = idVal !== '' ? parseInt(idVal) : null;
    let newData = {};

    if (entity === 'quartos') {
        const roomId = document.getElementById('form-id').value;
        const activeSelect = document.getElementById('field-ativo');

        const payload = {
            roomID: roomId !== '' ? parseInt(roomId) : null,
            roomSectorID: parseInt(document.getElementById('field-setor').value),
            roomIdentification: document.getElementById('field-numero').value,
            roomType: document.getElementById('field-tipo').value,
            roomActive: activeSelect ? (activeSelect.value === 'true' ? 1 : 0) : 1
        };

        const endpoint = roomId !== '' ? '/api/admin/update_room' : '/api/admin/register_room';

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast(resultText || 'Quarto salvo com sucesso!');
                await loadRoomsFromApi();
                closeModal();
            } else {
                showToast(resultText || 'Erro ao salvar quarto.', 'danger');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            showToast('Erro de conexão com o servidor.', 'danger');
        }
        return;
    } else if (entity === 'leitos') {
        const bedId = document.getElementById('form-id').value;
        const existingBed = bedId !== '' ? db.leitos.find(l => String(l.bedID || l.id) === String(bedId)) : null;

        const payload = {
            bedID: bedId !== '' ? parseInt(bedId) : null,
            bedRoomID: parseInt(document.getElementById('field-quarto').value),
            bedNumber: document.getElementById('field-numero').value,
            bedType: parseInt(document.getElementById('field-tipo').value), // Sent as integer
            bedStatus: parseInt(document.getElementById('field-status').value), // Sent as integer
            bedObservation: document.getElementById('field-obs').value,
            bedRegisterDate: existingBed && existingBed.bedRegisterDate ? existingBed.bedRegisterDate : new Date().toISOString()
        };

        const endpoint = bedId !== '' ? '/api/admin/update_bed' : '/api/admin/register_bed';

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast(resultText || 'Leito salvo com sucesso!');
                await loadBedsFromApi();
                closeModal();
            } else {
                showToast(resultText || 'Erro ao salvar leito.', 'danger');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            showToast('Erro de conexão com o servidor.', 'danger');
        }
        return;
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

const logoutBtn = document.getElementById('logout-btn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', async function(e) {
        e.preventDefault();

        try {
            const response = await fetch('/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok || response.redirected) {
                window.location.href = response.url || '/index.html';
            } else {
                window.location.href = '/index.html';
            }
        } catch (error) {
            console.error('Erro ao realizar logout:', error);
            window.location.href = '/index.html';
        }
    });
}
