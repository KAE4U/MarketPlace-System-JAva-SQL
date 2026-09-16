package br.unip.erp.controller;

import br.unip.erp.dao.RelatorioDAO;
import br.unip.erp.model.RelatorioLinha;

import java.time.LocalDate;
import java.util.List;

/** Controller dos relatorios (padrao MVC). */
public class RelatorioController {

    private final RelatorioDAO dao = new RelatorioDAO();

    public List<RelatorioLinha> vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe as datas de início e fim.");
        }
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à inicial.");
        }
        return dao.vendasPorPeriodo(inicio, fim);
    }

    public List<RelatorioLinha> produtosMaisVendidos(int limite) {
        if (limite <= 0) {
            limite = 10;
        }
        return dao.produtosMaisVendidos(limite);
    }
}
