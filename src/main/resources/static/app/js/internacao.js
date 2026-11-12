// Global cache for hospital data
const appHospitalState = {
    setores: [],
    quartos: [],
    leitos: [],
    pacientes: [],
    internacoes: [],
    altas: [],
    selectedBed: null,
    transferSelectedBed: null,
    currentTransferAdmission: null
};

// Constants
const BITMASK_AVAILABLE = 1;
const BITMASK_BUSY = 2;

const ADMISSION_STATUS = {
    INACTIVE: 0,
    OPEN: 1,
    CLOSED: 2
};

document.addEventListener('DOMContentLoaded', () => {
    loadPatientsAndAdmissions();
});

async function loadDischargeModule() {
    const activeTbody = document.getElementById('discharge-active-tbody');
    const historyTbody = document.getElementById('discharge-history-tbody');

    activeTbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #6b7280; padding: 1.5rem;">Carregando pacientes internadas...</td></tr>`;
    historyTbody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #6b7280; padding: 1.5rem;">Carregando histórico de altas...</td></tr>`;

    try {
        const [patRes, admRes, bedRes, roomRes, disRes] = await Promise.all([
            fetch('/api/get_patients'),
            fetch('/api/get_admissions'),
            fetch('/api/get_beds'),
            fetch('/api/get_rooms'),
            fetch('/api/get_discharges')
        ]);

        if (patRes.ok) appHospitalState.pacientes = await patRes.json();
        if (admRes.ok) appHospitalState.internacoes = await admRes.json();
        if (bedRes.ok) appHospitalState.leitos = await bedRes.json();
        if (roomRes.ok) appHospitalState.quartos = await roomRes.json();
        
        let dischargesList = [];
        if (disRes.ok) {
            const dData = await disRes.json();
            dischargesList = Array.isArray(dData) ? dData : [];
            appHospitalState.altas = dischargesList;
        }

        // Active Admissions for discharge
        const activeAdmissions = appHospitalState.internacoes.filter(a => a.admissionStatus === ADMISSION_STATUS.OPEN);

        if (activeAdmissions.length === 0) {
            activeTbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhuma paciente internada no momento.</td></tr>`;
        } else {
            activeTbody.innerHTML = activeAdmissions.map(a => {
                const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(a.admissionPatientID));
                const patientName = patient ? patient.patientName : `ID: ${a.admissionPatientID}`;
                const currentBed = findCurrentBedForAdmission(a, patient);
                const bedDisplayName = currentBed ? `Quarto ${getRoomName(currentBed.bedRoomID)} - Leito ${currentBed.bedNumber || currentBed.numero}` : 'Leito não identificado';

                return `
                    <tr>
                        <td>${a.admissionID}</td>
                        <td style="font-weight: 600;">${patientName}</td>
                        <td>${a.admissionInDate ? a.admissionInDate.replace('T', ' ').substring(0, 19) : '-'}</td>
                        <td><span style="background: #f1f5f9; color: #334155; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-weight: 600; font-size: 0.85rem;">${bedDisplayName}</span></td>
                        <td>
                            <button onclick="executeDischarge(${a.admissionID}, ${a.admissionPatientID})" class="btn-primary-custom" style="padding: 0.3rem 0.6rem; font-size: 0.8rem; background-color: #10b981;">
                                <i class="fa-solid fa-person-walking-arrow-right"></i> Dar Alta
                            </button>
                        </td>
                    </tr>
                `;
            }).join('');
        }

        // History of Discharges
        if (dischargesList.length === 0) {
            historyTbody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhuma alta registrada.</td></tr>`;
        } else {
            historyTbody.innerHTML = dischargesList.map(d => {
                const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(d.dischargePatientID));
                const patientName = patient ? patient.patientName : `ID: ${d.dischargePatientID}`;

                return `
                    <tr>
                        <td>${d.dischargeID || '-'}</td>
                        <td>${d.dischargePatientID || '-'}</td>
                        <td style="font-weight: 600;">${patientName}</td>
                        <td>${d.dischargeDate ? d.dischargeDate.replace('T', ' ').substring(0, 19) : '-'}</td>
                    </tr>
                `;
            }).join('');
        }

    } catch (error) {
        console.error('Erro ao carregar módulo de altas:', error);
        activeTbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro ao carregar dados de alta.</td></tr>`;
        historyTbody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro ao carregar histórico.</td></tr>`;
    }
}


function switchTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
    document.querySelectorAll('.sidebar-nav-btn').forEach(el => el.classList.remove('active'));

    document.getElementById(`tab-${tabName}`).classList.remove('hidden');
    document.getElementById(`btn-tab-${tabName}`).classList.add('active');

    if (tabName === 'lista') {
        loadPatientsAndAdmissions();
    } else if (tabName === 'internacao') {
        initAdmissionData();
    } else if (tabName === 'transferencias') {
        loadTransferTable();
    } else if (tabName === 'alta') {
        loadDischargeModule();
    } else if (tabName === 'lista-internacoes') {
        loadAdmissions();
    } else if (tabName === 'mapa-leitos') {
        loadBedMap();
    } else if (tabName === 'indicadores') {
        loadHospitalIndicators();
    }
}

async function handlePatientRegistration(event) {
    event.preventDefault();

    const payload = {
        patientName: document.getElementById('pat-name').value.trim(),
        patientBirthDate: document.getElementById('pat-dob').value ? document.getElementById('pat-dob').value + "T00:00:00" : null,
        patientDocument: document.getElementById('pat-doc').value.trim(),
        patientChart: document.getElementById('pat-record').value.trim()
    };

    try {
        const response = await fetch('/api/register_patient', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const resultText = await response.text();

        if (response.ok) {
            showToast(resultText || 'Paciente cadastrada com sucesso!');
            document.getElementById('form-cadastrar-paciente').reset();
            loadPatientsAndAdmissions();
        } else {
            showToast(resultText || 'Erro ao cadastrar paciente.', 'danger');
        }
    } catch (error) {
        console.error('Erro de rede:', error);
        showToast('Erro de conexão com o servidor.', 'danger');
    }
}

async function loadPatientsAndAdmissions() {
    const tbody = document.getElementById('patients-table-body');
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #6b7280; padding: 1.5rem;">Carregando dados...</td></tr>`;

    try {
        const [patRes, admRes] = await Promise.all([
            fetch('/api/get_patients'),
            fetch('/api/get_admissions')
        ]);

        if (patRes.ok) {
            const patData = await patRes.json();
            appHospitalState.pacientes = Array.isArray(patData) ? patData : [];
        }

        if (admRes.ok) {
            const admData = await admRes.json();
            appHospitalState.internacoes = Array.isArray(admData) ? admData : [];
        }

        if (appHospitalState.pacientes.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhuma paciente cadastrada.</td></tr>`;
            return;
        }

        const activeAdmittedPatientIDs = new Set(
            appHospitalState.internacoes
                .filter(a => a.admissionStatus === ADMISSION_STATUS.OPEN)
                .map(a => String(a.admissionPatientID))
        );

        tbody.innerHTML = appHospitalState.pacientes.map(p => {
            const isAdmitted = activeAdmittedPatientIDs.has(String(p.patientID));

            let actionButton = '';
            if (isAdmitted) {
                actionButton = `<button onclick="viewPatientAdmission(${p.patientID})" class="btn-secondary" style="padding: 0.3rem 0.6rem; font-size: 0.8rem; background-color: #dbeafe; color: #1e40af; border: 1px solid #bfdbfe;">
                    <i class="fa-solid fa-bed-pulse"></i> Ver Internação
                </button>`;
            } else {
                actionButton = `<button onclick="prepareAdmissionForPatient(${p.patientID})" class="btn-primary-custom" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;">
                    <i class="fa-solid fa-hospital-user"></i> Internar
                </button>`;
            }

            return `
                <tr>
                    <td>${p.patientID || '-'}</td>
                    <td style="font-weight: 600;">${p.patientName || '-'}</td>
                    <td>${p.patientBirthDate ? p.patientBirthDate.substring(0, 10) : '-'}</td>
                    <td>${p.patientDocument || '-'}</td>
                    <td>${p.patientChart || '-'}</td>
                    <td>${actionButton}</td>
                </tr>
            `;
        }).join('');

    } catch (error) {
        console.error('Erro de rede:', error);
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro de conexão com o servidor.</td></tr>`;
    }
}

async function prepareAdmissionForPatient(patientId) {
    switchTab('internacao');
    await initAdmissionData();
    const patientSelect = document.getElementById('admission-patient');
    if (patientSelect) {
        patientSelect.value = patientId;
    }
}

async function viewPatientAdmission(patientId) {
    switchTab('lista-internacoes');
}

// --- Admission Workflow & Terminal Tree Rendering ---

async function initAdmissionData() {
    const patientSelect = document.getElementById('admission-patient');
    const treeContainer = document.getElementById('hospital-tree-container');

    patientSelect.innerHTML = '<option value="">Carregando pacientes...</option>';
    treeContainer.innerHTML = '<div style="color: #94a3b8; padding: 1rem; text-align: center;">$ loading hospital_topology...</div>';
    appHospitalState.selectedBed = null;
    updateSelectedBedDisplay();

    try {
        if (appHospitalState.pacientes.length === 0) {
            const patRes = await fetch('/api/get_patients');
            if (patRes.ok) {
                const patData = await patRes.json();
                appHospitalState.pacientes = Array.isArray(patData) ? patData : [];
            }
        }

        patientSelect.innerHTML = appHospitalState.pacientes.length > 0 
            ? '<option value="">Selecione a paciente...</option>' + appHospitalState.pacientes.map(p => `<option value="${p.patientID}">${p.patientName} (Prontuário: ${p.patientChart || 'N/A'})</option>`).join('')
            : '<option value="">Nenhuma paciente cadastrada</option>';

        const [resSetores, resQuartos, resLeitos] = await Promise.all([
            fetch('/api/get_sectors'),
            fetch('/api/get_rooms'),
            fetch('/api/get_beds')
        ]);

        if (resSetores.ok) appHospitalState.setores = await resSetores.json();
        if (resQuartos.ok) appHospitalState.quartos = await resQuartos.json();
        if (resLeitos.ok) appHospitalState.leitos = await resLeitos.json();

        renderTerminalTree('hospital-tree-container', 'bed-node', selectBed);

    } catch (e) {
        console.error('Erro ao inicializar dados de internação:', e);
        treeContainer.innerHTML = '<div style="color: #f87171; padding: 1rem; text-align: center;">[ERROR] Falha ao carregar topologia hospitalar.</div>';
        showToast('Erro ao carregar dados hospitalares.', 'danger');
    }
}

function renderTerminalTree(containerId, nodeClassPrefix, selectCallbackName) {
    const treeContainer = document.getElementById(containerId);
    const activeSectors = appHospitalState.setores.filter(s => s.sectorActive !== false && s.sectorActive !== 0);

    if (activeSectors.length === 0) {
        treeContainer.innerHTML = '<div style="color: #94a3b8; padding: 1rem; text-align: center;">Nenhum setor ativo encontrado.</div>';
        return;
    }

    treeContainer.innerHTML = activeSectors.map((sector, sIdx) => {
        const sectorId = sector.sectorID || sector.id;
        const sectorName = sector.sectorName || sector.nome;
        const activeRooms = appHospitalState.quartos.filter(q => 
            String(q.roomSectorID) === String(sectorId) && 
            (q.roomActive !== false && q.roomActive !== 0)
        );

        const isLastSector = sIdx === activeSectors.length - 1;
        const sectorPrefix = isLastSector ? '└── ' : '├── ';

        return `
            <div class="tree-sector expanded" id="${containerId}-sector-${sectorId}">
                <div class="tree-sector-header" onclick="toggleTreeSection('${containerId}-sector-${sectorId}')">
                    <span>${sectorPrefix}📁 Setor: ${sectorName}</span>
                </div>
                <div class="tree-sector-body">
                    ${activeRooms.length === 0 ? '<div style="color: #64748b; padding-left: 2rem;">└── (Nenhum quarto ativo)</div>' : 
                        activeRooms.map((room, rIdx) => {
                            const roomId = room.roomID || room.id;
                            const roomName = room.roomIdentification || room.numero;
                            const roomType = room.roomType || room.tipo || 'Padrão';
                            const roomBeds = appHospitalState.leitos.filter(l => 
                                String(l.bedRoomID) === String(roomId) && 
                                (l.bedActive !== 0 && l.bed_active !== 0)
                            );

                            const isLastRoom = rIdx === activeRooms.length - 1;
                            const roomPrefix = isLastRoom ? '└── ' : '├── ';

                            return `
                                <div class="tree-room expanded" id="${containerId}-room-${roomId}">
                                    <div class="tree-room-header" onclick="toggleTreeSection('${containerId}-room-${roomId}')">
                                        <span>${roomPrefix}🚪 Quarto ${roomName} [${roomType}]</span>
                                    </div>
                                    <div class="tree-room-body">
                                        ${roomBeds.length === 0 ? '<div style="color: #64748b; padding-left: 2rem;">└── (Nenhum leito)</div>' :
                                            roomBeds.map((bed, bIdx) => {
                                                const bedId = bed.bedID || bed.id;
                                                const bedNumber = bed.bedNumber || bed.numero;
                                                const rawStatus = Number(bed.bedStatus !== undefined ? bed.bedStatus : (bed.status !== undefined ? bed.status : 1));
                                                const isAvailable = (rawStatus & 1) !== 0;

                                                const isLastBed = bIdx === roomBeds.length - 1;
                                                const bedPrefix = isLastBed ? '└── ' : '├── ';

                                                const bedClass = isAvailable ? `tree-bed-node available ${nodeClassPrefix}` : 'tree-bed-node busy';
                                                const statusText = isAvailable ? 'DISPONÍVEL' : 'OCUPADO';
                                                const clickAction = isAvailable ? `${selectCallbackName.name}(${bedId}, '${bedNumber}', '${roomName}')` : `showToast('Este leito está ocupado.', 'danger')`;

                                                return `
                                                    <div style="margin-left: 1.5rem;">
                                                        <span class="${bedClass}" id="${nodeClassPrefix}-${bedId}" onclick="${clickAction}">
                                                            ${bedPrefix}🛏️ Leito ${bedNumber} (${statusText})
                                                        </span>
                                                    </div>
                                                `;
                                            }).join('')
                                        }
                                    </div>
                                </div>
                            `;
                        }).join('')
                    }
                </div>
            </div>
        `;
    }).join('');
}

function toggleTreeSection(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.classList.toggle('expanded');
    }
}

function selectBed(bedId, bedNumber, roomName) {
    document.querySelectorAll('.bed-node').forEach(el => el.classList.remove('selected'));

    const bedNode = document.getElementById(`bed-node-${bedId}`);
    if (bedNode) {
        bedNode.classList.add('selected');
    }

    document.getElementById('admission-bed-id').value = bedId;
    appHospitalState.selectedBed = appHospitalState.leitos.find(l => String(l.bedID || l.id) === String(bedId));

    updateSelectedBedDisplay(roomName, bedNumber);
}

function updateSelectedBedDisplay(roomName = '', bedNumber = '') {
    const display = document.getElementById('selected-bed-display');
    if (roomName && bedNumber) {
        display.className = 'selected-bed-banner selected';
        display.innerHTML = `<i class="fa-solid fa-circle-check" style="color: #2563eb;"></i> Leito Selecionado: Quarto ${roomName} - Leito ${bedNumber}`;
    } else {
        display.className = 'selected-bed-banner';
        display.innerHTML = `<i class="fa-solid fa-bed"></i> Nenhum leito selecionado`;
    }
}

// --- Discharge (Alta) Workflow Functions ---

async function executeDischarge(admissionID, patientID) {
    if (!confirm('Tem certeza que deseja registrar a alta hospitalar para esta paciente? O leito será encaminhado para higienização.')) {
        return;
    }

    const currentTimestamp = new Date().toISOString();
    const admission = appHospitalState.internacoes.find(a => String(a.admissionID) === String(admissionID));
    const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(patientID));
    const currentBed = findCurrentBedForAdmission(admission, patient);

    if (!currentBed) {
        showToast('Não foi possível identificar o leito atual para liberar.', 'danger');
        return;
    }

    const dischargePayload = {
        dischargePatientID: parseInt(patientID),
        dischargeDate: currentTimestamp
    };

    const closedAdmissionPayload = {
        admissionID: parseInt(admission.admissionID),
        admissionPatientID: parseInt(admission.admissionPatientID),
        admissionInDate: admission.admissionInDate,
        admissionOutDate: currentTimestamp,
        admissionStatus: ADMISSION_STATUS.CLOSED, // Status 2 = Fechada
        admissionObservation: `Alta concedida em ${currentTimestamp} | ${admission.admissionObservation || ''}`
    };

    // Bitmask para Higienização (1 << 3 = 8) conforme a regra da maternidade
    const BITMASK_HIGIENIZATION = 8;

    const bedHigienizationPayload = {
        bedID: parseInt(currentBed.bedID || currentBed.id),
        bedRoomID: parseInt(currentBed.bedRoomID),
        bedNumber: currentBed.bedNumber || currentBed.numero,
        bedType: parseInt(currentBed.bedType || 1),
        bedStatus: BITMASK_HIGIENIZATION,
        bedActive: 1,
        bedObservation: `Aguardando higienização após alta da paciente ${patient ? patient.patientName : 'N/A'} em ${currentTimestamp}`,
        bedRegisterDate: currentTimestamp
    };

    try {
        const [disRes, admRes, bedRes] = await Promise.all([
            fetch('/api/register_discharge', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dischargePayload)
            }),
            fetch('/api/update_admission', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(closedAdmissionPayload)
            }),
            fetch('/api/update_bed', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bedHigienizationPayload)
            })
        ]);

        const disText = await disRes.text();

        if (disRes.ok && bedRes.ok) {
            showToast('Alta hospitalar registrada com sucesso! Leito encaminhado para higienização.');
            loadDischargeModule();
        } else {
            showToast(disText || 'Erro ao registrar alta.', 'danger');
        }
    } catch (error) {
        console.error('Erro de rede na alta:', error);
        showToast('Erro de conexão com o servidor.', 'danger');
    }
}

// --- Transfer Workflow Functions ---

async function loadTransferTable() {
    const tbody = document.getElementById('transfer-patients-tbody');
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #6b7280; padding: 1.5rem;">Carregando pacientes internadas...</td></tr>`;

    cancelTransfer();

    try {
        const [patRes, admRes, secRes, bedRes, roomRes] = await Promise.all([
            fetch('/api/get_patients'),
            fetch('/api/get_admissions'),
            fetch('/api/get_sectors'),
            fetch('/api/get_beds'),
            fetch('/api/get_rooms')
        ]);

        if (patRes.ok) appHospitalState.pacientes = await patRes.json();
        if (admRes.ok) appHospitalState.internacoes = await admRes.json();
        if (secRes.ok) appHospitalState.setores = await secRes.json();
        if (bedRes.ok) appHospitalState.leitos = await bedRes.json();
        if (roomRes.ok) appHospitalState.quartos = await roomRes.json();

        const activeAdmissions = appHospitalState.internacoes.filter(a => a.admissionStatus === ADMISSION_STATUS.OPEN);

        if (activeAdmissions.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhuma paciente internada no momento.</td></tr>`;
            return;
        }

        tbody.innerHTML = activeAdmissions.map(a => {
            const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(a.admissionPatientID));
            const patientName = patient ? patient.patientName : `ID: ${a.admissionPatientID}`;

            const currentBed = findCurrentBedForAdmission(a, patient);
            const bedDisplayName = currentBed ? `Quarto ${getRoomName(currentBed.bedRoomID)} - Leito ${currentBed.bedNumber || currentBed.numero}` : 'Leito não identificado';

            return `
                <tr>
                    <td>${a.admissionID}</td>
                    <td style="font-weight: 600;">${patientName}</td>
                    <td>${a.admissionInDate ? a.admissionInDate.replace('T', ' ').substring(0, 19) : '-'}</td>
                    <td><span style="background: #f1f5f9; color: #334155; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-weight: 600; font-size: 0.85rem;">${bedDisplayName}</span></td>
                    <td>
                        <button onclick="openTransferForm(${a.admissionID})" class="btn-primary-custom" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;">
                            <i class="fa-solid fa-right-left"></i> Transferir
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

    } catch (error) {
        console.error('Erro ao carregar transferências:', error);
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro ao carregar dados de transferência.</td></tr>`;
    }
}

function findCurrentBedForAdmission(admission, patient) {
    if (!patient) return null;
    return appHospitalState.leitos.find(l => {
        const obs = l.bedObservation || l.observacao || '';
        const isBusy = (Number(l.bedStatus || l.status || 1) & BITMASK_BUSY) !== 0;
        return isBusy && (obs.includes(patient.patientName) || obs.includes(`Paciente: ${patient.patientName}`));
    }) || appHospitalState.leitos.find(l => (Number(l.bedStatus || l.status || 1) & BITMASK_BUSY) !== 0);
}

function getRoomName(roomId) {
    const room = appHospitalState.quartos.find(q => String(q.roomID || q.id) === String(roomId));
    return room ? (room.roomIdentification || room.numero) : roomId;
}

async function openTransferForm(admissionID) {
    const admission = appHospitalState.internacoes.find(a => String(a.admissionID) === String(admissionID));
    if (!admission) return;

    const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(admission.admissionPatientID));
    const currentBed = findCurrentBedForAdmission(admission, patient);

    if (!currentBed) {
        showToast('Não foi possível identificar o leito atual desta internação.', 'danger');
        return;
    }

    appHospitalState.currentTransferAdmission = admission;
    appHospitalState.transferSelectedBed = null;

    document.getElementById('trans-admission-id').value = admission.admissionID;
    document.getElementById('trans-from-bed-id').value = currentBed.bedID || currentBed.id;
    document.getElementById('trans-patient-name').value = patient ? patient.patientName : `ID: ${admission.admissionPatientID}`;
    document.getElementById('trans-current-bed-name').value = `Quarto ${getRoomName(currentBed.bedRoomID)} - Leito ${currentBed.bedNumber || currentBed.numero}`;
    document.getElementById('trans-reason').value = '';

    updateTransferSelectedBedDisplay();

    renderTerminalTree('transfer-hospital-tree-container', 'trans-bed-node', selectTransferBed);

    document.getElementById('transfer-form-container').classList.remove('hidden');
    document.getElementById('transfer-form-container').scrollIntoView({ behavior: 'smooth' });
}

function cancelTransfer() {
    document.getElementById('transfer-form-container').classList.add('hidden');
    appHospitalState.currentTransferAdmission = null;
    appHospitalState.transferSelectedBed = null;
}

function selectTransferBed(bedId, bedNumber, roomName) {
    document.querySelectorAll('.trans-bed-node').forEach(el => el.classList.remove('selected'));

    const bedNode = document.getElementById(`trans-bed-node-${bedId}`);
    if (bedNode) {
        bedNode.classList.add('selected');
    }

    document.getElementById('trans-to-bed-id').value = bedId;
    appHospitalState.transferSelectedBed = appHospitalState.leitos.find(l => String(l.bedID || l.id) === String(bedId));

    updateTransferSelectedBedDisplay(roomName, bedNumber);
}

function updateTransferSelectedBedDisplay(roomName = '', bedNumber = '') {
    const display = document.getElementById('transfer-selected-bed-display');
    if (roomName && bedNumber) {
        display.className = 'selected-bed-banner selected';
        display.innerHTML = `<i class="fa-solid fa-circle-check" style="color: #2563eb;"></i> Novo Leito Selecionado: Quarto ${roomName} - Leito ${bedNumber}`;
    } else {
        display.className = 'selected-bed-banner';
        display.innerHTML = `<i class="fa-solid fa-bed"></i> Nenhum leito de destino selecionado`;
    }
}

async function handleTransferSubmit(event) {
    event.preventDefault();

    const admissionID = document.getElementById('trans-admission-id').value;
    const fromBedID = document.getElementById('trans-from-bed-id').value;
    const toBedID = document.getElementById('trans-to-bed-id').value;
    const reason = document.getElementById('trans-reason').value.trim();

    if (!admissionID || !fromBedID || !toBedID || !appHospitalState.transferSelectedBed) {
        showToast('Selecione o leito de destino no organograma.', 'danger');
        return;
    }

    const currentTimestamp = new Date().toISOString();
    const targetAdmission = appHospitalState.currentTransferAdmission;
    const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(targetAdmission.admissionPatientID));
    const oldBed = appHospitalState.leitos.find(l => String(l.bedID || l.id) === String(fromBedID));
    const newBed = appHospitalState.transferSelectedBed;

    const transferPayload = {
        transferAdmissionID: parseInt(admissionID),
        transferFromBedID: parseInt(fromBedID),
        transferToBedID: parseInt(newBed.bedID || newBed.id),
        transferDate: currentTimestamp,
        transferReason: reason
    };

    const oldBedUpdatePayload = {
        bedID: parseInt(oldBed.bedID || oldBed.id),
        bedRoomID: parseInt(oldBed.bedRoomID),
        bedNumber: oldBed.bedNumber || oldBed.numero,
        bedType: parseInt(oldBed.bedType || 1),
        bedStatus: BITMASK_AVAILABLE,
        bedActive: 1,
        bedObservation: `Leito liberado por transferência em ${currentTimestamp}`,
        bedRegisterDate: currentTimestamp
    };

    const newBedUpdatePayload = {
        bedID: parseInt(newBed.bedID || newBed.id),
        bedRoomID: parseInt(newBed.bedRoomID),
        bedNumber: newBed.bedNumber || newBed.numero,
        bedType: parseInt(newBed.bedType || 1),
        bedStatus: BITMASK_BUSY,
        bedActive: 1,
        bedObservation: `Paciente: ${patient ? patient.patientName : 'N/A'} | Transferido em ${currentTimestamp} | Motivo: ${reason}`,
        bedRegisterDate: currentTimestamp
    };

    try {
        const [transferRes, oldBedRes, newBedRes] = await Promise.all([
            fetch('/api/register_transfer', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(transferPayload)
            }),
            fetch('/api/update_bed', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(oldBedUpdatePayload)
            }),
            fetch('/api/update_bed', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newBedUpdatePayload)
            })
        ]);

        const transferText = await transferRes.text();

        if (transferRes.ok && oldBedRes.ok && newBedRes.ok) {
            showToast('Transferência de leito realizada com sucesso!');
            loadTransferTable();
        } else {
            showToast(transferText || 'Erro ao registrar transferência.', 'danger');
        }
    } catch (error) {
        console.error('Erro de rede na transferência:', error);
        showToast('Erro de conexão com o servidor.', 'danger');
    }
}

async function handleInternacaoSubmit(event) {
    event.preventDefault();

    const patientId = document.getElementById('admission-patient').value;
    const bedId = document.getElementById('admission-bed-id').value;
    const notes = document.getElementById('admission-notes').value.trim();

    if (!patientId || !bedId || !appHospitalState.selectedBed) {
        showToast('Selecione a paciente e clique em um leito disponível no organograma.', 'danger');
        return;
    }

    const targetBed = appHospitalState.selectedBed;
    const targetPatient = appHospitalState.pacientes.find(p => String(p.patientID) === String(patientId));

    if (!targetBed || !targetPatient) {
        showToast('Paciente ou leito selecionado inválido.', 'danger');
        return;
    }

    const currentTimestamp = new Date().toISOString();

    const admissionPayload = {
        admissionPatientID: parseInt(patientId),
        admissionInDate: currentTimestamp,
        admissionOutDate: null,
        admissionStatus: ADMISSION_STATUS.OPEN,
        admissionObservation: `Leito: ${targetBed.bedNumber || targetBed.numero} | ${notes}`
    };

    const bedUpdatePayload = {
        bedID: parseInt(targetBed.bedID || targetBed.id),
        bedRoomID: parseInt(targetBed.bedRoomID),
        bedNumber: targetBed.bedNumber || targetBed.numero,
        bedType: parseInt(targetBed.bedType || 1),
        bedStatus: BITMASK_BUSY,
        bedActive: 1,
        bedObservation: `Paciente: ${targetPatient.patientName} | Prontuário: ${targetPatient.patientChart || 'N/A'} | ${notes}`,
        bedRegisterDate: currentTimestamp
    };

    try {
        const [admissionRes, bedRes] = await Promise.all([
            fetch('/api/register_admission', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(admissionPayload)
            }),
            fetch('/api/update_bed', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bedUpdatePayload)
            })
        ]);

        const admissionText = await admissionRes.text();
        const bedText = await bedRes.text();

        if (admissionRes.ok && bedRes.ok) {
            showToast('Internação realizada com sucesso! Leito marcado como ocupado.');
            document.getElementById('form-internacao').reset();
            switchTab('lista-internacoes');
        } else {
            showToast(admissionRes.ok ? bedText : admissionText, 'danger');
        }
    } catch (error) {
        console.error('Erro de rede na internação:', error);
        showToast('Erro de conexão com o servidor.', 'danger');
    }
}

function getAdmissionStatusBadge(status) {
    if (status === ADMISSION_STATUS.OPEN) {
        return '<span style="background: #dbeafe; color: #1e40af; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-size: 0.8rem; font-weight: 600;">Aberta</span>';
    } else if (status === ADMISSION_STATUS.CLOSED) {
        return '<span style="background: #fee2e2; color: #991b1b; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-size: 0.8rem; font-weight: 600;">Fechada</span>';
    } else if (status === ADMISSION_STATUS.INACTIVE) {
        return '<span style="background: #f3f4f6; color: #374151; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-size: 0.8rem; font-weight: 600;">Inativa</span>';
    }
    return `<span style="background: #f3f4f6; color: #374151; padding: 0.2rem 0.5rem; border-radius: 0.25rem; font-size: 0.8rem; font-weight: 600;">${status}</span>`;
}

async function loadAdmissions() {
    const tbody = document.getElementById('admissions-table-body');
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #6b7280; padding: 1.5rem;">Carregando internações...</td></tr>`;

    try {
        if (appHospitalState.pacientes.length === 0) {
            const patRes = await fetch('/api/get_patients');
            if (patRes.ok) {
                const patData = await patRes.json();
                appHospitalState.pacientes = Array.isArray(patData) ? patData : [];
            }
        }

        const response = await fetch('/api/get_admissions');
        if (response.ok) {
            const data = await response.json();
            appHospitalState.internacoes = Array.isArray(data) ? data : [];

            if (appHospitalState.internacoes.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhuma internação registrada.</td></tr>`;
                return;
            }

            tbody.innerHTML = appHospitalState.internacoes.map(a => {
                const patient = appHospitalState.pacientes.find(p => String(p.patientID) === String(a.admissionPatientID));
                const patientDisplayName = patient ? patient.patientName : `ID: ${a.admissionPatientID}`;

                return `
                    <tr>
                        <td>${a.admissionID || '-'}</td>
                        <td style="font-weight: 600;">${patientDisplayName}</td>
                        <td>${a.admissionInDate ? a.admissionInDate.replace('T', ' ').substring(0, 19) : '-'}</td>
                        <td>${a.admissionOutDate ? a.admissionOutDate.replace('T', ' ').substring(0, 19) : 'Em aberto'}</td>
                        <td>${getAdmissionStatusBadge(a.admissionStatus)}</td>
                        <td>${a.admissionObservation || '-'}</td>
                    </tr>
                `;
            }).join('');
        } else {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro ao carregar internações.</td></tr>`;
        }
    } catch (error) {
        console.error('Erro de rede:', error);
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #ef4444; padding: 1.5rem;">Erro de conexão com o servidor.</td></tr>`;
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type === 'danger' ? 'danger' : ''}`;
    toast.classList.remove('hidden');

    setTimeout(() => {
        toast.classList.add('hidden');
    }, 4000);
}

// --- Bed Map & Management Dashboard Functions ---

async function loadBedMap() {
    const container = document.getElementById('bed-map-container');
    container.innerHTML = '<div style="color: #94a3b8; padding: 1rem; text-align: center;">$ loading hospital_bed_map...</div>';

    try {
        const [resSetores, resQuartos, resLeitos] = await Promise.all([
            fetch('/api/get_sectors'),
            fetch('/api/get_rooms'),
            fetch('/api/get_beds')
        ]);

        if (resSetores.ok) appHospitalState.setores = await resSetores.json();
        if (resQuartos.ok) appHospitalState.quartos = await resQuartos.json();
        if (resLeitos.ok) appHospitalState.leitos = await resLeitos.json();

        updateBedDashboardMetrics();
        renderReadOnlyBedMap();

    } catch (error) {
        console.error('Erro ao carregar mapa de leitos:', error);
        container.innerHTML = '<div style="color: #f87171; padding: 1rem; text-align: center;">[ERROR] Falha ao carregar dados do mapa.</div>';
    }
}

function updateBedDashboardMetrics() {
    const beds = appHospitalState.leitos;
    let total = 0;
    let ocupados = 0;
    let disponiveis = 0;
    let reservados = 0;
    let indisponiveis = 0; // Higienização + Manutenção + Bloqueados

    beds.forEach(bed => {
        const isActive = (bed.bedActive !== 0 && bed.bed_active !== 0);
        if (!isActive) return;

        total++;
        const status = Number(bed.bedStatus !== undefined ? bed.bedStatus : (bed.status !== undefined ? bed.status : 1));

        if ((status & (1 << 1)) !== 0) { // Busy / Ocupado
            ocupados++;
        } else if ((status & (1 << 0)) !== 0) { // Available / Disponível
            disponiveis++;
        } else if ((status & (1 << 2)) !== 0) { // Reserved / Reservado
            reservados++;
        } else if (
            (status & (1 << 3)) !== 0 || // Higienização
            (status & (1 << 4)) !== 0 || // Manutenção
            (status & (1 << 5)) !== 0    // Bloqueado
        ) {
            indisponiveis++;
        }
    });

    document.getElementById('dash-total-leitos').textContent = total;
    document.getElementById('dash-ocupados').textContent = ocupados;
    document.getElementById('dash-disponiveis').textContent = disponiveis;
    document.getElementById('dash-reservados').textContent = reservados;
    document.getElementById('dash-indisponiveis').textContent = indisponiveis;
}

function renderReadOnlyBedMap() {
    const container = document.getElementById('bed-map-container');
    const activeSectors = appHospitalState.setores.filter(s => s.sectorActive !== false && s.sectorActive !== 0);

    if (activeSectors.length === 0) {
        container.innerHTML = '<div style="color: #94a3b8; padding: 1rem; text-align: center;">Nenhum setor ativo encontrado.</div>';
        return;
    }

    container.innerHTML = activeSectors.map((sector, sIdx) => {
        const sectorId = sector.sectorID || sector.id;
        const sectorName = sector.sectorName || sector.nome;
        const activeRooms = appHospitalState.quartos.filter(q => 
            String(q.roomSectorID) === String(sectorId) && 
            (q.roomActive !== false && q.roomActive !== 0)
        );

        const isLastSector = sIdx === activeSectors.length - 1;
        const sectorPrefix = isLastSector ? '└── ' : '├── ';

        return `
            <div class="tree-sector expanded" id="map-sector-${sectorId}">
                <div class="tree-sector-header" onclick="toggleTreeSection('map-sector-${sectorId}')">
                    <span>${sectorPrefix}📁 Setor: ${sectorName}</span>
                </div>
                <div class="tree-sector-body">
                    ${activeRooms.length === 0 ? '<div style="color: #64748b; padding-left: 2rem;">└── (Nenhum quarto ativo)</div>' : 
                        activeRooms.map((room, rIdx) => {
                            const roomId = room.roomID || room.id;
                            const roomName = room.roomIdentification || room.numero;
                            const roomType = room.roomType || room.tipo || 'Padrão';
                            const roomBeds = appHospitalState.leitos.filter(l => 
                                String(l.bedRoomID) === String(roomId) && 
                                (l.bedActive !== 0 && l.bed_active !== 0)
                            );

                            const isLastRoom = rIdx === activeRooms.length - 1;
                            const roomPrefix = isLastRoom ? '└── ' : '├── ';

                            return `
                                <div class="tree-room expanded" id="map-room-${roomId}">
                                    <div class="tree-room-header" onclick="toggleTreeSection('map-room-${roomId}')">
                                        <span>${roomPrefix}🚪 Quarto ${roomName} [${roomType}]</span>
                                    </div>
                                    <div class="tree-room-body">
                                        ${roomBeds.length === 0 ? '<div style="color: #64748b; padding-left: 2rem;">└── (Nenhum leito)</div>' :
                                            roomBeds.map((bed, bIdx) => {
                                                const bedNumber = bed.bedNumber || bed.numero;
                                                const rawStatus = Number(bed.bedStatus !== undefined ? bed.bedStatus : (bed.status !== undefined ? bed.status : 1));
                                                const obs = bed.bedObservation || bed.observacao ? ` — Obs: ${bed.bedObservation || bed.observacao}` : '';

                                                let statusLabel = 'DESCONHECIDO';
                                                let statusColor = '#94a3b8';
                                                if ((rawStatus & (1 << 0)) !== 0) { statusLabel = 'DISPONÍVEL'; statusColor = '#4ade80'; }
                                                else if ((rawStatus & (1 << 1)) !== 0) { statusLabel = 'OCUPADO'; statusColor = '#f87171'; }
                                                else if ((rawStatus & (1 << 2)) !== 0) { statusLabel = 'RESERVADO'; statusColor = '#60a5fa'; }
                                                else if ((rawStatus & (1 << 3)) !== 0) { statusLabel = 'HIGIENIZAÇÃO'; statusColor = '#c084fc'; }
                                                else if ((rawStatus & (1 << 4)) !== 0) { statusLabel = 'MANUTENÇÃO'; statusColor = '#facc15'; }
                                                else if ((rawStatus & (1 << 5)) !== 0) { statusLabel = 'BLOQUEADO'; statusColor = '#94a3b8'; }

                                                const isLastBed = bIdx === roomBeds.length - 1;
                                                const bedPrefix = isLastBed ? '└── ' : '├── ';

                                                return `
                                                    <div style="margin-left: 1.5rem; color: #cbd5e1;">
                                                        <span>${bedPrefix}🛏️ Leito ${bedNumber} 
                                                            <strong style="color: ${statusColor};">[${statusLabel}]</strong>
                                                            <span style="font-size: 0.75rem; color: #94a3b8;">${obs}</span>
                                                        </span>
                                                    </div>
                                                `;
                                            }).join('')
                                        }
                                    </div>
                                </div>
                            `;
                        }).join('')
                    }
                </div>
            </div>
        `;
    }).join('');
}
// --- Hospital Indicators Functions ---

async function loadHospitalIndicators() {
    try {
        const [resBeds, resRooms, resSectors, resAdmissions, resTransfers, resDischarges] = await Promise.all([
            fetch('/api/get_beds'),
            fetch('/api/get_rooms'),
            fetch('/api/get_sectors'),
            fetch('/api/get_admissions'),
            fetch('/api/get_transfers'),
            fetch('/api/get_discharges')
        ]);

        const beds = resBeds.ok ? await resBeds.json() : [];
        const rooms = resRooms.ok ? await resRooms.json() : [];
        const sectors = resSectors.ok ? await resSectors.json() : [];
        const admissions = resAdmissions.ok ? await resAdmissions.json() : [];
        const transfers = resTransfers.ok ? await resTransfers.json() : [];
        const discharges = resDischarges.ok ? await resDischarges.json() : [];

        const activeBeds = Array.isArray(beds) ? beds.filter(b => b.bedActive !== 0 && b.bed_active !== 0) : [];
        
        let totalLeitos = activeBeds.length;
        let leitosOcupados = 0;
        let leitosDisponiveis = 0;
        let leitosBloqueados = 0;

        activeBeds.forEach(b => {
            const status = Number(b.bedStatus !== undefined ? b.bedStatus : (b.status !== undefined ? b.status : 1));
            if ((status & (1 << 1)) !== 0) leitosOcupados++;
            if ((status & (1 << 0)) !== 0) leitosDisponiveis++;
            if ((status & (1 << 5)) !== 0 || (status & (1 << 4)) !== 0) leitosBloqueados++;
        });

        // Fórmula da Taxa de Ocupação: (Leitos ocupados / Leitos disponíveis para uso) × 100
        // Consideramos "Leitos disponíveis para uso" como o total de leitos ativos menos os bloqueados/manutenção estritos, ou total ativo geral.
        const leitosDisponiveisParaUso = totalLeitos - leitosBloqueados;
        const taxaOcupacao = leitosDisponiveisParaUso > 0 ? ((leitosOcupados / leitosDisponiveisParaUso) * 100).toFixed(1) : 0;

        // Quantidade de internações, altas e transferências
        const totalInternacoes = Array.isArray(admissions) ? admissions.length : 0;
        const totalAltas = Array.isArray(discharges) ? discharges.length : 0;
        const totalTransferencias = Array.isArray(transfers) ? transfers.length : 0;

        // Tempo médio de permanência (para internações fechadas / com data de saída)
        let totalDays = 0;
        let closedCount = 0;
        if (Array.isArray(admissions)) {
            admissions.forEach(a => {
                if (a.admissionInDate && a.admissionOutDate) {
                    const inDate = new Date(a.admissionInDate);
                    const outDate = new Date(a.admissionOutDate);
                    const diffTime = Math.abs(outDate - inDate);
                    const diffDays = diffTime / (1000 * 60 * 60 * 24);
                    totalDays += diffDays;
                    closedCount++;
                }
            });
        }
        const tempoMedioPermanencia = closedCount > 0 ? (totalDays / closedCount).toFixed(1) : 0;

        // Atualização dos Cards no DOM
        document.getElementById('ind-total-leitos').textContent = totalLeitos;
        document.getElementById('ind-leitos-ocupados').textContent = leitosOcupados;
        document.getElementById('ind-leitos-disponiveis').textContent = leitosDisponiveis;
        document.getElementById('ind-leitos-bloqueados').textContent = leitosBloqueados;
        document.getElementById('ind-taxa-ocupacao').textContent = `${taxaOcupacao}%`;
        document.getElementById('ind-total-internacoes').textContent = totalInternacoes;
        document.getElementById('ind-total-altas').textContent = totalAltas;
        document.getElementById('ind-total-transferencias').textContent = totalTransferencias;
        document.getElementById('ind-tempo-permanencia').textContent = `${tempoMedioPermanencia} dias`;

        // Cálculo por Setor
        const activeSectors = Array.isArray(sectors) ? sectors.filter(s => s.sectorActive !== false && s.sectorActive !== 0) : [];
        const sectorTbody = document.getElementById('indicators-sector-tbody');

        if (activeSectors.length === 0) {
            sectorTbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #6b7280; padding: 1.5rem;">Nenhum setor cadastrado.</td></tr>`;
            return;
        }

        sectorTbody.innerHTML = activeSectors.map(sector => {
            const sectorId = sector.sectorID || sector.id;
            const sectorName = sector.sectorName || sector.nome;

            // Quartos do setor
            const sectorRoomIDs = new Set(
                rooms.filter(r => String(r.roomSectorID) === String(sectorId) && (r.roomActive !== false && r.roomActive !== 0))
                     .map(r => String(r.roomID || r.id))
            );

            // Leitos do setor
            const sectorBeds = activeBeds.filter(b => sectorRoomIDs.has(String(b.bedRoomID)));
            const sTotal = sectorBeds.length;
            let sOcupados = 0;
            let sDisponiveis = 0;

            sectorBeds.forEach(b => {
                const status = Number(b.bedStatus !== undefined ? b.bedStatus : (b.status !== undefined ? b.status : 1));
                if ((status & (1 << 1)) !== 0) sOcupados++;
                if ((status & (1 << 0)) !== 0) sDisponiveis++;
            });

            const sTaxa = sTotal > 0 ? ((sOcupados / sTotal) * 100).toFixed(1) : 0;

            return `
                <tr>
                    <td style="font-weight: 600;">${sectorName}</td>
                    <td>${sTotal}</td>
                    <td style="color: #b91c1c; font-weight: 600;">${sOcupados}</td>
                    <td style="color: #15803d; font-weight: 600;">${sDisponiveis}</td>
                    <td><strong style="color: #1d4ed8;">${sTaxa}%</strong></td>
                </tr>
            `;
        }).join('');

    } catch (error) {
        console.error('Erro ao calcular indicadores hospitalares:', error);
        showToast('Erro ao carregar indicadores de desempenho.', 'danger');
    }
}
