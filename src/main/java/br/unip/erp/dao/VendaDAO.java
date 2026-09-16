package br.unip.erp.dao;

import br.unip.erp.model.Venda;
import br.unip.erp.model.VendaPagamento;
import br.unip.erp.model.VendaProduto;
import br.unip.erp.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade VENDA. Persiste, em uma unica transacao, o cabecalho,
 * os itens (VENDA_PRODUTO), as formas de pagamento (VENDA_PAGTO) e baixa
 * o estoque dos produtos vendidos.
 */
public class VendaDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public int inserir(Venda v) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO venda (usu_codigo, cli_codigo, vda_data, vda_valor, "
                    + "vda_desconto, vda_total, vda_obs) VALUES (?,?,?,?,?,?,?) RETURNING vda_codigo";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, v.getUsuario().getCodigo());
                ps.setInt(2, v.getCliente().getCodigo());
                ps.setDate(3, Date.valueOf(v.getData()));
                ps.setBigDecimal(4, v.getValor());
                ps.setBigDecimal(5, v.getDesconto());
                ps.setBigDecimal(6, v.getTotal());
                ps.setString(7, v.getObs());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        v.setCodigo(rs.getInt(1));
                    }
                }
            }

            // Itens + baixa de estoque
            String sqlItem = "INSERT INTO venda_produto (vda_codigo, pro_codigo, vep_qtde, "
                    + "vep_preco, vep_desconto, vep_total) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                for (VendaProduto item : v.getItens()) {
                    ps.setInt(1, v.getCodigo());
                    ps.setInt(2, item.getProduto().getCodigo());
                    ps.setBigDecimal(3, item.getQtde());
                    ps.setBigDecimal(4, item.getPreco());
                    ps.setBigDecimal(5, item.getDesconto());
                    ps.setBigDecimal(6, item.getTotal());
                    ps.executeUpdate();
                    // Venda diminui o estoque
                    produtoDAO.movimentarEstoque(conn, item.getProduto().getCodigo(),
                            item.getQtde().negate());
                }
            }

            // Formas de pagamento
            String sqlPg = "INSERT INTO venda_pagto (vda_codigo, fpg_codigo, vdp_valor) "
                    + "VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPg)) {
                for (VendaPagamento pg : v.getPagamentos()) {
                    ps.setInt(1, v.getCodigo());
                    ps.setInt(2, pg.getFormaPagamento().getCodigo());
                    ps.setBigDecimal(3, pg.getValor());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return v.getCodigo();
        } catch (SQLException e) {
            rollback(conn);
            throw new DAOException("Erro ao registrar venda: " + e.getMessage(), e);
        } finally {
            restore(conn);
        }
    }

    public void excluir(int codigo) {
        // itens e pagamentos saem por ON DELETE CASCADE
        String sql = "DELETE FROM venda WHERE vda_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir venda: " + e.getMessage(), e);
        }
    }

    /** Lista resumo das vendas (cabecalho + nome do cliente). */
    public List<Venda> listar() {
        String sql = "SELECT v.vda_codigo, v.vda_data, v.vda_total, v.cli_codigo, p.pes_nome "
                + "FROM venda v "
                + "JOIN cliente c ON c.cli_codigo = v.cli_codigo "
                + "JOIN pessoa p ON p.pes_codigo = c.pes_codigo "
                + "ORDER BY v.vda_codigo DESC";
        List<Venda> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venda v = new Venda();
                v.setCodigo(rs.getInt("vda_codigo"));
                Date d = rs.getDate("vda_data");
                if (d != null) {
                    v.setData(d.toLocalDate());
                }
                v.setTotal(rs.getBigDecimal("vda_total"));
                v.getCliente().setCodigo(rs.getInt("cli_codigo"));
                v.getCliente().getPessoa().setNome(rs.getString("pes_nome"));
                lista.add(v);
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar vendas: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Carrega uma venda completa (cabecalho + cliente + itens + pagamentos)
     * para exibicao de detalhes.
     */
    public Venda buscarPorCodigo(int codigo) {
        String sqlCab = "SELECT v.vda_codigo, v.vda_data, v.vda_valor, v.vda_desconto, "
                + "v.vda_total, v.vda_obs, v.cli_codigo, pc.pes_nome AS cliente_nome, "
                + "v.usu_codigo, u.usu_nome AS usuario_nome "
                + "FROM venda v "
                + "JOIN cliente c ON c.cli_codigo = v.cli_codigo "
                + "JOIN pessoa pc ON pc.pes_codigo = c.pes_codigo "
                + "JOIN usuario u ON u.usu_codigo = v.usu_codigo "
                + "WHERE v.vda_codigo = ?";
        String sqlItens = "SELECT vp.pro_codigo, pr.pro_nome, vp.vep_qtde, vp.vep_preco, "
                + "vp.vep_desconto, vp.vep_total "
                + "FROM venda_produto vp "
                + "JOIN produto pr ON pr.pro_codigo = vp.pro_codigo "
                + "WHERE vp.vda_codigo = ? ORDER BY vp.vep_codigo";
        String sqlPagtos = "SELECT vpg.fpg_codigo, fp.fpg_nome, vpg.vdp_valor "
                + "FROM venda_pagto vpg "
                + "JOIN formapagto fp ON fp.fpg_codigo = vpg.fpg_codigo "
                + "WHERE vpg.vda_codigo = ? ORDER BY vpg.vdp_codigo";

        try (Connection conn = ConnectionFactory.getConnection()) {
            Venda v = new Venda();
            try (PreparedStatement ps = conn.prepareStatement(sqlCab)) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    v.setCodigo(rs.getInt("vda_codigo"));
                    Date d = rs.getDate("vda_data");
                    if (d != null) {
                        v.setData(d.toLocalDate());
                    }
                    v.setValor(rs.getBigDecimal("vda_valor"));
                    v.setDesconto(rs.getBigDecimal("vda_desconto"));
                    v.setTotal(rs.getBigDecimal("vda_total"));
                    v.setObs(rs.getString("vda_obs"));
                    v.getCliente().setCodigo(rs.getInt("cli_codigo"));
                    v.getCliente().getPessoa().setNome(rs.getString("cliente_nome"));
                    v.getUsuario().setCodigo(rs.getInt("usu_codigo"));
                    v.getUsuario().setNome(rs.getString("usuario_nome"));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlItens)) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        VendaProduto item = new VendaProduto();
                        item.setVendaCodigo(codigo);
                        item.getProduto().setCodigo(rs.getInt("pro_codigo"));
                        item.getProduto().setNome(rs.getString("pro_nome"));
                        item.setQtde(rs.getBigDecimal("vep_qtde"));
                        item.setPreco(rs.getBigDecimal("vep_preco"));
                        item.setDesconto(rs.getBigDecimal("vep_desconto"));
                        item.setTotal(rs.getBigDecimal("vep_total"));
                        v.getItens().add(item);
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlPagtos)) {
                ps.setInt(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        VendaPagamento pg = new VendaPagamento();
                        pg.setVendaCodigo(codigo);
                        pg.getFormaPagamento().setCodigo(rs.getInt("fpg_codigo"));
                        pg.getFormaPagamento().setNome(rs.getString("fpg_nome"));
                        pg.setValor(rs.getBigDecimal("vdp_valor"));
                        v.getPagamentos().add(pg);
                    }
                }
            }
            return v;
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar venda: " + e.getMessage(), e);
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
