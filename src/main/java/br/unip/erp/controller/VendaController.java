package br.unip.erp.controller;

import br.unip.erp.dao.VendaDAO;
import br.unip.erp.model.Venda;

import java.math.BigDecimal;
import java.util.List;

/** Controller da entidade VENDA (padrao MVC). */
public class VendaController {

    private final VendaDAO dao = new VendaDAO();

    public int registrar(Venda v) {
        if (v.getCliente() == null || v.getCliente().getCodigo() == 0) {
            throw new IllegalArgumentException("Selecione um cliente para a venda.");
        }
        if (v.getUsuario() == null || v.getUsuario().getCodigo() == 0) {
            throw new IllegalArgumentException("Usuario da venda nao identificado.");
        }
        if (v.getItens() == null || v.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um produto a venda.");
        }
        recalcularTotais(v);
        return dao.inserir(v);
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public List<Venda> listar() {
        return dao.listar();
    }

    /** Soma os itens e aplica o desconto do cabecalho para obter o total. */
    public void recalcularTotais(Venda v) {
        BigDecimal valor = BigDecimal.ZERO;
        for (var item : v.getItens()) {
            BigDecimal totalItem = item.getPreco()
                    .multiply(item.getQtde())
                    .subtract(item.getDesconto() == null ? BigDecimal.ZERO : item.getDesconto());
            item.setTotal(totalItem);
            valor = valor.add(totalItem);
        }
        v.setValor(valor);
        BigDecimal desc = v.getDesconto() == null ? BigDecimal.ZERO : v.getDesconto();
        v.setTotal(valor.subtract(desc));
    }
}
