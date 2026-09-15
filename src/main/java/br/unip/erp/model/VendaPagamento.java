package br.unip.erp.model;

import java.math.BigDecimal;

/** Forma de pagamento de uma VENDA (VENDA_PAGTO). */
public class VendaPagamento {

    private int codigo;
    private int vendaCodigo;
    private FormaPagamento formaPagamento = new FormaPagamento();
    private BigDecimal valor = BigDecimal.ZERO;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public int getVendaCodigo() { return vendaCodigo; }
    public void setVendaCodigo(int vendaCodigo) { this.vendaCodigo = vendaCodigo; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
