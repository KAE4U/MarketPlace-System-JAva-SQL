package br.unip.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Entidade VENDA, com seus itens (produtos) e formas de pagamento. */
public class Venda {

    private int codigo;
    private Usuario usuario = new Usuario();
    private Cliente cliente = new Cliente();
    private LocalDate data = LocalDate.now();
    private BigDecimal valor = BigDecimal.ZERO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private String obs;

    private List<VendaProduto> itens = new ArrayList<>();
    private List<VendaPagamento> pagamentos = new ArrayList<>();

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getObs() { return obs; }
    public void setObs(String obs) { this.obs = obs; }

    public List<VendaProduto> getItens() { return itens; }
    public void setItens(List<VendaProduto> itens) { this.itens = itens; }

    public List<VendaPagamento> getPagamentos() { return pagamentos; }
    public void setPagamentos(List<VendaPagamento> pagamentos) { this.pagamentos = pagamentos; }
}
