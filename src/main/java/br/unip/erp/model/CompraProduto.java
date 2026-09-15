package br.unip.erp.model;

import java.math.BigDecimal;

/** Item de uma COMPRA (COMPRA_PRODUTO). */
public class CompraProduto {

    private int codigo;
    private int compraCodigo;
    private Produto produto = new Produto();
    private BigDecimal qtde = BigDecimal.ZERO;
    private BigDecimal preco = BigDecimal.ZERO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public int getCompraCodigo() { return compraCodigo; }
    public void setCompraCodigo(int compraCodigo) { this.compraCodigo = compraCodigo; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public BigDecimal getQtde() { return qtde; }
    public void setQtde(BigDecimal qtde) { this.qtde = qtde; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
