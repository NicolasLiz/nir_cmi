package br.com.nircmi.zebra;

import br.com.nircmi.dto.PacienteWristbandRequest;
import br.com.nircmi.dto.WristbandSettings;
import org.springframework.stereotype.Component;

/**
 * Porte direto de server/zebra/wristband.js (gerador de ZPL para pulseira hospitalar Zebra).
 */
@Component
public class WristbandLabelBuilder {

    public int mmToDots(double mm, int dpi) {
        return Math.round((float) ((mm / 25.4) * dpi));
    }

    public String build(PacienteWristbandRequest paciente, WristbandSettings settingsIn) {
        WristbandSettings settings = settingsIn != null ? settingsIn : new WristbandSettings();

        int widthDots = mmToDots(settings.getLarguraMm(), 203);
        int heightDots = mmToDots(settings.getAlturaMm(), 203);
        int m = mmToDots(settings.getMargemMm(), 203);
        int fs = settings.getTamanhoFonte();
        int fsS = Math.max(fs - 5, 12);
        String fonteTexto = settings.getFonteTexto();

        String maeRow = (paciente.getNomeMae() != null && !paciente.getNomeMae().isBlank())
                ? String.format("^FO%d,%d^A%s,N,%d,%d^FDMae: %s^FS%n", m, m + 20, fonteTexto, fsS, fsS, sanitize(paciente.getNomeMae()))
                : "";

        String prevAltaRow = (paciente.getPrevAlta() != null && !paciente.getPrevAlta().isBlank())
                ? String.format("^FO%d,%d^A%s,N,%d,%d^FDPrev Alta: %s^FS%n", m, m + 140, fonteTexto, fsS, fsS, sanitize(paciente.getPrevAlta()))
                : "";

        String secondaryBarcode = settings.isShowSecondaryBarcode()
                ? String.format("^FO%d,%d^BY2,3,40^BCN,40,N,N,N^FD%s^FS", m, m + 240, paciente.getCodigoPaciente())
                : "";

        return "^XA\n" +
                "^PW" + widthDots + "\n" +
                "^LL" + heightDots + "\n" +
                "^LH0,0\n\n" +
                String.format("^FO%d,%d^A%s,N,%d,%d^FD%s^FS%n", m, m, fonteTexto, fs, fs, sanitize(paciente.getNome())) +
                maeRow +
                String.format("^FO%d,%d^A%s,N,%d,%d^FDPac: %s  Atend: %s^FS%n", m, m + 50, fonteTexto, fsS, fsS, paciente.getCodigoPaciente(), paciente.getCodigoAtendimento()) +
                String.format("^FO%d,%d^A%s,N,%d,%d^FDUnid: %s  Leito: %s^FS%n", m, m + 80, fonteTexto, fsS, fsS, sanitize(paciente.getUnidade()), sanitize(paciente.getLeito())) +
                prevAltaRow + "\n" +
                String.format("^FO%d,%d^BY2,3,60^BCN,60,Y,N,N^FD%s^FS%n", m, m + 165, paciente.getCodigoBarras()) +
                secondaryBarcode + "\n" +
                "^XZ";
    }

    private String sanitize(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\^~]", " ");
    }
}
