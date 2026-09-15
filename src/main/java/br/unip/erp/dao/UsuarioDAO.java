package br.unip.erp.dao;

import br.unip.erp.model.Usuario;
import br.unip.erp.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** DAO da entidade USUARIO. */
public class UsuarioDAO implements GenericDAO<Usuario> {

    @Override
    public int inserir(Usuario u) {
        String sql = "INSERT INTO usuario (usu_nome, usu_login, usu_senha, usu_ativo) "
                + "VALUES (?, ?, ?, ?) RETURNING usu_codigo";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getAtivo());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u.setCodigo(rs.getInt(1));
                }
            }
            return u.getCodigo();
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Usuario u) {
        String sql = "UPDATE usuario SET usu_nome=?, usu_login=?, usu_senha=?, usu_ativo=? "
                + "WHERE usu_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getAtivo());
            ps.setInt(5, u.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int codigo) {
        String sql = "DELETE FROM usuario WHERE usu_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM usuario WHERE usu_codigo=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuario: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Usuario> listar() {
        String sql = "SELECT * FROM usuario ORDER BY usu_nome";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuarios: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Autentica pelo login e senha; retorna o usuario ou null. */
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuario WHERE usu_login=? AND usu_senha=? AND usu_ativo='S'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao autenticar: " + e.getMessage(), e);
        }
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setCodigo(rs.getInt("usu_codigo"));
        u.setNome(rs.getString("usu_nome"));
        u.setLogin(rs.getString("usu_login"));
        u.setSenha(rs.getString("usu_senha"));
        Date cad = rs.getDate("usu_cadastro");
        if (cad != null) {
            u.setCadastro(cad.toLocalDate());
        }
        u.setAtivo(rs.getString("usu_ativo"));
        return u;
    }
}
