package br.unip.erp.dao;

import br.unip.erp.model.Pessoa;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO auxiliar da entidade PESSOA. Suas operacoes recebem a Connection
 * do chamador para participarem da mesma transacao de Cliente/Fornecedor.
 */
public class PessoaDAO {

    /** Insere uma Pessoa usando a conexao fornecida; retorna o codigo gerado. */
    public int inserir(Connection conn, Pessoa p) throws SQLException {
        String sql = "INSERT INTO pessoa (pes_nome, pes_fantasia, pes_fisica, pes_cpfcnpj, pes_rgie, "
                + "pes_endereco, pes_numero, pes_complemento, pes_bairro, pes_cidade, pes_uf, pes_cep, "
                + "pes_fone1, pes_fone2, pes_celular, pes_site, pes_email, pes_ativo) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING pes_codigo";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            preencher(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setCodigo(rs.getInt(1));
                }
            }
        }
        return p.getCodigo();
    }

    /** Atualiza a Pessoa usando a conexao fornecida. */
    public void atualizar(Connection conn, Pessoa p) throws SQLException {
        String sql = "UPDATE pessoa SET pes_nome=?, pes_fantasia=?, pes_fisica=?, pes_cpfcnpj=?, "
                + "pes_rgie=?, pes_endereco=?, pes_numero=?, pes_complemento=?, pes_bairro=?, "
                + "pes_cidade=?, pes_uf=?, pes_cep=?, pes_fone1=?, pes_fone2=?, pes_celular=?, "
                + "pes_site=?, pes_email=?, pes_ativo=? WHERE pes_codigo=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            preencher(ps, p);
            ps.setInt(19, p.getCodigo());
            ps.executeUpdate();
        }
    }

    /** Exclui a Pessoa usando a conexao fornecida. */
    public void excluir(Connection conn, int codigo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM pessoa WHERE pes_codigo=?")) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Pessoa p) throws SQLException {
        ps.setString(1, p.getNome());
        ps.setString(2, p.getFantasia());
        ps.setString(3, p.getFisica());
        ps.setString(4, p.getCpfCnpj());
        ps.setString(5, p.getRgIe());
        ps.setString(6, p.getEndereco());
        ps.setString(7, p.getNumero());
        ps.setString(8, p.getComplemento());
        ps.setString(9, p.getBairro());
        ps.setString(10, p.getCidade());
        ps.setString(11, p.getUf());
        ps.setString(12, p.getCep());
        ps.setString(13, p.getFone1());
        ps.setString(14, p.getFone2());
        ps.setString(15, p.getCelular());
        ps.setString(16, p.getSite());
        ps.setString(17, p.getEmail());
        ps.setString(18, p.getAtivo());
    }

    /** Preenche um objeto Pessoa a partir de um ResultSet (colunas prefixadas pes_). */
    public Pessoa mapear(ResultSet rs) throws SQLException {
        Pessoa p = new Pessoa();
        p.setCodigo(rs.getInt("pes_codigo"));
        p.setNome(rs.getString("pes_nome"));
        p.setFantasia(rs.getString("pes_fantasia"));
        p.setFisica(rs.getString("pes_fisica"));
        p.setCpfCnpj(rs.getString("pes_cpfcnpj"));
        p.setRgIe(rs.getString("pes_rgie"));
        Date cad = rs.getDate("pes_cadastro");
        if (cad != null) {
            p.setCadastro(cad.toLocalDate());
        }
        p.setEndereco(rs.getString("pes_endereco"));
        p.setNumero(rs.getString("pes_numero"));
        p.setComplemento(rs.getString("pes_complemento"));
        p.setBairro(rs.getString("pes_bairro"));
        p.setCidade(rs.getString("pes_cidade"));
        p.setUf(rs.getString("pes_uf"));
        p.setCep(rs.getString("pes_cep"));
        p.setFone1(rs.getString("pes_fone1"));
        p.setFone2(rs.getString("pes_fone2"));
        p.setCelular(rs.getString("pes_celular"));
        p.setSite(rs.getString("pes_site"));
        p.setEmail(rs.getString("pes_email"));
        p.setAtivo(rs.getString("pes_ativo"));
        return p;
    }
}
