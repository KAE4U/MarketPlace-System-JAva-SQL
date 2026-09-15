package br.unip.erp.dao;

import br.unip.erp.model.Produto;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO da entidade PRODUTO. */
public class ProdutoDAO implements GenericDAO<Produto> {

    @Override
    public int inserir(Produto p) {
        String sql = "INSERT INTO produto (pro_nome, pro_estoque, pro_unidade, pro_preco, pro_custo, "
                + "pro_atacado, pro_min, pro_max, pro_embalagem, pro_peso, pro_obs, pro_ativo) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?) RETURNING pro_codigo";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preencher(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setCodigo(rs.getInt(1));
                }
            }
            return p.getCodigo();
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET pro_nome=?, pro_estoque=?, pro_unidade=?, pro_preco=?, "
                + "pro_custo=?, pro_atacado=?, pro_min=?, pro_max=?, pro_embalagem=?, pro_peso=?, "
                + "pro_obs=?, pro_ativo=? WHERE pro_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preencher(ps, p);
            ps.setInt(13, p.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    private void preencher(PreparedStatement ps, Produto p) throws SQLException {
        ps.setString(1, p.getNome());
        ps.setBigDecimal(2, p.getEstoque());
        ps.setString(3, p.getUnidade());
        ps.setBigDecimal(4, p.getPreco());
        ps.setBigDecimal(5, p.getCusto());
        ps.setBigDecimal(6, p.getAtacado());
        ps.setBigDecimal(7, p.getMinimo());
        ps.setBigDecimal(8, p.getMaximo());
        ps.setBigDecimal(9, p.getEmbalagem());
        ps.setBigDecimal(10, p.getPeso());
        ps.setString(11, p.getObs());
        ps.setString(12, p.getAtivo());
    }

    @Override
    public void excluir(int codigo) {
        String sql = "DELETE FROM produto WHERE pro_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    @Override
    public Produto buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM produto WHERE pro_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar produto: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Produto> listar() {
        String sql = "SELECT * FROM produto ORDER BY pro_nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Atualiza o estoque somando (delta positivo) ou subtraindo (delta negativo). */
    public void movimentarEstoque(Connection conn, int codigo, java.math.BigDecimal delta) throws SQLException {
        String sql = "UPDATE produto SET pro_estoque = COALESCE(pro_estoque,0) + ? WHERE pro_codigo=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, delta);
            ps.setInt(2, codigo);
            ps.executeUpdate();
        }
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setCodigo(rs.getInt("pro_codigo"));
        p.setNome(rs.getString("pro_nome"));
        p.setEstoque(rs.getBigDecimal("pro_estoque"));
        p.setUnidade(rs.getString("pro_unidade"));
        p.setPreco(rs.getBigDecimal("pro_preco"));
        p.setCusto(rs.getBigDecimal("pro_custo"));
        p.setAtacado(rs.getBigDecimal("pro_atacado"));
        p.setMinimo(rs.getBigDecimal("pro_min"));
        p.setMaximo(rs.getBigDecimal("pro_max"));
        p.setEmbalagem(rs.getBigDecimal("pro_embalagem"));
        p.setPeso(rs.getBigDecimal("pro_peso"));
        Date cad = rs.getDate("pro_cadastro");
        if (cad != null) {
            p.setCadastro(cad.toLocalDate());
        }
        p.setObs(rs.getString("pro_obs"));
        p.setAtivo(rs.getString("pro_ativo"));
        return p;
    }
}
