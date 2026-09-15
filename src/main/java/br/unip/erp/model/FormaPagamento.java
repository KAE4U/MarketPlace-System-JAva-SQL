package br.unip.erp.model;

/** Entidade FORMAPAGTO. */
public class FormaPagamento {

    private int codigo;
    private String nome;
    private String ativo = "S";

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome;
    }
}
