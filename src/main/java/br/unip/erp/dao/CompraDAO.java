package br.unip.erp.dao;

import br.unip.erp.model.Compra;
import br.unip.erp.model.CompraProduto;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade COMPRA. Persiste, em uma unica transacao, o cabecalho,
 * os itens (COMPRA_PRODUTO) e incrementa o estoque dos produtos comprados.
 */
public class CompraDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public int inserir(Compra c) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO compra (usu_codigo, for_codigo, cpr_emissao, cpr_valor, "
                    + "cpr_desconto, cpr_total, cpr_dtentrada, cpr_obs) "
                    + "VALUES (?,?,?,?,?,?,?,?) RETURNING cpr_codigo";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getUsuario().getCodigo());
                ps.setInt(2, c.getFornecedor().getCodigo());
                ps.setDate(3, Date.valueOf(c.getEmissao()));
                ps.setBigDecimal(4, c.getValor());
                ps.setBigDecimal(5, c.getDesconto());
                ps.setBigDecimal(6, c.getTotal());
                if (c.getDataEntrada() != null) {
                    ps.setDate(7, Date.valueOf(c.getDataEntrada()));
                } else {
                    ps.setNull(7, java.sql.Types.DATE);
                }
                ps.setString(8, c.getObs());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        c.setCodigo(rs.getInt(1));
                    }
                }
            }

            // Itens + entrada de estoque
            String sqlItem = "INSERT INTO compra_produto (cpr_codigo, pro_codigo, cpp_qtde, "
                    + "cpp_preco, cpp_desconto, cpp_total) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                for (CompraProduto item : c.getItens()) {
                    ps.setInt(1, c.getCodigo());
                    ps.setInt(2, item.getProduto().getCodigo());
                    ps.setBigDecimal(3, item.getQtde());
                    ps.setBigDecimal(4, item.getPreco());
                    ps.setBigDecimal(5, item.getDesconto());
                    ps.setBigDecimal(6, item.getTotal());
                    ps.executeUpdate();
                    // Compra aumenta o estoque
                    produtoDAO.movimentarEstoque(conn, item.getProduto().getCodigo(), item.getQtde());
                }
            }

            conn.commit();
            return c.getCodigo();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao registrar compra: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    public void excluir(int codigo) {
        String sql = "DELETE FROM compra WHERE cpr_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir compra: " + e.getMessage(), e);
        }
    }

    /** Lista resumo das compras (cabecalho + nome do fornecedor). */
    public List<Compra> listar() {
        String sql = "SELECT c.cpr_codigo, c.cpr_emissao, c.cpr_total, c.for_codigo, p.pes_nome "
                + "FROM compra c "
                + "JOIN fornecedor f ON f.for_codigo = c.for_codigo "
                + "JOIN pessoa p ON p.pes_codigo = f.pes_codigo "
                + "ORDER BY c.cpr_codigo DESC";
        List<Compra> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Compra c = new Compra();
                c.setCodigo(rs.getInt("cpr_codigo"));
                Date d = rs.getDate("cpr_emissao");
                if (d != null) {
                    c.setEmissao(d.toLocalDate());
                }
                c.setTotal(rs.getBigDecimal("cpr_total"));
                c.getFornecedor().setCodigo(rs.getInt("for_codigo"));
                c.getFornecedor().getPessoa().setNome(rs.getString("pes_nome"));
                lista.add(c);
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar compras: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Carrega uma compra completa (cabecalho + fornecedor + itens) para detalhes. */
    public Compra buscarPorCodigo(int codigo) {
        String sqlCab = "SELECT c.cpr_codigo, c.cpr_emissao, c.cpr_valor, c.cpr_desconto, "
                + "c.cpr_total, c.cpr_dtentrada, c.cpr_obs, c.for_codigo, pf.pes_nome AS fornecedor_nome, "
                + "c.usu_codigo, u.usu_nome AS usuario_nome "
                + "FROM compra c "
                + "JOIN fornecedor f ON f.for_codigo = c.for_codigo "
                + "JOIN pessoa pf ON pf.pes_codigo = f.pes_codigo "
                + "JOIN usuario u ON u.usu_codigo = c.usu_codigo "
                + "WHERE c.cpr_codigo = ?";
        String sqlItens = "SELECT cp.pro_codigo, pr.pro_nome, cp.cpp_qtde, cp.cpp_preco, "
                + "cp.cpp_desconto, cp.cpp_total "
                + "FROM compra_produto cp "
                + "JOIN produto pr ON pr.pro_codigo = cp.pro_codigo "
                + "WHERE cp.cpr_codigo = ? ORDER BY cp.cpp_codigo";

        try (Connection conn = ConnectionFactory.getConnection()) {
            Compra c = new Compra();
            try (PreparedStatement ps = conn.prepareStatement(sqlCab)) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    c.setCodigo(rs.getInt("cpr_codigo"));
                    Date em = rs.getDate("cpr_emissao");
                    if (em != null) {
                        c.setEmissao(em.toLocalDate());
                    }
                    Date dt = rs.getDate("cpr_dtentrada");
                    if (dt != null) {
                        c.setDataEntrada(dt.toLocalDate());
                    }
                    c.setValor(rs.getBigDecimal("cpr_valor"));
                    c.setDesconto(rs.getBigDecimal("cpr_desconto"));
                    c.setTotal(rs.getBigDecimal("cpr_total"));
                    c.setObs(rs.getString("cpr_obs"));
                    c.getFornecedor().setCodigo(rs.getInt("for_codigo"));
                    c.getFornecedor().getPessoa().setNome(rs.getString("fornecedor_nome"));
                    c.getUsuario().setCodigo(rs.getInt("usu_codigo"));
                    c.getUsuario().setNome(rs.getString("usuario_nome"));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlItens)) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CompraProduto item = new CompraProduto();
                        item.setCompraCodigo(codigo);
                        item.getProduto().setCodigo(rs.getInt("pro_codigo"));
                        item.getProduto().setNome(rs.getString("pro_nome"));
                        item.setQtde(rs.getBigDecimal("cpp_qtde"));
                        item.setPreco(rs.getBigDecimal("cpp_preco"));
                        item.setDesconto(rs.getBigDecimal("cpp_desconto"));
                        item.setTotal(rs.getBigDecimal("cpp_total"));
                        c.getItens().add(item);
                    }
                }
            }
            return c;
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar compra: " + e.getMessage(), e);
        }
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
