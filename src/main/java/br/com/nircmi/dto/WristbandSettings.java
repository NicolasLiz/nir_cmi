package br.com.nircmi.dto;

/**
 * Equivalente ao objeto "settings" usado em server/zebra/wristband.js
 */
public class WristbandSettings {
    private double larguraMm = 25;
    private double alturaMm = 160;
    private double margemMm = 2;
    private String fonteTexto = "0";
    private int tamanhoFonte = 25;
    private boolean showSecondaryBarcode = true;

    public double getLarguraMm() { return larguraMm; }
    public void setLarguraMm(double larguraMm) { this.larguraMm = larguraMm; }

    public double getAlturaMm() { return alturaMm; }
    public void setAlturaMm(double alturaMm) { this.alturaMm = alturaMm; }

    public double getMargemMm() { return margemMm; }
    public void setMargemMm(double margemMm) { this.margemMm = margemMm; }

    public String getFonteTexto() { return fonteTexto; }
    public void setFonteTexto(String fonteTexto) { this.fonteTexto = fonteTexto; }

    public int getTamanhoFonte() { return tamanhoFonte; }
    public void setTamanhoFonte(int tamanhoFonte) { this.tamanhoFonte = tamanhoFonte; }

    public boolean isShowSecondaryBarcode() { return showSecondaryBarcode; }
    public void setShowSecondaryBarcode(boolean showSecondaryBarcode) { this.showSecondaryBarcode = showSecondaryBarcode; }
}
