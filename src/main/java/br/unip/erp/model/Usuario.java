package br.unip.erp.model;

import java.time.LocalDate;

/** Entidade USUARIO. */
public class Usuario {

    private int codigo;
    private String nome;
    private String login;
    private String senha;
    private LocalDate cadastro;
    private String ativo = "S";

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public LocalDate getCadastro() { return cadastro; }
    public void setCadastro(LocalDate cadastro) { this.cadastro = cadastro; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome != null ? nome : login;
    }
}
