package br.com.nircmi.service;

import br.com.nircmi.model.Admission;
import br.com.nircmi.repository.AdmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Equivalente ao startPolling()/pollOnce() de server/index.js.
 * Publica atualizacoes periodicas no topico STOMP /topic/cmi-internacoes
 * (substitui o canal Socket.IO 'cmi_internacoes' do projeto Node original).
 */
@Service
public class AdmissionPollingService {

    private static final Logger log = LoggerFactory.getLogger(AdmissionPollingService.class);

    private final AdmissionRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public AdmissionPollingService(AdmissionRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelayString = "${nircmi.refresh-minutes:1}" , timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public void pollOnce() {
        try {
            List<Admission> rows = repository.findCmiAdmissions();
            messagingTemplate.convertAndSend("/topic/cmi-admission", rows);
        } catch (Exception e) {
            log.error("Erro no polling de internacoes CMI", e);
        }
    }
}
