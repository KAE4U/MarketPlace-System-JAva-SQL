package br.unip.erp.dao;

import br.unip.erp.model.FormaPagamento;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO da entidade FORMAPAGTO. */
public class FormaPagamentoDAO implements GenericDAO<FormaPagamento> {

    @Override
    public int inserir(FormaPagamento f) {
        String sql = "INSERT INTO formapagto (fpg_nome, fpg_ativo) VALUES (?,?) RETURNING fpg_codigo";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getAtivo());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    f.setCodigo(rs.getInt(1));
                }
            }
            return f.getCodigo();
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir forma de pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(FormaPagamento f) {
        String sql = "UPDATE formapagto SET fpg_nome=?, fpg_ativo=? WHERE fpg_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getAtivo());
            ps.setInt(3, f.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar forma de pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int codigo) {
        String sql = "DELETE FROM formapagto WHERE fpg_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir forma de pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public FormaPagamento buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM formapagto WHERE fpg_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar forma de pagamento: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<FormaPagamento> listar() {
        String sql = "SELECT * FROM formapagto ORDER BY fpg_nome";
        List<FormaPagamento> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar formas de pagamento: " + e.getMessage(), e);
        }
        return lista;
    }

    private FormaPagamento mapear(ResultSet rs) throws SQLException {
        FormaPagamento f = new FormaPagamento();
        f.setCodigo(rs.getInt("fpg_codigo"));
        f.setNome(rs.getString("fpg_nome"));
        f.setAtivo(rs.getString("fpg_ativo"));
        return f;
    }
}
