package br.unip.erp.util;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitario de mascaras e formatacao para campos de texto:
 * CPF/CNPJ, telefone e valores monetarios (R$).
 */
public final class Mascaras {

    @SuppressWarnings("deprecation")
    private static final Locale BR = new Locale("pt", "BR");

    private Mascaras() { }

    /** Aplica mascara dinamica de CPF (000.000.000-00) ou CNPJ (00.000.000/0000-00). */
    public static void cpfCnpj(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                aplicar(fb, offset, length, text, 14, Mascaras::formatarCpfCnpj);
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }
        });
    }

    /** Aplica mascara dinamica de data: dd/MM/aaaa. */
    public static void data(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                aplicar(fb, offset, length, text, 8, Mascaras::formatarData);
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }
        });
    }

    /** Aplica mascara dinamica de telefone: (00) 0000-0000 ou (00) 00000-0000. */
    public static void telefone(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                aplicar(fb, offset, length, text, 11, Mascaras::formatarTelefone);
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                    throws BadLocationException {
                replace(fb, offset, 0, text, attrs);
            }
        });
    }

    // ---- nucleo comum das mascaras baseadas em digitos ----
    private interface Formatador {
        String format(String digitos);
    }

    private static void aplicar(DocumentFilter.FilterBypass fb, int offset, int length,
                                String text, int maxDigitos, Formatador formatador)
            throws BadLocationException {
        String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String antesDigitos = soDigitos(atual.substring(0, offset));
        String novoTrecho = text == null ? "" : soDigitos(text);
        String depoisDigitos = soDigitos(atual.substring(offset + length));

        String digitos = antesDigitos + novoTrecho + depoisDigitos;
        if (digitos.length() > maxDigitos) {
            digitos = digitos.substring(0, maxDigitos);
        }
        String formatado = formatador.format(digitos);
        fb.replace(0, fb.getDocument().getLength(), formatado, null);
    }

    private static String soDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    private static String formatarCpfCnpj(String d) {
        if (d.length() <= 11) {
            // CPF: 000.000.000-00
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < d.length(); i++) {
                if (i == 3 || i == 6) sb.append('.');
                if (i == 9) sb.append('-');
                sb.append(d.charAt(i));
            }
            return sb.toString();
        }
        // CNPJ: 00.000.000/0000-00
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length(); i++) {
            if (i == 2 || i == 5) sb.append('.');
            if (i == 8) sb.append('/');
            if (i == 12) sb.append('-');
            sb.append(d.charAt(i));
        }
        return sb.toString();
    }

    private static String formatarData(String d) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length(); i++) {
            if (i == 2 || i == 4) sb.append('/');
            sb.append(d.charAt(i));
        }
        return sb.toString();
    }

    private static String formatarTelefone(String d) {
        StringBuilder sb = new StringBuilder();
        int n = d.length();
        for (int i = 0; i < n; i++) {
            if (i == 0) sb.append('(');
            if (i == 2) sb.append(") ");
            // hifen: 4 digitos finais
            if ((n <= 10 && i == 6) || (n > 10 && i == 7)) sb.append('-');
            sb.append(d.charAt(i));
        }
        return sb.toString();
    }

    // ---- moeda ----

    /** Formata um valor como moeda brasileira: R$ 1.234,56 */
    public static String moeda(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(BR);
        return nf.format(valor);
    }

    /** Formata um double como moeda brasileira. */
    public static String moeda(double valor) {
        return moeda(BigDecimal.valueOf(valor));
    }

    /**
     * Converte um texto (com ou sem simbolos R$, pontos e virgula) em BigDecimal.
     * Ex.: "R$ 1.234,56" -> 1234.56 ; "9,90" -> 9.90 ; "10" -> 10
     */
    public static BigDecimal valorDe(String texto) {
        if (texto == null || texto.isBlank()) {
            return BigDecimal.ZERO;
        }
        String limpo = texto.replaceAll("[^0-9,.-]", "").trim();
        // remove separador de milhar (.) e usa . como decimal a partir da virgula
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        if (limpo.isBlank() || limpo.equals("-") || limpo.equals(".")) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(limpo);
    }
}
