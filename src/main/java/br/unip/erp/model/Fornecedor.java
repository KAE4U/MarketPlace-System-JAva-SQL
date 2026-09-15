package br.unip.erp.model;

/** Entidade FORNECEDOR (associada a uma PESSOA). */
public class Fornecedor {

    private int codigo;
    private Pessoa pessoa = new Pessoa();
    private String contato;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    @Override
    public String toString() {
        return pessoa != null ? pessoa.getNome() : "Fornecedor " + codigo;
    }
}
