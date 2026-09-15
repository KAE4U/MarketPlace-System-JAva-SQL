package br.unip.erp.dao;

import br.unip.erp.model.Fornecedor;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade FORNECEDOR. Gerencia a PESSOA associada dentro
 * de uma transacao (insere/atualiza/exclui as duas em conjunto).
 */
public class FornecedorDAO implements GenericDAO<Fornecedor> {

    private final PessoaDAO pessoaDAO = new PessoaDAO();

    @Override
    public int inserir(Fornecedor f) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int pesCodigo = pessoaDAO.inserir(conn, f.getPessoa());

            String sql = "INSERT INTO fornecedor (pes_codigo, for_contato) VALUES (?,?) "
                    + "RETURNING for_codigo";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pesCodigo);
                ps.setString(2, f.getContato());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        f.setCodigo(rs.getInt(1));
                    }
                }
            }
            conn.commit();
            return f.getCodigo();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao inserir fornecedor: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    @Override
    public void atualizar(Fornecedor f) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            pessoaDAO.atualizar(conn, f.getPessoa());

            String sql = "UPDATE fornecedor SET for_contato=? WHERE for_codigo=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, f.getContato());
                ps.setInt(2, f.getCodigo());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    @Override
    public void excluir(int codigo) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int pesCodigo = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT pes_codigo FROM fornecedor WHERE for_codigo=?")) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        pesCodigo = rs.getInt(1);
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM fornecedor WHERE for_codigo=?")) {
                ps.setInt(1, codigo);
                ps.executeUpdate();
            }
            if (pesCodigo > 0) {
                pessoaDAO.excluir(conn, pesCodigo);
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao excluir fornecedor: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    @Override
    public Fornecedor buscarPorCodigo(int codigo) {
        String sql = "SELECT f.for_codigo, f.for_contato, p.* "
                + "FROM fornecedor f JOIN pessoa p ON p.pes_codigo = f.pes_codigo "
                + "WHERE f.for_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar fornecedor: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Fornecedor> listar() {
        String sql = "SELECT f.for_codigo, f.for_contato, p.* "
                + "FROM fornecedor f JOIN pessoa p ON p.pes_codigo = f.pes_codigo "
                + "ORDER BY p.pes_nome";
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar fornecedores: " + e.getMessage(), e);
        }
        return lista;
    }

    private Fornecedor mapear(ResultSet rs) throws SQLException {
        Fornecedor f = new Fornecedor();
        f.setCodigo(rs.getInt("for_codigo"));
        f.setContato(rs.getString("for_contato"));
        f.setPessoa(pessoaDAO.mapear(rs));
        return f;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) { }
        }
    }

    private void restore(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) { }
            ConnectionFactory.close(conn);
        }
    }
}
