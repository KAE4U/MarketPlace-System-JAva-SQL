package br.unip.erp.dao;

import br.unip.erp.model.Cliente;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade CLIENTE. Gerencia tambem a PESSOA associada dentro
 * de uma transacao (insere/atualiza/exclui as duas em conjunto).
 */
public class ClienteDAO implements GenericDAO<Cliente> {

    private final PessoaDAO pessoaDAO = new PessoaDAO();

    @Override
    public int inserir(Cliente c) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int pesCodigo = pessoaDAO.inserir(conn, c.getPessoa());

            String sql = "INSERT INTO cliente (pes_codigo, cli_limitecred) VALUES (?,?) "
                    + "RETURNING cli_codigo";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pesCodigo);
                ps.setBigDecimal(2, c.getLimiteCredito());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        c.setCodigo(rs.getInt(1));
                    }
                }
            }
            conn.commit();
            return c.getCodigo();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao inserir cliente: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    @Override
    public void atualizar(Cliente c) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            pessoaDAO.atualizar(conn, c.getPessoa());

            String sql = "UPDATE cliente SET cli_limitecred=? WHERE cli_codigo=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, c.getLimiteCredito());
                ps.setInt(2, c.getCodigo());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao atualizar cliente: " + e.getMessage(), e);
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

            // Descobre a pessoa vinculada
            int pesCodigo = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT pes_codigo FROM cliente WHERE cli_codigo=?")) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        pesCodigo = rs.getInt(1);
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM cliente WHERE cli_codigo=?")) {
                ps.setInt(1, codigo);
                ps.executeUpdate();
            }
            if (pesCodigo > 0) {
                pessoaDAO.excluir(conn, pesCodigo);
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao excluir cliente: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    @Override
    public Cliente buscarPorCodigo(int codigo) {
        String sql = "SELECT c.cli_codigo, c.cli_limitecred, p.* "
                + "FROM cliente c JOIN pessoa p ON p.pes_codigo = c.pes_codigo "
                + "WHERE c.cli_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT c.cli_codigo, c.cli_limitecred, p.* "
                + "FROM cliente c JOIN pessoa p ON p.pes_codigo = c.pes_codigo "
                + "ORDER BY p.pes_nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setCodigo(rs.getInt("cli_codigo"));
        c.setLimiteCredito(rs.getBigDecimal("cli_limitecred"));
        c.setPessoa(pessoaDAO.mapear(rs));
        return c;
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
