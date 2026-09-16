# MarketPlace System — ERP de Compra e Venda (Java + SQL)

Sistema **ERP de compra e venda** desenvolvido em **Java Swing** com persistência em
**PostgreSQL (Supabase)** via **JDBC**, aplicando os padrões de projeto **MVC** e **DAO**.

Trabalho da disciplina *Práticas de Programação Orientada a Objetos* — UNIP (integrado com LPBD),
baseado no DER fornecido pelo professor.

---

## ✨ Funcionalidades

- **Tela de Login** com autenticação de usuário.
- **Menu principal** (janela MDI) com os grupos:
  - **Cadastros:** Cliente, Fornecedor, Forma de Pagamento, Usuário, Produto
  - **Movimentos:** Venda (com produtos e formas de pagamento), Compra (com produtos)
  - **Sair:** trocar usuário / encerrar
- CRUD completo dos cadastros.
- **Venda**: seleção de cliente, adição de itens e formas de pagamento, cálculo de total e
  **baixa automática de estoque**.
- **Compra**: seleção de fornecedor, adição de itens e **entrada automática de estoque**.
- Operações de venda/compra são **transacionais** (cabeçalho + itens + estoque em um único commit).
- **Histórico de Vendas e Compras** (menu Movimentos): lista os movimentos realizados e permite excluí-los.
- **Controle de permissão por perfil**: o cadastro de usuário tem a opção **"Perfil administrador"**.
  Apenas administradores podem **excluir** dados do sistema; um usuário comum que tente excluir recebe
  uma mensagem informando a falta de permissão.
- **Máscaras** de CPF/CNPJ e telefone nos cadastros de Cliente e Fornecedor, e **valores em R$**
  (formato brasileiro) nas telas de produtos, vendas e compras.
- **Tema claro/escuro** alternável em tempo real pelo menu **Exibir**.

---

## 🏗️ Arquitetura (MVC + DAO)

```
src/main/java/br/unip/erp/
├── Main.java                 # Ponto de entrada
├── model/                    # Entidades (POJOs) — camada Model
│   ├── Pessoa, Cliente, Fornecedor, Usuario, Produto, FormaPagamento
│   └── Venda, VendaProduto, VendaPagamento, Compra, CompraProduto
├── dao/                      # Data Access Object (JDBC / PreparedStatement)
│   ├── GenericDAO<T>         # Contrato CRUD genérico
│   ├── UsuarioDAO, ProdutoDAO, FormaPagamentoDAO
│   ├── PessoaDAO             # Auxiliar (transacional) de Cliente/Fornecedor
│   ├── ClienteDAO, FornecedorDAO
│   └── VendaDAO, CompraDAO   # Transacionais (itens + estoque)
├── controller/               # Regras de negócio — camada Controller
│   └── *Controller.java
├── view/                     # Interface Swing — camada View
│   ├── LoginView, MenuView
│   └── ClienteView, FornecedorView, FormaPagamentoView,
│       UsuarioView, ProdutoView, VendaView, CompraView
└── util/
    ├── ConnectionFactory     # Fábrica de conexões JDBC
    └── Sessao                # Usuário autenticado

database/
└── schema.sql                # Script de criação das tabelas (PostgreSQL)
```

---

## 🗄️ Banco de Dados

O `database/schema.sql` cria as **11 tabelas** do DER, adaptadas de Oracle para PostgreSQL:

`pessoa`, `cliente`, `fornecedor`, `usuario`, `produto`, `formapagto`,
`venda`, `venda_produto`, `venda_pagto`, `compra`, `compra_produto`.

Também insere um usuário administrador padrão e algumas formas de pagamento:

| Usuário padrão | Senha  |
|----------------|--------|
| `admin`        | `admin`|

### Aplicando o schema no Supabase

No painel do Supabase → **SQL Editor**, cole o conteúdo de `database/schema.sql` e execute.

---

## ⚙️ Configuração da conexão

As credenciais **não ficam no código**. Edite `src/main/resources/config.properties`:

```properties
# --- Opção 1: POOLER (recomendado, funciona em IPv4) ---
db.url=jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres
db.user=postgres.<PROJECT_REF>
db.password=SUA_SENHA_AQUI
db.ssl=true

# --- Opção 2: Conexão DIRETA (requer IPv6) ---
# db.url=jdbc:postgresql://db.<PROJECT_REF>.supabase.co:5432/postgres
# db.user=postgres
```

> **Nota técnica:** o *transaction pooler* do Supabase (porta `6543`, baseado em PgBouncer)
> não suporta *prepared statements* no servidor. Por isso a `ConnectionFactory` define
> `prepareThreshold=0`, que força o modo *simple query* do driver JDBC. Isso é transparente
> e inofensivo também na conexão direta.

> ⚠️ **Segurança:** nunca faça commit da senha real. O arquivo versionado é um *template*.

---

## ▶️ Como executar

Pré-requisitos: **JDK 17+** e **Maven 3.8+**.

```bash
# Compilar
mvn clean compile

# Executar diretamente
mvn exec:java

# Ou gerar o JAR executável (fat jar, com o driver embutido) e rodar
mvn clean package
java -jar target/marketplace-erp.jar
```

Ao iniciar, informe **admin / admin** na tela de login.

---

## 🎨 Interface

O sistema usa a biblioteca [**FlatLaf**](https://www.formdev.com/flatlaf/) para um visual
moderno com **tema escuro** (flat design, cantos arredondados, cores de acento). O tema é
centralizado na classe `util/Tema.java` e aplicado em `Main.java` antes de abrir as telas.

## 🧰 Tecnologias

- Java 17 (Swing)
- [FlatLaf 3.7.1](https://www.formdev.com/flatlaf/) — Look & Feel moderno (tema escuro)
- Maven
- PostgreSQL 17 (Supabase)
- Driver JDBC `org.postgresql:postgresql`
- Padrões: **MVC**, **DAO**
