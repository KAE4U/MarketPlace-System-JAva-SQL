package br.unip.erp.controller;

import br.unip.erp.dao.CompraDAO;
import br.unip.erp.model.Compra;

import java.math.BigDecimal;
import java.util.List;

/** Controller da entidade COMPRA (padrao MVC). */
public class CompraController {

    private final CompraDAO dao = new CompraDAO();

    public int registrar(Compra c) {
        if (c.getFornecedor() == null || c.getFornecedor().getCodigo() == 0) {
            throw new IllegalArgumentException("Selecione um fornecedor para a compra.");
        }
        if (c.getUsuario() == null || c.getUsuario().getCodigo() == 0) {
            throw new IllegalArgumentException("Usuario da compra nao identificado.");
        }
        if (c.getItens() == null || c.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um produto a compra.");
        }
        recalcularTotais(c);
        return dao.inserir(c);
    }

    public void excluir(int codigo) {
        dao.excluir(codigo);
    }

    public List<Compra> listar() {
        return dao.listar();
    }

    /** Soma os itens e aplica o desconto do cabecalho para obter o total. */
    public void recalcularTotais(Compra c) {
        BigDecimal valor = BigDecimal.ZERO;
        for (var item : c.getItens()) {
            BigDecimal totalItem = item.getPreco()
                    .multiply(item.getQtde())
                    .subtract(item.getDesconto() == null ? BigDecimal.ZERO : item.getDesconto());
            item.setTotal(totalItem);
            valor = valor.add(totalItem);
        }
        c.setValor(valor);
        BigDecimal desc = c.getDesconto() == null ? BigDecimal.ZERO : c.getDesconto();
        c.setTotal(valor.subtract(desc));
    }
}
