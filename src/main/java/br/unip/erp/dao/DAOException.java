package br.unip.erp.dao;

/** Excecao nao-checada para erros na camada de persistencia (DAO). */
public class DAOException extends RuntimeException {

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(String message) {
        super(message);
    }
}
