let stompClient = null;

function connect() {
    // Connects to your SockJS endpoint registered in WebSocketConfig (/ws)
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    // Disable debug logs in console if desired
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        console.log('Conectado ao STOMP: ' + frame);
        
        // Update UI status badge
        const statusBadge = document.getElementById('connectionStatus');
        statusBadge.textContent = 'Conectado em Tempo Real';
        statusBadge.className = 'status-badge status-connected';

        // Subscribe to the topic defined in your InternacaoPollingService
        stompClient.subscribe('/topic/cmi-internacoes', function (message) {
            const internacoes = JSON.parse(message.body);
            renderTable(internacoes);
        });
    }, function (error) {
        console.error('Erro de conexão STOMP:', error);
        
        const statusBadge = document.getElementById('connectionStatus');
        statusBadge.textContent = 'Desconectado (Reconectando...)';
        statusBadge.className = 'status-badge status-disconnected';

        // Attempt to reconnect after 5 seconds
        setTimeout(connect, 5000);
    });
}

function renderTable(rows) {
    const tbody = document.getElementById('internacoesTableBody');
    tbody.innerHTML = ''; // Clear existing rows

    if (!rows || rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="no-data">Nenhuma internação ativa no momento.</td></tr>';
        return;
    }

    rows.forEach(item => {
        const tr = document.createElement('tr');
        
        // Map properties according to your Java Internacao model fields
        tr.innerHTML = `
            <td><strong>${item.id || item.internacaoID || 'N/A'}</strong></td>
            <td>${item.pacienteName || item.patientName || 'N/A'}</td>
            <td>${item.leito || item.roomNumber || 'Não alocado'}</td>
            <td>${item.medico || item.doctorName || 'N/A'}</td>
            <td>${item.dataInternacao || item.admissionDate || 'N/A'}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Initialize connection on page load
connect();
