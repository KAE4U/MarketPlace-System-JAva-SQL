package br.unip.erp.view;

import br.unip.erp.controller.CompraController;
import br.unip.erp.model.Compra;
import br.unip.erp.util.Mascaras;
import br.unip.erp.util.Permissao;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Historico de compras: lista as compras realizadas e permite excluir (somente admin). */
public class HistoricoCompraView extends JInternalFrame {

    private final CompraController controller = new CompraController();
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Código", "Emissão", "Fornecedor", "Total"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable tabela = new JTable(tableModel);

    public HistoricoCompraView() {
        super("Histórico de Compras", true, true, true, true);
        setSize(680, 460);
        montar();
        carregarTabela();
    }

    private void montar() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.estilizarTabela(tabela);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btAtualizar = new JButton("Atualizar");
        JButton btDetalhes = new JButton("Detalhes");
        JButton btExcluir = new JButton("Excluir");
        Tema.botaoSecundario(btAtualizar);
        Tema.botaoPrimario(btDetalhes);
        Tema.botaoPerigo(btExcluir);
        btAtualizar.addActionListener(e -> carregarTabela());
        btDetalhes.addActionListener(e -> verDetalhes());
        btExcluir.addActionListener(e -> excluir());
        botoes.add(btAtualizar);
        botoes.add(btDetalhes);
        botoes.add(btExcluir);

        // Duplo-clique na linha abre os detalhes
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalhes();
                }
            }
        });

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void verDetalhes() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma compra para ver os detalhes.");
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        new DetalheCompraDialog(this, codigo).setVisible(true);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Compra> lista = controller.listar();
        for (Compra c : lista) {
            tableModel.addRow(new Object[]{
                    c.getCodigo(),
                    c.getEmissao() != null ? c.getEmissao().format(DATA) : "",
                    c.getFornecedor().getPessoa().getNome(),
                    Mascaras.moeda(c.getTotal())});
        }
    }

    private void excluir() {
        if (!Permissao.podeExcluir(this)) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma compra para excluir.");
            return;
        }
        int codigo = (int) tableModel.getValueAt(row, 0);
        int op = JOptionPane.showConfirmDialog(this,
                "Excluir a compra #" + codigo + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(codigo);
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
