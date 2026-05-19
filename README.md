![GitHub Repo Banner](https://ghrb.waren.build/banner?header=%21%5Bgithub%5D+HelpDesk+TI&subheader=Sistema+de+Chamados+de+Suporte+T%C3%A9cnico&bg=431586-9231A8&color=FFFFFF&headerfont=Bitcount+Prop+Single&subheaderfont=Zen+Dots&watermarkpos=bottom-right)

Sistema de simulação de atendimento de chamados de suporte técnico desenvolvido em **Java**, utilizando uma **API REST** no backend e uma **interface web moderna** no frontend.

O principal objetivo do projeto é demonstrar, de forma prática, a aplicação das principais **Estruturas de Dados** em um cenário real de suporte técnico de TI.
---

## 👥 Integrantes

- Breno Henrique Ruiz dos Santos — 823131791  
- Maria Eduarda Medeiro Porto — 824144948  
- Matheus Alves Santana — 824144952  
---

# 📌 Objetivo do Projeto

O sistema foi desenvolvido como projeto acadêmico da disciplina de **Estrutura de Dados e Análise de Algoritmos**, com foco na implementação manual das seguintes estruturas:

- Fila (Queue)
- Pilha (Stack)
- Lista Ligada (Linked List)
- Árvore Binária de Busca (BST)

---

# 🖥️ Funcionalidades do Sistema

✅ Cadastro de usuários  
✅ Cadastro de equipamentos  
✅ Abertura de chamados técnicos  
✅ Atendimento de chamados em ordem FIFO  
✅ Finalização de chamados  
✅ Histórico de operações  
✅ Busca eficiente por ID utilizando árvore binária  
✅ Dashboard com estatísticas gerais  

---

# 🏗️ Estrutura do Projeto

```bash
help_desk_web_main/
├── backend/
│   ├── src/
│   │   └── helpdesk/
│   │       ├── api/
│   │       │   └── HelpDeskAPI.java
│   │       ├── estruturas/
│   │       │   ├── Fila.java
│   │       │   ├── Pilha.java
│   │       │   ├── ListaLigada.java
│   │       │   └── ArvoreBinaria.java
│   │       ├── modelo/
│   │       │   ├── Chamado.java
│   │       │   ├── Usuario.java
│   │       │   ├── Equipamento.java
│   │       │   └── RegistroHistorico.java
│   │       └── util/
│   │           └── JsonParser.java
│   │
│   ├── compilar.bat
│   └── executar.bat
│
├── frontend/
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js
│
└── README.md
```

---

# ⚙️ Estruturas de Dados Utilizadas

## 🔹 Fila (Queue) — FIFO

**Arquivo:** `Fila.java`

Responsável por organizar os chamados aguardando atendimento.

### Operações implementadas

```java
enfileirar()
desenfileirar()
espiar()
estaVazia()
getTamanho()
paraArray()
```

### Aplicação no sistema

```java
filaChamados.enfileirar(chamado);
filaChamados.desenfileirar();
```

---

## 🔹 Pilha (Stack) — LIFO

**Arquivo:** `Pilha.java`

Responsável pelo histórico de operações do sistema.

### Operações implementadas

```java
empilhar()
desempilhar()
espiar()
estaVazia()
getTamanho()
paraArray()
```

### Aplicação no sistema

```java
pilhaHistorico.empilhar(registro);
```

---

## 🔹 Lista Ligada (Linked List)

**Arquivo:** `ListaLigada.java`

Utilizada para armazenar:

- Usuários
- Equipamentos
- Chamados

### Operações implementadas

```java
adicionar()
obter()
remover()
buscar()
filtrar()
paraArray()
```

### Aplicação no sistema

```java
listaUsuarios.adicionar(usuario);
listaChamados.filtrar(...);
```

---

## 🔹 Árvore Binária de Busca (BST)

**Arquivo:** `ArvoreBinaria.java`

Responsável pela busca eficiente de chamados por ID.

### Operações implementadas

```java
inserir()
buscar()
remover()
emOrdem()
preOrdem()
```

### Aplicação no sistema

```java
arvoreChamados.inserir(chamado);
arvoreChamados.buscar(chave);
```

---

# 🚀 Funcionalidades e Estruturas Utilizadas

| Funcionalidade | Estruturas Utilizadas |
|---|---|
| Criar chamado | Lista Ligada, Fila, Árvore Binária, Pilha |
| Atender chamado | Fila, Pilha |
| Finalizar chamado | Árvore Binária, Pilha |
| Buscar por ID | Árvore Binária |
| Buscar por termo | Lista Ligada |
| Histórico de operações | Pilha |
| Dashboard | Todas as estruturas |
| Cadastro de usuários | Lista Ligada |
| Cadastro de equipamentos | Lista Ligada |

---

# 🧪 Tecnologias Utilizadas

## Backend
- Java
- API REST

## Frontend
- HTML5
- CSS3
- JavaScript Vanilla
- Fetch API

---

# ▶️ Como Executar o Projeto

## ✅ Pré-requisitos

- Java JDK 25
- VS Code
- Extensão Live Server

---

## 1️⃣ Compilar o Backend

```bash
cd backend
compilar.bat
```

---

## 2️⃣ Executar o Backend

```bash
cd backend
executar.bat
```

A API ficará disponível em:

```bash
http://localhost:8080
```

---

## 3️⃣ Executar o Frontend

1. Abra a pasta `frontend` no VS Code
2. Clique com o botão direito em `index.html`
3. Selecione:

```bash
Open with Live Server
```

---

# 🔌 Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/estatisticas` | Estatísticas do dashboard |
| GET | `/api/chamados` | Lista chamados |
| GET | `/api/chamados/{id}` | Busca chamado por ID |
| POST | `/api/chamados` | Cria chamado |
| POST | `/api/chamados/atender/{id}` | Inicia atendimento |
| POST | `/api/chamados/finalizar/{id}` | Finaliza chamado |
| GET | `/api/fila` | Lista fila de atendimento |
| GET | `/api/historico` | Lista histórico |
| GET | `/api/usuarios` | Lista usuários |
| POST | `/api/usuarios` | Cria usuário |
| DELETE | `/api/usuarios/{id}` | Remove usuário |
| GET | `/api/equipamentos` | Lista equipamentos |
| POST | `/api/equipamentos` | Cria equipamento |
| DELETE | `/api/equipamentos/{id}` | Remove equipamento |
| GET | `/api/busca?id={id}` | Busca na árvore |
| GET | `/api/busca?termo={texto}` | Busca textual |
| GET | `/api/busca` | Listagem em ordem |

---

# 📊 Complexidade das Estruturas

| Estrutura | Operação | Complexidade |
|---|---|---|
| Fila | Inserção/Remoção | O(1) |
| Pilha | Inserção/Remoção | O(1) |
| Lista Ligada | Busca | O(n) |
| Árvore Binária | Busca média | O(log n) |

---

# 📚 Considerações Finais

Este projeto demonstra de forma prática como estruturas de dados clássicas podem ser utilizadas em um cenário real de desenvolvimento de software.

Cada estrutura foi escolhida de acordo com sua finalidade:

- **Fila** → controle de atendimento
- **Pilha** → histórico de ações
- **Lista Ligada** → armazenamento dinâmico
- **Árvore Binária** → busca eficiente


---
