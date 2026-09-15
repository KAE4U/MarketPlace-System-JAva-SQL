package br.unip.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Entidade COMPRA, com seus itens (produtos). */
public class Compra {

    private int codigo;
    private Usuario usuario = new Usuario();
    private Fornecedor fornecedor = new Fornecedor();
    private LocalDate emissao = LocalDate.now();
    private BigDecimal valor = BigDecimal.ZERO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private LocalDate dataEntrada;
    private String obs;

    private List<CompraProduto> itens = new ArrayList<>();

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public LocalDate getEmissao() { return emissao; }
    public void setEmissao(LocalDate emissao) { this.emissao = emissao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public String getObs() { return obs; }
    public void setObs(String obs) { this.obs = obs; }

    public List<CompraProduto> getItens() { return itens; }
    public void setItens(List<CompraProduto> itens) { this.itens = itens; }
}
