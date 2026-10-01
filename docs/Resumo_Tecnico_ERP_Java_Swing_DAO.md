# Sistema ERP de Compra e Venda
## Resumo técnico e guia de uso — Java Swing, JDBC, MVC e DAO

**Projeto:** MarketPlace-System-JAva-SQL  
**Disciplina:** Práticas de Programação Orientada a Objetos — UNIP  
**Tecnologias:** Java 17, Swing, Maven, PostgreSQL/Supabase, JDBC e FlatLaf

---

## 1. Objetivo do trabalho

O projeto implementa um ERP desktop para controlar cadastros, compras, vendas, estoque, formas de pagamento, usuários e relatórios. A interface foi construída com Java Swing; os dados são persistidos em PostgreSQL hospedado no Supabase por meio de JDBC.

O código aplica duas organizações principais:

- **MVC:** separa interface, regras de negócio e dados.
- **DAO:** concentra todo o acesso SQL em classes específicas, evitando SQL nas telas.

## 2. Funcionalidades

- Login com sessão de usuário.
- Perfis comum e administrador.
- Somente administradores podem excluir registros.
- Cadastros de usuários, clientes, fornecedores, produtos e formas de pagamento.
- Máscaras para CPF/CNPJ, telefone, datas e valores monetários.
- Validação dos dígitos verificadores de CPF e CNPJ.
- Busca dinâmica nas tabelas de cadastro.
- Compra com fornecedor, produtos e entrada automática no estoque.
- Venda com cliente, produtos, pagamentos e baixa automática no estoque.
- Histórico de compras e vendas.
- Detalhamento de itens e pagamentos dos movimentos.
- Relatório de vendas por período.
- Ranking dos produtos mais vendidos.
- Alternância entre tema claro e escuro.

## 3. Uso do sistema

### Configuração

A conexão é configurada em `src/main/resources/config.properties`. O arquivo possui URL JDBC, usuário, senha e opção de SSL. A senha real não deve ser publicada no GitHub.

### Execução

```bash
mvn clean package
java -jar target/marketplace-erp.jar
```

Também é possível executar com `mvn exec:java` ou diretamente pela classe `Main` no IntelliJ.

### Fluxo de uso

1. Iniciar o sistema e autenticar-se.
2. Cadastrar usuários e definir, quando necessário, o perfil administrador.
3. Cadastrar clientes, fornecedores, produtos e formas de pagamento.
4. Registrar compras para incluir produtos no estoque.
5. Registrar vendas para baixar produtos do estoque.
6. Consultar históricos e abrir detalhes por botão ou duplo clique.
7. Consultar relatórios no menu Relatórios.
8. Alternar tema no menu Exibir.

## 4. Arquitetura MVC

### Model

Contém os objetos que representam os dados do domínio: `Pessoa`, `Cliente`, `Fornecedor`, `Usuario`, `Produto`, `FormaPagamento`, `Venda`, `VendaProduto`, `VendaPagamento`, `Compra` e `CompraProduto`.

### View

Contém telas Swing como `LoginView`, `MenuView`, formulários de cadastro, telas de movimentos, históricos, detalhes e relatórios.

### Controller

Recebe ações das telas, valida dados, aplica regras e chama os DAOs. Exemplos: `UsuarioController`, `ClienteController`, `VendaController` e `RelatorioController`.

### DAO

Executa SQL e transforma linhas do banco em objetos Java. A View não acessa diretamente o banco.

Fluxo principal:

```text
Usuário → View → Controller → DAO → ConnectionFactory/JDBC → Supabase
                                      ↓
Usuário ← View ← Controller ← objetos Model / resultados SQL
```

## 5. Padrão DAO — parte central do projeto

DAO significa **Data Access Object**. O objetivo é isolar a persistência. Se o SQL estivesse dentro das telas, interface e banco ficariam acoplados, dificultando manutenção e testes.

### Contrato GenericDAO

```java
public interface GenericDAO<T> {
    int inserir(T entidade);
    void atualizar(T entidade);
    void excluir(int codigo);
    T buscarPorCodigo(int codigo);
    List<T> listar();
}
```

Esse contrato padroniza o CRUD. Classes como `ClienteDAO`, `FornecedorDAO`, `ProdutoDAO`, `UsuarioDAO` e `FormaPagamentoDAO` implementam a interface.

### Responsabilidades do DAO

- Abrir conexão pela `ConnectionFactory`.
- Preparar e executar comandos SQL.
- Passar parâmetros usando `PreparedStatement`.
- Ler `ResultSet` e montar objetos Model.
- Controlar commit e rollback em operações compostas.
- Fechar `Connection`, `PreparedStatement` e `ResultSet`.
- Converter `SQLException` em `DAOException` com mensagem contextual.

### PreparedStatement

Os valores não são concatenados no SQL. São enviados por parâmetros `?`, o que reduz risco de SQL injection e trata corretamente textos, datas e números.

```java
String sql = "SELECT * FROM usuario WHERE usu_login=? AND usu_senha=?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, login);
    ps.setString(2, senha);
}
```

### Mapeamento de resultados

O DAO lê as colunas do `ResultSet` e preenche um objeto Java. Assim, controllers e views trabalham com objetos, não com linhas SQL.

```java
Usuario u = new Usuario();
u.setCodigo(rs.getInt("usu_codigo"));
u.setNome(rs.getString("usu_nome"));
u.setLogin(rs.getString("usu_login"));
u.setAdmin(rs.getString("usu_admin"));
```

### Transação de Cliente e Pessoa

Cliente depende de Pessoa. Ao inserir um cliente, `ClienteDAO`:

1. Desabilita o auto-commit.
2. Insere a Pessoa usando `PessoaDAO` e a mesma conexão.
3. Obtém o código gerado.
4. Insere o Cliente com a chave da Pessoa.
5. Executa `commit()` se tudo funcionar.
6. Executa `rollback()` se qualquer etapa falhar.

Isso impede que fique uma Pessoa sem Cliente quando a segunda inserção falha.

### Transação de Venda

`VendaDAO.inserir` coordena uma operação maior:

1. Insere o cabeçalho da venda.
2. Insere cada item em `venda_produto`.
3. Reduz o estoque de cada produto.
4. Insere as formas de pagamento em `venda_pagto`.
5. Confirma tudo com `commit()`.
6. Em erro, usa `rollback()` para desfazer toda a operação.

A atomicidade garante que não exista venda pela metade, item sem cabeçalho ou estoque alterado sem venda registrada.

### Transação de Compra

`CompraDAO` usa o mesmo princípio, mas a movimentação incrementa o estoque. O cabeçalho, os itens e a atualização dos produtos pertencem à mesma transação.

### Consultas de detalhes e relatórios

- `VendaDAO.buscarPorCodigo`: carrega cabeçalho, cliente, usuário, itens e pagamentos.
- `CompraDAO.buscarPorCodigo`: carrega cabeçalho, fornecedor, usuário e itens.
- `RelatorioDAO.vendasPorPeriodo`: agrupa vendas por data com `COUNT` e `SUM`.
- `RelatorioDAO.produtosMaisVendidos`: agrupa itens por produto e ordena pela quantidade vendida.

### Tratamento de erros

Os DAOs capturam `SQLException` e lançam `DAOException`. Isso evita que detalhes de JDBC se espalhem pelas telas e fornece mensagens relacionadas à operação executada.

## 6. ConnectionFactory e Supabase

`ConnectionFactory` centraliza a abertura de conexões. Ela lê o `config.properties`, carrega o driver PostgreSQL, configura SSL e chama `DriverManager.getConnection`.

Para o pooler transacional do Supabase, a propriedade `prepareThreshold=0` evita conflito de prepared statements no PgBouncer. Os DAOs não precisam conhecer URL, usuário ou senha: apenas chamam `ConnectionFactory.getConnection()`.

## 7. Banco de dados

Principais tabelas:

- `pessoa`: dados gerais de pessoas físicas ou jurídicas.
- `cliente`: especialização de pessoa e limite de crédito.
- `fornecedor`: especialização de pessoa e contato.
- `usuario`: login, senha, situação e perfil administrador.
- `produto`: preços, custo, estoque, unidade e situação.
- `formapagto`: formas de pagamento.
- `venda`, `venda_produto`, `venda_pagto`: cabeçalho, itens e pagamentos.
- `compra`, `compra_produto`: cabeçalho e itens.

As chaves estrangeiras mantêm a integridade entre as tabelas. Itens e pagamentos usam exclusão em cascata quando o movimento principal é removido.

## 8. Segurança e permissões

`Sessao` guarda o usuário autenticado durante a execução. `Permissao.isAdmin()` consulta o campo `usu_admin`. Antes de excluir dados, as telas chamam `Permissao.podeExcluir(...)`. Usuários comuns recebem uma mensagem de permissão negada.

Essa autorização na aplicação atende ao requisito funcional. Em um sistema de produção, seria recomendável também aplicar políticas no banco e armazenar senhas com hash, nunca em texto puro.

## 9. Recursos auxiliares

- `Tema`: instala FlatLaf, alterna tema e padroniza botões e tabelas.
- `Mascaras`: formata CPF/CNPJ, telefone, data e moeda brasileira.
- `ValidadorDocumento`: valida dígitos verificadores de CPF e CNPJ.
- `Sessao`: mantém o usuário logado.
- Filtros das tabelas: usam `TableRowSorter` e `RowFilter`.

## 10. Roteiro para apresentação

1. Explicar o objetivo do ERP e as tecnologias.
2. Mostrar a divisão em Model, View, Controller e DAO.
3. Abrir `GenericDAO` e explicar o contrato CRUD.
4. Abrir `ClienteDAO` e demonstrar a transação Pessoa + Cliente.
5. Abrir `VendaDAO` e demonstrar cabeçalho, itens, pagamentos e estoque no mesmo commit.
6. Mostrar `ConnectionFactory` e a configuração externa.
7. Executar login e os principais cadastros.
8. Registrar compra e venda e verificar o estoque.
9. Abrir histórico, detalhes e relatórios.
10. Demonstrar que usuário comum não pode excluir.

## 11. Conclusão

O trabalho demonstra orientação a objetos, interface desktop, banco relacional e separação de responsabilidades. O padrão DAO é essencial porque organiza todo o acesso ao PostgreSQL, reduz o acoplamento e permite que regras e telas utilizem objetos de domínio sem conhecer os detalhes do SQL. As transações em Cliente, Fornecedor, Compra e Venda preservam a consistência do banco, especialmente quando uma operação depende de várias tabelas.
