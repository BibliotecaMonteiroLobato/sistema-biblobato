# 📚 Biblioteca Monteiro Lobato

> Sistema web para gerenciamento de biblioteca com controle de empréstimos, devoluções e administração de usuários e acervo.

## 🎯 Visão Geral

O **Sistema Biblioteca Monteiro Lobato** é uma aplicação administrativa que permite o controle completo de uma biblioteca, incluindo gerenciamento de usuários, livros, empréstimos e devoluções. O sistema foi projetado para facilitar o trabalho de bibliotecários e administradores de acervos.

## ✨ Funcionalidades

### 🔐 Autenticação
- **Login seguro** para administradores

### 👥 Gestão de Usuários
- **📋 Listar usuários**: Visualização de todos os usuários cadastrados
- **➕ Cadastrar usuário**: Formulário para adicionar novos usuários
- **🗑️ Deletar usuário**: Remoção de usuários com confirmação

### 📖 Gestão de Livros
- **📋 Listar livros**: Catálogo completo do acervo
- **➕ Cadastrar livro**: Adição de novos títulos ao acervo
- **🗑️ Deletar livro**: Remoção de livros com confirmação

### 📋 Gestão de Empréstimos
- **📋 Listar empréstimos**: Visualização de todos os empréstimos ativos e histórico
- **➕ Cadastrar empréstimo**: Registro de novos empréstimos
- **🗑️ Deletar empréstimo**: Cancelamento de empréstimos
- **🔄 Controle de status**: Switch button para marcar livros como devolvidos/não devolvidos

### 🔄 Gestão de Devoluções
- **📋 Listar devoluções**: Histórico completo com ID do livro, nome do cliente e data
- **➕ Cadastrar devolução**: Registro manual de devoluções
- **🗑️ Deletar devolução**: Remoção de registros de devolução

---

## 🏗️ Arquitetura

O sistema segue o padrão **CRUD** (Create, Read, Update, Delete) para todas as entidades principais:

```
📚 Sistema Biblioteca Monteiro Lobato
├── 🔐 Autenticação
├── 👥 Módulo Usuários (CRUD)
├── 📖 Módulo Livros (CRUD)  
├── 📋 Módulo Empréstimos (CRUD + Status)
└── 🔄 Módulo Devoluções (CRUD)
```

## 📊 Modelagem de Processos

O sistema foi modelado utilizando **BPMN** (Business Process Model and Notation), documentando todos os fluxos de trabalho e decisões do usuário. O diagrama BPMN completo está disponível na documentação do projeto.

### 🚀 Fluxo Principal
1. **🔐 Login** → Validação de credenciais
2. **🏠 Menu Principal** → Escolha da funcionalidade
3. **⚙️ Módulos** → Operações CRUD específicas
4. **🚪 Logout** → Encerramento da sessão

---

## 🔧 Tecnologias Utilizadas

| Categoria | Tecnologia |
|-----------|------------|
| **Frontend** | Thymeleaf (Template Engine) |
| **Backend** | Java 17+ com Spring Boot |
| **Banco de Dados** | MongoDB Atlas |
| **Autenticação** | Spring Security |
| **Gerenciamento** | Maven |

---
