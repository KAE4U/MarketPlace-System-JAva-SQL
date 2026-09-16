package br.unip.erp.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;

/**
 * Centraliza o tema visual da aplicacao (FlatLaf dark) e helpers de estilizacao.
 */
public final class Tema {

    // Paleta de cores (tema escuro)
    public static final Color FUNDO        = new Color(30, 32, 40);
    public static final Color SUPERFICIE   = new Color(43, 46, 58);
    public static final Color PRIMARIA     = new Color(124, 92, 232);   // roxo
    public static final Color PRIMARIA_HOV = new Color(142, 112, 240);
    public static final Color TEXTO        = new Color(230, 232, 238);
    public static final Color TEXTO_SUAVE  = new Color(150, 155, 168);
    public static final Color BORDA        = new Color(60, 64, 78);
    public static final Color SUCESSO      = new Color(70, 180, 120);
    public static final Color PERIGO       = new Color(220, 90, 90);

    public static final Font FONTE_TITULO  = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONTE_LABEL   = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONTE_BOTAO   = new Font("SansSerif", Font.BOLD, 13);

    private Tema() { }

    /** Instala o FlatLaf dark e ajusta defaults globais. Chame antes de criar telas. */
    public static void instalar() {
        try {
            // Cantos arredondados e detalhes de acento antes de instalar o LaF
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 14);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.thumbArc", 12);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Component.focusColor", PRIMARIA);
            UIManager.put("Component.accentColor", PRIMARIA);

            FlatDarkLaf.setup();

            // Ajustes finos apos instalar
            UIManager.put("TableHeader.background", SUPERFICIE);
            UIManager.put("TableHeader.foreground", TEXTO);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
            UIManager.put("Table.rowHeight", 26);
            UIManager.put("TitledBorder.titleColor", TEXTO_SUAVE);
            UIManager.put("MenuBar.background", SUPERFICIE);
            UIManager.put("Menu.font", FONTE_LABEL);
            UIManager.put("defaultFont", FONTE_LABEL);
        } catch (Exception e) {
            System.err.println("Nao foi possivel aplicar o tema FlatLaf: " + e.getMessage());
        }
    }

    /** Estiliza um botao como acao primaria (destaque roxo). */
    public static void botaoPrimario(JButton b) {
        b.setBackground(PRIMARIA);
        b.setForeground(Color.WHITE);
        b.setFont(FONTE_BOTAO);
        b.setFocusPainted(false);
        b.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** Estiliza um botao como acao secundaria (contorno). */
    public static void botaoSecundario(JButton b) {
        b.setFont(FONTE_BOTAO);
        b.setFocusPainted(false);
        b.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** Estiliza um botao de acao destrutiva (vermelho). */
    public static void botaoPerigo(JButton b) {
        b.setBackground(PERIGO);
        b.setForeground(Color.WHITE);
        b.setFont(FONTE_BOTAO);
        b.setFocusPainted(false);
        b.putClientProperty("JButton.buttonType", "roundRect");
    }

    /** Aplica visual moderno a uma tabela (altura de linha, grade, selecao). */
    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(28);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(BORDA);
        tabela.setSelectionBackground(PRIMARIA);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);
        JTableHeader header = tabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    /** Marca um campo com texto de placeholder (dica interna). */
    public static void placeholder(JComponent campo, String texto) {
        campo.putClientProperty("JTextField.placeholderText", texto);
    }

    /** Aplica o tema ESCURO e atualiza todas as janelas abertas. */
    public static void aplicarEscuro(java.awt.Component origem) {
        try {
            FlatDarkLaf.setup();
            atualizarJanelas();
        } catch (Exception e) {
            System.err.println("Falha ao aplicar tema escuro: " + e.getMessage());
        }
    }

    /** Aplica o tema CLARO e atualiza todas as janelas abertas. */
    public static void aplicarClaro(java.awt.Component origem) {
        try {
            FlatLightLaf.setup();
            atualizarJanelas();
        } catch (Exception e) {
            System.err.println("Falha ao aplicar tema claro: " + e.getMessage());
        }
    }

    /** Reaplica o Look & Feel atual em todas as janelas abertas (troca de tema em runtime). */
    private static void atualizarJanelas() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
    }
}
