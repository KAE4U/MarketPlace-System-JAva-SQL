package br.unip.erp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrica de conexoes JDBC com o banco PostgreSQL (Supabase).
 *
 * As credenciais sao lidas do arquivo {@code config.properties} presente no
 * classpath (src/main/resources). Nenhuma credencial fica hardcoded no codigo.
 */
public final class ConnectionFactory {

    private static final String CONFIG_FILE = "/config.properties";

    private static String url;
    private static String user;
    private static String password;
    private static boolean ssl;
    private static boolean loaded = false;

    private ConnectionFactory() {
        // classe utilitaria - nao instanciavel
    }

    /** Carrega o config.properties do classpath uma unica vez. */
    private static synchronized void load() {
        if (loaded) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = ConnectionFactory.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Arquivo de configuracao nao encontrado no classpath: " + CONFIG_FILE);
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + CONFIG_FILE, e);
        }

        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");
        ssl = Boolean.parseBoolean(props.getProperty("db.ssl", "true"));

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                    "Configuracao incompleta em " + CONFIG_FILE
                    + " (db.url, db.user e db.password sao obrigatorios).");
        }

        // Garante que o driver PostgreSQL esteja registrado.
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver PostgreSQL nao encontrado no classpath.", e);
        }
        loaded = true;
    }

    /**
     * Abre uma nova conexao com o banco de dados.
     *
     * @return conexao JDBC aberta
     * @throws SQLException se a conexao falhar
     */
    public static Connection getConnection() throws SQLException {
        load();
        Properties connProps = new Properties();
        connProps.setProperty("user", user);
        connProps.setProperty("password", password);
        if (ssl) {
            connProps.setProperty("sslmode", "require");
        }
        // O transaction pooler do Supabase (porta 6543 / pgbouncer) nao suporta
        // prepared statements no lado do servidor. prepareThreshold=0 forca o
        // driver a usar "simple query mode", evitando o erro
        // 'prepared statement "S_x" already exists'. Inofensivo na conexao direta.
        connProps.setProperty("prepareThreshold", "0");
        return DriverManager.getConnection(url, connProps);
    }

    /** Fecha silenciosamente uma conexao (uso em blocos finally). */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }
}
