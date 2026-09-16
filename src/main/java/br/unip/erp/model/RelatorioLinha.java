package br.unip.erp.model;

import java.math.BigDecimal;

/**
 * Linha generica de relatorio: um rotulo (data, nome do produto, etc.),
 * uma quantidade e um valor. Reutilizada pelos diferentes relatorios.
 */
public class RelatorioLinha {

    private String rotulo;
    private BigDecimal quantidade = BigDecimal.ZERO;
    private BigDecimal valor = BigDecimal.ZERO;

    public RelatorioLinha() { }

    public RelatorioLinha(String rotulo, BigDecimal quantidade, BigDecimal valor) {
        this.rotulo = rotulo;
        this.quantidade = quantidade != null ? quantidade : BigDecimal.ZERO;
        this.valor = valor != null ? valor : BigDecimal.ZERO;
    }

    public String getRotulo() { return rotulo; }
    public void setRotulo(String rotulo) { this.rotulo = rotulo; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
