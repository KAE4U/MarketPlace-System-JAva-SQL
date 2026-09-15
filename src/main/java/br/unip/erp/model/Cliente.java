package br.unip.erp.model;

import java.math.BigDecimal;

/** Entidade CLIENTE (associada a uma PESSOA). */
public class Cliente {

    private int codigo;
    private Pessoa pessoa = new Pessoa();
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }

    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }

    @Override
    public String toString() {
        return pessoa != null ? pessoa.getNome() : "Cliente " + codigo;
    }
}
