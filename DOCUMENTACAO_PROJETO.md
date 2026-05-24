# Sistema de Help Desk TI - Explicacao Completa do Projeto

## Visao Geral

Este projeto e um **sistema de simulacao de atendimento de chamados de suporte tecnico de informatica**, desenvolvido em **Java** (backend REST API) com **interface web** (HTML/CSS/JavaScript). O sistema permite o registro, organizacao, atendimento e consulta de chamados tecnicos, aplicando os principais conceitos de **Estrutura de Dados**.

---

## Arquitetura do Sistema

```
help_desk_web_main/
├── backend/                          # API REST em Java
│   ├── src/
│   │   └── helpdesk/
│   │       ├── api/
│   │       │   └── HelpDeskAPI.java          # Servidor HTTP e endpoints REST
│   │       ├── estruturas/
│   │       │   ├── Fila.java                 # Implementacao de Fila (Queue)
│   │       │   ├── Pilha.java                # Implementacao de Pilha (Stack)
│   │       │   ├── ListaLigada.java          # Implementacao de Lista Ligada
│   │       │   └── ArvoreBinaria.java        # Implementacao de Arvore Binaria de Busca
│   │       ├── modelo/
│   │       │   ├── Chamado.java              # Modelo de chamado tecnico
│   │       │   ├── Usuario.java              # Modelo de usuario
│   │       │   ├── Equipamento.java          # Modelo de equipamento
│   │       │   └── RegistroHistorico.java    # Modelo de registro de historico
│   │       └── util/
│   │           └── JsonParser.java           # Parser JSON simples
│   ├── compilar.bat                          # Script de compilacao
│   └── executar.bat                          # Script de execucao
├── frontend/                         # Interface Web
│   ├── index.html                    # Pagina principal
│   ├── css/
│   │   └── style.css                 # Estilos da interface
│   └── js/
│       └── app.js                    # Logica da interface (fetch API)
└── EXPLICACAO_PROJETO.md             # Este arquivo
```

---

## Estruturas de Dados Utilizadas

### 1. Fila (Queue) - FIFO (First In, First Out)

**Arquivo:** `backend/src/helpdesk/estruturas/Fila.java`

**Uso no sistema:** Organizar os chamados que estao **aguardando atendimento**. Quando um chamado e criado, ele entra na fila. O primeiro chamado a entrar e o primeiro a ser atendido, respeitando a ordem de chegada.

**Como funciona:**
- Quando um chamado e **criado**, ele e adicionado ao final da fila (`enfileirar()`).
- Quando um tecnico inicia o **atendimento**, o chamado e removido do inicio da fila (`desenfileirar()`).
- O proximo chamado a ser atendido pode ser consultado sem remocao (`espiar()`).

**Operacoes implementadas:**
- `enfileirar(T dado)` - Adiciona elemento ao final da fila
- `desenfileirar()` - Remove e retorna o elemento do inicio da fila
- `espiar()` - Retorna o elemento do inicio sem remover
- `estaVazia()` - Verifica se a fila esta vazia
- `getTamanho()` - Retorna o numero de elementos
- `paraArray()` - Converte a fila para array

**Onde e usado no codigo:**
```java
// Em HelpDeskAPI.java
private static final Fila<Chamado> filaChamados = new Fila<>();

// Ao criar chamado:
filaChamados.enfileirar(chamado);

// Ao iniciar atendimento:
Chamado c = filaChamados.desenfileirar();
```

**Tela na interface:** Menu "Fila de Atendimento"

---

### 2. Pilha (Stack) - LIFO (Last In, First Out)

**Arquivo:** `backend/src/helpdesk/estruturas/Pilha.java`

**Uso no sistema:** Registrar o **historico de operacoes** realizadas no sistema. Cada criacao, atendimento e finalizacao de chamado e empilhada. A operacao mais recente fica no topo.

**Como funciona:**
- Quando uma operacao e realizada (criacao, atendimento, finalizacao), um registro e adicionado ao topo da pilha (`empilhar()`).
- O historico e exibido na ordem inversa (mais recente primeiro), caracteristica natural da pilha (LIFO).

**Operacoes implementadas:**
- `empilhar(T dado)` - Adiciona elemento ao topo da pilha
- `desempilhar()` - Remove e retorna o elemento do topo
- `espiar()` - Retorna o elemento do topo sem remover
- `estaVazia()` - Verifica se a pilha esta vazia
- `getTamanho()` - Retorna o numero de elementos
- `paraArray()` - Converte a pilha para array

**Onde e usado no codigo:**
```java
// Em HelpDeskAPI.java
private static final Pilha<RegistroHistorico> pilhaHistorico = new Pilha<>();

// Ao criar chamado:
pilhaHistorico.empilhar(new RegistroHistorico("CRIACAO", ...));

// Ao iniciar atendimento:
pilhaHistorico.empilhar(new RegistroHistorico("ATENDIMENTO", ...));

// Ao finalizar chamado:
pilhaHistorico.empilhar(new RegistroHistorico("FINALIZACAO", ...));
```

**Tela na interface:** Menu "Historico" e secao "Ultimas Operacoes" no Dashboard

---

### 3. Lista Ligada (Linked List)

**Arquivo:** `backend/src/helpdesk/estruturas/ListaLigada.java`

**Uso no sistema:** Armazenar **usuarios**, **equipamentos** e **todos os chamados** do sistema. Permite insercao, remocao e busca dinamica sem tamanho fixo.

**Como funciona:**
- Cada novo usuario, equipamento ou chamado e adicionado ao final da lista (`adicionar()`).
- E possivel buscar por criterios especificos usando filtros (`buscar()`, `filtrar()`).
- Itens podem ser removidos da lista (`remover()`).

**Operacoes implementadas:**
- `adicionar(T dado)` - Adiciona elemento ao final da lista
- `obter(int indice)` - Retorna elemento pelo indice
- `remover(T dado)` - Remove elemento por igualdade
- `removerPorIndice(int indice)` - Remove elemento pelo indice
- `buscar(Filtro<T> filtro)` - Busca primeiro elemento que atende ao filtro
- `filtrar(Filtro<T> filtro)` - Retorna nova lista com elementos filtrados
- `getTamanho()` - Retorna o numero de elementos
- `paraArray()` - Converte a lista para array

**Onde e usado no codigo:**
```java
// Em HelpDeskAPI.java
private static final ListaLigada<Usuario> listaUsuarios = new ListaLigada<>();
private static final ListaLigada<Equipamento> listaEquipamentos = new ListaLigada<>();
private static final ListaLigada<Chamado> listaChamados = new ListaLigada<>();

// Busca de usuario por ID:
Usuario u = listaUsuarios.buscar(usr -> usr.getId() == id);

// Busca de chamados por termo:
ListaLigada<Chamado> resultados = listaChamados.filtrar(c ->
    c.getTitulo().toLowerCase().contains(termo));
```

**Tela na interface:** Menus "Usuarios", "Equipamentos" e "Chamados"

---

### 4. Arvore Binaria de Busca (Binary Search Tree - BST)

**Arquivo:** `backend/src/helpdesk/estruturas/ArvoreBinaria.java`

**Uso no sistema:** Organizar e **buscar chamados por ID** de forma eficiente. A arvore binaria permite busca com complexidade O(log n) no caso medio, muito mais eficiente que busca linear.

**Como funciona:**
- Ao criar um chamado, ele e inserido na arvore binaria (`inserir()`).
- Ao buscar um chamado por ID, a arvore e percorrida de forma binaria (`buscar()`).
- A travessia em ordem (in-order) exibe os chamados ordenados por ID.

**Operacoes implementadas:**
- `inserir(T dado)` - Insere elemento na posicao correta da arvore
- `buscar(T chave)` - Busca elemento na arvore
- `remover(T dado)` - Remove elemento da arvore
- `emOrdem(Visitante<T>)` - Percorre a arvore em ordem (esquerda, raiz, direita)
- `preOrdem(Visitante<T>)` - Percorre a arvore em pre-ordem (raiz, esquerda, direita)
- `getTamanho()` - Retorna o numero de elementos
- `estaVazia()` - Verifica se a arvore esta vazia

**Onde e usado no codigo:**
```java
// Em HelpDeskAPI.java
private static final ArvoreBinaria<Chamado> arvoreChamados = new ArvoreBinaria<>();

// Ao criar chamado:
arvoreChamados.inserir(chamado);

// Busca por ID:
Chamado chave = new Chamado();
chave.setId(id);
Chamado c = arvoreChamados.buscar(chave);

// Listagem em ordem:
arvoreChamados.emOrdem(c -> lista.adicionar(c.toJson()));
```

**Tela na interface:** Menu "Busca (Arvore)" com busca por ID e listagem em ordem

---

## Funcionalidades do Sistema

| Funcionalidade | Descricao | Estrutura(s) Utilizada(s) |
|---|---|---|
| Criar chamado | Registra novo chamado tecnico | Lista Ligada, Fila, Arvore Binaria, Pilha |
| Atender chamado | Retira o proximo da fila para atendimento | Fila, Pilha |
| Finalizar chamado | Conclui chamado com solucao aplicada | Arvore Binaria, Pilha |
| Listar chamados | Exibe todos os chamados cadastrados | Lista Ligada |
| Buscar por ID | Busca eficiente de chamado por codigo | Arvore Binaria |
| Buscar por termo | Filtra chamados por texto | Lista Ligada |
| Listar em ordem | Exibe chamados ordenados (in-order traversal) | Arvore Binaria |
| Ver fila | Mostra chamados aguardando atendimento | Fila |
| Ver historico | Mostra operacoes realizadas (mais recente primeiro) | Pilha |
| Cadastrar usuario | Registra novo usuario no sistema | Lista Ligada |
| Cadastrar equipamento | Registra novo equipamento | Lista Ligada |
| Dashboard | Visao geral com estatisticas | Todas as estruturas |

---

## Como Executar

### Pre-requisitos
- **Java JDK 8+** instalado
- **Navegador web** moderno (Chrome, Firefox, Edge)
- **Live Server** (extensao do VS Code) para o frontend

### Passo 1: Compilar o Backend
```
cd backend
.\compilar.bat
```

### Passo 2: Executar o Backend
```
cd backend
.\executar.bat
```
A API estara disponivel em `http://localhost:8080`

### Passo 3: Abrir o Frontend
1. Abra a pasta `frontend` no VS Code
2. Clique com botao direito no arquivo `index.html`
3. Selecione "Open with Live Server"
4. A interface abrira no navegador

---

## Endpoints da API REST

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | /api/estatisticas | Estatisticas gerais do dashboard |
| GET | /api/chamados | Lista todos os chamados |
| GET | /api/chamados/{id} | Busca chamado por ID |
| POST | /api/chamados | Cria novo chamado |
| POST | /api/chamados/atender/{id} | Inicia atendimento do chamado |
| POST | /api/chamados/finalizar/{id} | Finaliza chamado com solucao |
| GET | /api/fila | Lista chamados na fila |
| GET | /api/historico | Lista historico de operacoes |
| GET | /api/usuarios | Lista todos os usuarios |
| POST | /api/usuarios | Cria novo usuario |
| DELETE | /api/usuarios/{id} | Remove usuario |
| GET | /api/equipamentos | Lista todos os equipamentos |
| POST | /api/equipamentos | Cria novo equipamento |
| DELETE | /api/equipamentos/{id} | Remove equipamento |
| GET | /api/busca?id={id} | Busca na arvore binaria por ID |
| GET | /api/busca?termo={texto} | Busca por termo na lista ligada |
| GET | /api/busca | Listagem em ordem pela arvore |

---

## Tecnologias Utilizadas

- **Java** (com.sun.net.httpserver) - API REST sem dependencias externas
- **HTML5 / CSS3** - Interface web responsiva
- **JavaScript (Vanilla)** - Logica do frontend com Fetch API
- **Font Awesome** - Icones
- **Google Fonts (Inter)** - Tipografia

---

## Consideracoes Finais

Este projeto demonstra de forma pratica a aplicacao das quatro principais estruturas de dados em um cenario real de suporte tecnico:

1. **Fila**: Garante justica no atendimento (primeiro a abrir, primeiro a ser atendido)
2. **Pilha**: Mantem historico de acoes, sempre mostrando a mais recente primeiro
3. **Lista Ligada**: Oferece flexibilidade no armazenamento de colecoes dinamicas
4. **Arvore Binaria**: Proporciona busca eficiente por identificadores

Todas as estruturas foram implementadas **do zero**, sem uso de colecoes prontas do Java (como ArrayList, LinkedList, Stack, Queue, TreeMap), evidenciando o aprendizado e compreensao dos algoritmos fundamentais.
