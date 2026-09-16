package br.unip.erp.dao;

import br.unip.erp.model.RelatorioLinha;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Consultas agregadas para os relatorios do sistema. */
public class RelatorioDAO {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Vendas por dia dentro de um periodo (inclusive).
     * Retorna, por dia: quantidade de vendas e total faturado.
     */
    public List<RelatorioLinha> vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        String sql = "SELECT vda_data, COUNT(*) AS qtd, COALESCE(SUM(vda_total),0) AS total "
                + "FROM venda "
                + "WHERE vda_data BETWEEN ? AND ? "
                + "GROUP BY vda_data ORDER BY vda_data";
        List<RelatorioLinha> linhas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("vda_data");
                    String rotulo = d != null ? d.toLocalDate().format(DATA) : "-";
                    linhas.add(new RelatorioLinha(rotulo,
                            rs.getBigDecimal("qtd"), rs.getBigDecimal("total")));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro no relatorio de vendas por periodo: " + e.getMessage(), e);
        }
        return linhas;
    }

    /**
     * Produtos mais vendidos: soma das quantidades e do total por produto,
     * ordenado da maior quantidade para a menor. {@code limite} restringe o topo.
     */
    public List<RelatorioLinha> produtosMaisVendidos(int limite) {
        String sql = "SELECT pr.pro_nome, COALESCE(SUM(vp.vep_qtde),0) AS qtd, "
                + "COALESCE(SUM(vp.vep_total),0) AS total "
                + "FROM venda_produto vp "
                + "JOIN produto pr ON pr.pro_codigo = vp.pro_codigo "
                + "GROUP BY pr.pro_codigo, pr.pro_nome "
                + "ORDER BY qtd DESC, total DESC "
                + "LIMIT ?";
        List<RelatorioLinha> linhas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new RelatorioLinha(rs.getString("pro_nome"),
                            rs.getBigDecimal("qtd"), rs.getBigDecimal("total")));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro no relatorio de produtos mais vendidos: " + e.getMessage(), e);
        }
        return linhas;
    }
}
