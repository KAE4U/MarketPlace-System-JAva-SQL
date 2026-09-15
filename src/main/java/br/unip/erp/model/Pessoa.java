package br.unip.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Entidade PESSOA - super-entidade de Cliente e Fornecedor. */
public class Pessoa {

    private int codigo;
    private String nome;
    private String fantasia;
    private String fisica = "F";     // F=Fisica, J=Juridica
    private String cpfCnpj;
    private String rgIe;
    private LocalDate cadastro;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String fone1;
    private String fone2;
    private String celular;
    private String site;
    private String email;
    private String ativo = "S";      // S=Sim, N=Nao

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getFantasia() { return fantasia; }
    public void setFantasia(String fantasia) { this.fantasia = fantasia; }

    public String getFisica() { return fisica; }
    public void setFisica(String fisica) { this.fisica = fisica; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getRgIe() { return rgIe; }
    public void setRgIe(String rgIe) { this.rgIe = rgIe; }

    public LocalDate getCadastro() { return cadastro; }
    public void setCadastro(LocalDate cadastro) { this.cadastro = cadastro; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getFone1() { return fone1; }
    public void setFone1(String fone1) { this.fone1 = fone1; }

    public String getFone2() { return fone2; }
    public void setFone2(String fone2) { this.fone2 = fone2; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome;
    }
}
