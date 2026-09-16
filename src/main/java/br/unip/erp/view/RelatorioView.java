package br.unip.erp.view;

import br.unip.erp.controller.RelatorioController;
import br.unip.erp.model.RelatorioLinha;
import br.unip.erp.util.Mascaras;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Tela de relatorios: vendas por periodo e produtos mais vendidos. */
public class RelatorioView extends JInternalFrame {

    private final RelatorioController controller = new RelatorioController();
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Aba 1 - vendas por periodo
    private final JTextField txtInicio = new JTextField(10);
    private final JTextField txtFim = new JTextField(10);
    private final DefaultTableModel modelVendas =
            new DefaultTableModel(new Object[]{"Data", "Qtde de Vendas", "Total"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaVendas = new JTable(modelVendas);
    private final JLabel lblTotalGeral = new JLabel("Total do período: R$ 0,00");

    // Aba 2 - produtos mais vendidos
    private final DefaultTableModel modelProdutos =
            new DefaultTableModel(new Object[]{"#", "Produto", "Qtde Vendida", "Total"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabelaProdutos = new JTable(modelProdutos);

    public RelatorioView() {
        super("Relatórios", true, true, true, true);
        setSize(640, 500);
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Vendas por Período", abaVendasPorPeriodo());
        abas.addTab("Produtos Mais Vendidos", abaProdutosMaisVendidos());
        setLayout(new BorderLayout());
        add(abas, BorderLayout.CENTER);
    }

    // ---- Aba 1 ----
    private JPanel abaVendasPorPeriodo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filtro.add(new JLabel("Início:"));
        Mascaras.data(txtInicio);
        filtro.add(txtInicio);
        filtro.add(new JLabel("Fim:"));
        Mascaras.data(txtFim);
        filtro.add(txtFim);
        JButton btGerar = new JButton("Gerar");
        Tema.botaoPrimario(btGerar);
        btGerar.addActionListener(e -> gerarVendas());
        filtro.add(btGerar);

        // Sugestao inicial: mes atual
        LocalDate hoje = LocalDate.now();
        txtInicio.setText(hoje.withDayOfMonth(1).format(DATA));
        txtFim.setText(hoje.format(DATA));

        Tema.estilizarTabela(tabelaVendas);
        lblTotalGeral.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalGeral.setForeground(Tema.SUCESSO);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(lblTotalGeral);

        painel.add(filtro, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private void gerarVendas() {
        try {
            LocalDate inicio = LocalDate.parse(txtInicio.getText().trim(), DATA);
            LocalDate fim = LocalDate.parse(txtFim.getText().trim(), DATA);
            List<RelatorioLinha> linhas = controller.vendasPorPeriodo(inicio, fim);
            modelVendas.setRowCount(0);
            BigDecimal totalGeral = BigDecimal.ZERO;
            for (RelatorioLinha l : linhas) {
                modelVendas.addRow(new Object[]{
                        l.getRotulo(),
                        l.getQuantidade().stripTrailingZeros().toPlainString(),
                        Mascaras.moeda(l.getValor())});
                totalGeral = totalGeral.add(l.getValor());
            }
            lblTotalGeral.setText("Total do período: " + Mascaras.moeda(totalGeral));
            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada no período.");
            }
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/aaaa.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---- Aba 2 ----
    private JPanel abaProdutosMaisVendidos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btGerar = new JButton("Atualizar");
        Tema.botaoPrimario(btGerar);
        btGerar.addActionListener(e -> gerarProdutos());
        topo.add(new JLabel("Top 10 produtos mais vendidos:"));
        topo.add(btGerar);

        Tema.estilizarTabela(tabelaProdutos);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        // carrega ao abrir
        gerarProdutos();
        return painel;
    }

    private void gerarProdutos() {
        try {
            List<RelatorioLinha> linhas = controller.produtosMaisVendidos(10);
            modelProdutos.setRowCount(0);
            int pos = 1;
            for (RelatorioLinha l : linhas) {
                modelProdutos.addRow(new Object[]{
                        pos++, l.getRotulo(),
                        l.getQuantidade().stripTrailingZeros().toPlainString(),
                        Mascaras.moeda(l.getValor())});
            }
            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ainda não há vendas registradas.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
