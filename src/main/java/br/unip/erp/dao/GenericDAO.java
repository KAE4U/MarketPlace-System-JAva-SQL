package br.unip.erp.dao;

import java.util.List;

/**
 * Contrato generico do padrao DAO (Data Access Object).
 *
 * @param <T> tipo da entidade manipulada
 */
public interface GenericDAO<T> {

    /** Insere uma nova entidade e retorna o codigo gerado. */
    int inserir(T entidade);

    /** Atualiza uma entidade existente. */
    void atualizar(T entidade);

    /** Exclui a entidade pelo codigo. */
    void excluir(int codigo);

    /** Busca uma entidade pelo codigo (ou null se nao existir). */
    T buscarPorCodigo(int codigo);

    /** Lista todas as entidades. */
    List<T> listar();
}
