package br.unip.erp.view;

import br.unip.erp.controller.CompraController;
import br.unip.erp.model.Compra;
import br.unip.erp.model.CompraProduto;
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
import java.awt.Window;
import java.time.format.DateTimeFormatter;

/** Dialogo modal com os detalhes de uma compra (cabecalho + itens). */
public class DetalheCompraDialog extends JDialog {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DetalheCompraDialog(Component parente, int compraCodigo) {
        super(janelaDe(parente), "Detalhes da Compra", ModalityType.APPLICATION_MODAL);
        Compra c = new CompraController().buscar(compraCodigo);
        montar(c);
        setSize(560, 440);
        setLocationRelativeTo(parente);
    }

    private static Window janelaDe(Component c) {
        return c == null ? null : javax.swing.SwingUtilities.getWindowAncestor(c);
    }

    private void montar(Compra c) {
        setLayout(new BorderLayout(10, 10));
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        if (c == null) {
            root.add(new JLabel("Compra não encontrada."), BorderLayout.CENTER);
            add(root);
            return;
        }

        JPanel cab = new JPanel(new GridLayout(0, 2, 6, 4));
        cab.setBorder(BorderFactory.createTitledBorder("Compra #" + c.getCodigo()));
        cab.add(rotulo("Emissão:"));
        cab.add(new JLabel(c.getEmissao() != null ? c.getEmissao().format(DATA) : "-"));
        cab.add(rotulo("Entrada:"));
        cab.add(new JLabel(c.getDataEntrada() != null ? c.getDataEntrada().format(DATA) : "-"));
        cab.add(rotulo("Fornecedor:"));
        cab.add(new JLabel(c.getFornecedor().getPessoa().getNome()));
        cab.add(rotulo("Usuário:"));
        cab.add(new JLabel(c.getUsuario().getNome() != null ? c.getUsuario().getNome() : "-"));
        cab.add(rotulo("Subtotal:"));
        cab.add(new JLabel(Mascaras.moeda(c.getValor())));
        cab.add(rotulo("Desconto:"));
        cab.add(new JLabel(Mascaras.moeda(c.getDesconto())));
        cab.add(rotulo("Total:"));
        JLabel total = new JLabel(Mascaras.moeda(c.getTotal()));
        total.setFont(new Font("SansSerif", Font.BOLD, 14));
        total.setForeground(Tema.SUCESSO);
        cab.add(total);

        DefaultTableModel mItens = new DefaultTableModel(
                new Object[]{"Produto", "Qtde", "Preço", "Desc.", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c2) { return false; }
        };
        for (CompraProduto it : c.getItens()) {
            mItens.addRow(new Object[]{
                    it.getProduto().getNome(), it.getQtde(),
                    Mascaras.moeda(it.getPreco()), Mascaras.moeda(it.getDesconto()),
                    Mascaras.moeda(it.getTotal())});
        }
        JTable tItens = new JTable(mItens);
        Tema.estilizarTabela(tItens);
        JScrollPane spItens = new JScrollPane(tItens);
        spItens.setBorder(BorderFactory.createTitledBorder("Produtos"));

        JButton btFechar = new JButton("Fechar");
        Tema.botaoSecundario(btFechar);
        btFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(btFechar, BorderLayout.EAST);

        root.add(cab, BorderLayout.NORTH);
        root.add(spItens, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }

    private JLabel rotulo(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }
}
