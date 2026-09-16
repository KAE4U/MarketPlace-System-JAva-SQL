package br.unip.erp.view;

import br.unip.erp.controller.VendaController;
import br.unip.erp.model.Venda;
import br.unip.erp.model.VendaPagamento;
import br.unip.erp.model.VendaProduto;
import br.unip.erp.util.Mascaras;
import br.unip.erp.util.Tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.awt.Window;

/** Dialogo modal com os detalhes de uma venda (cabecalho + itens + pagamentos). */
public class DetalheVendaDialog extends JDialog {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DetalheVendaDialog(Component parente, int vendaCodigo) {
        super(janelaDe(parente), "Detalhes da Venda", ModalityType.APPLICATION_MODAL);
        Venda v = new VendaController().buscar(vendaCodigo);
        montar(v);
        setSize(560, 480);
        setLocationRelativeTo(parente);
    }

    private static Window janelaDe(Component c) {
        return c == null ? null : javax.swing.SwingUtilities.getWindowAncestor(c);
    }

    private void montar(Venda v) {
        setLayout(new BorderLayout(10, 10));
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (v == null) {
            root.add(new JLabel("Venda não encontrada."), BorderLayout.CENTER);
            add(root);
            return;
        }

        // Cabecalho
        JPanel cab = new JPanel(new GridLayout(0, 2, 6, 4));
        cab.setBorder(BorderFactory.createTitledBorder("Venda #" + v.getCodigo()));
        cab.add(rotulo("Data:"));
        cab.add(new JLabel(v.getData() != null ? v.getData().format(DATA) : "-"));
        cab.add(rotulo("Cliente:"));
        cab.add(new JLabel(v.getCliente().getPessoa().getNome()));
        cab.add(rotulo("Usuário:"));
        cab.add(new JLabel(v.getUsuario().getNome() != null ? v.getUsuario().getNome() : "-"));
        cab.add(rotulo("Subtotal:"));
        cab.add(new JLabel(Mascaras.moeda(v.getValor())));
        cab.add(rotulo("Desconto:"));
        cab.add(new JLabel(Mascaras.moeda(v.getDesconto())));
        cab.add(rotulo("Total:"));
        JLabel total = new JLabel(Mascaras.moeda(v.getTotal()));
        total.setFont(new Font("SansSerif", Font.BOLD, 14));
        total.setForeground(Tema.SUCESSO);
        cab.add(total);

        // Itens
        DefaultTableModel mItens = new DefaultTableModel(
                new Object[]{"Produto", "Qtde", "Preço", "Desc.", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (VendaProduto it : v.getItens()) {
            mItens.addRow(new Object[]{
                    it.getProduto().getNome(), it.getQtde(),
                    Mascaras.moeda(it.getPreco()), Mascaras.moeda(it.getDesconto()),
                    Mascaras.moeda(it.getTotal())});
        }
        JTable tItens = new JTable(mItens);
        Tema.estilizarTabela(tItens);
        JScrollPane spItens = new JScrollPane(tItens);
        spItens.setBorder(BorderFactory.createTitledBorder("Produtos"));

        // Pagamentos
        DefaultTableModel mPg = new DefaultTableModel(
                new Object[]{"Forma de Pagamento", "Valor"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (VendaPagamento pg : v.getPagamentos()) {
            mPg.addRow(new Object[]{pg.getFormaPagamento().getNome(), Mascaras.moeda(pg.getValor())});
        }
        JTable tPg = new JTable(mPg);
        Tema.estilizarTabela(tPg);
        JScrollPane spPg = new JScrollPane(tPg);
        spPg.setBorder(BorderFactory.createTitledBorder("Formas de Pagamento"));

        JPanel tabelas = new JPanel(new GridLayout(2, 1, 0, 8));
        tabelas.add(spItens);
        tabelas.add(spPg);

        JButton btFechar = new JButton("Fechar");
        Tema.botaoSecundario(btFechar);
        btFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(btFechar, BorderLayout.EAST);

        root.add(cab, BorderLayout.NORTH);
        root.add(tabelas, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }

    private JLabel rotulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }
}
