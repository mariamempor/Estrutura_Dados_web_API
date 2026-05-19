package helpdesk.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import helpdesk.estruturas.*;
import helpdesk.modelo.*;
import helpdesk.util.JsonParser;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * API REST principal do sistema de Help Desk.
 * Utiliza com.sun.net.httpserver (nativo do Java) para simplicidade educacional.
 */
public class HelpDeskAPI {

    // ==================== ESTRUTURAS DE DADOS ====================

    // FILA: Chamados aguardando atendimento (FIFO)
    private static final Fila<Chamado> filaChamados = new Fila<>();

    // PILHA: Histórico de operações realizadas (LIFO)
    private static final Pilha<RegistroHistorico> pilhaHistorico = new Pilha<>();

    // LISTA LIGADA: Armazenamento de usuários, equipamentos e todos os chamados
    private static final ListaLigada<Usuario> listaUsuarios = new ListaLigada<>();
    private static final ListaLigada<Equipamento> listaEquipamentos = new ListaLigada<>();
    private static final ListaLigada<Chamado> listaChamados = new ListaLigada<>();

    // ÁRVORE BINÁRIA: Busca rápida de chamados por ID
    private static final ArvoreBinaria<Chamado> arvoreChamados = new ArvoreBinaria<>();

    // Contadores para IDs auto-incrementais
    private static int proximoIdUsuario = 1;
    private static int proximoIdEquipamento = 1;
    private static int proximoIdChamado = 1;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) throws IOException {
        carregarDadosIniciais();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoints de Usuários
        server.createContext("/api/usuarios", HelpDeskAPI::handleUsuarios);

        // Endpoints de Equipamentos
        server.createContext("/api/equipamentos", HelpDeskAPI::handleEquipamentos);

        // Endpoints de Chamados
        server.createContext("/api/chamados", HelpDeskAPI::handleChamados);

        // Endpoint da Fila de Atendimento
        server.createContext("/api/fila", HelpDeskAPI::handleFila);

        // Endpoint do Histórico (Pilha)
        server.createContext("/api/historico", HelpDeskAPI::handleHistorico);

        // Endpoint de Estatísticas (Dashboard)
        server.createContext("/api/estatisticas", HelpDeskAPI::handleEstatisticas);

        // Endpoint de Busca na Árvore
        server.createContext("/api/busca", HelpDeskAPI::handleBusca);

        server.setExecutor(null);
        server.start();
        System.out.println("===========================================");
        System.out.println("  Help Desk API rodando em http://localhost:8080");
        System.out.println("===========================================");
    }

    // ==================== DADOS INICIAIS ====================
    private static void carregarDadosIniciais() {
        // Usuários de exemplo
        criarUsuario("Carlos Silva", "carlos@empresa.com", "TI", "Analista");
        criarUsuario("Maria Souza", "maria@empresa.com", "RH", "Gerente");
        criarUsuario("Joao Santos", "joao@empresa.com", "Financeiro", "Assistente");
        criarUsuario("Ana Lima", "ana@empresa.com", "Marketing", "Coordenadora");

        // Equipamentos de exemplo
        criarEquipamento("Notebook", "Dell", "PAT-001", "TI");
        criarEquipamento("Desktop", "HP", "PAT-002", "RH");
        criarEquipamento("Impressora", "Epson", "PAT-003", "Financeiro");
        criarEquipamento("Monitor", "Samsung", "PAT-004", "Marketing");

        System.out.println("Dados iniciais carregados com sucesso!");
    }

    // ==================== HANDLERS ====================

    private static void handleUsuarios(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                // GET /api/usuarios ou /api/usuarios/{id}
                String idStr = extractIdFromPath(path, "/api/usuarios/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Usuario u = listaUsuarios.buscar(usr -> usr.getId() == id);
                    if (u != null) {
                        sendResponse(exchange, 200, u.toJson());
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Usuario nao encontrado\"}");
                    }
                } else {
                    int tam = listaUsuarios.getTamanho();
                    String[] jsons = new String[tam];
                    for (int i = 0; i < tam; i++) {
                        jsons[i] = listaUsuarios.obter(i).toJson();
                    }
                    sendResponse(exchange, 200, JsonParser.toJsonArray(jsons));
                }
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> dados = JsonParser.parse(body);
                Usuario u = criarUsuario(
                    dados.getOrDefault("nome", ""),
                    dados.getOrDefault("email", ""),
                    dados.getOrDefault("setor", ""),
                    dados.getOrDefault("cargo", "")
                );
                sendResponse(exchange, 201, u.toJson());
            } else if ("DELETE".equals(method)) {
                String idStr = extractIdFromPath(path, "/api/usuarios/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Usuario u = listaUsuarios.buscar(usr -> usr.getId() == id);
                    if (u != null) {
                        listaUsuarios.remover(u);
                        sendResponse(exchange, 200, "{\"mensagem\":\"Usuario removido\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Usuario nao encontrado\"}");
                    }
                } else {
                    sendResponse(exchange, 400, "{\"erro\":\"ID nao informado\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleEquipamentos(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                String idStr = extractIdFromPath(path, "/api/equipamentos/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Equipamento e = listaEquipamentos.buscar(eq -> eq.getId() == id);
                    if (e != null) {
                        sendResponse(exchange, 200, e.toJson());
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Equipamento nao encontrado\"}");
                    }
                } else {
                    int tam = listaEquipamentos.getTamanho();
                    String[] jsons = new String[tam];
                    for (int i = 0; i < tam; i++) {
                        jsons[i] = listaEquipamentos.obter(i).toJson();
                    }
                    sendResponse(exchange, 200, JsonParser.toJsonArray(jsons));
                }
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> dados = JsonParser.parse(body);
                Equipamento e = criarEquipamento(
                    dados.getOrDefault("tipo", ""),
                    dados.getOrDefault("marca", ""),
                    dados.getOrDefault("patrimonio", ""),
                    dados.getOrDefault("setor", "")
                );
                sendResponse(exchange, 201, e.toJson());
            } else if ("DELETE".equals(method)) {
                String idStr = extractIdFromPath(path, "/api/equipamentos/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Equipamento e = listaEquipamentos.buscar(eq -> eq.getId() == id);
                    if (e != null) {
                        listaEquipamentos.remover(e);
                        sendResponse(exchange, 200, "{\"mensagem\":\"Equipamento removido\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Equipamento nao encontrado\"}");
                    }
                } else {
                    sendResponse(exchange, 400, "{\"erro\":\"ID nao informado\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleChamados(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            // POST /api/chamados/atender/{id} - Iniciar atendimento
            if ("POST".equals(method) && path.contains("/atender/")) {
                String idStr = extractIdFromPath(path, "/api/chamados/atender/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    atenderChamado(id, exchange);
                    return;
                }
            }

            // POST /api/chamados/finalizar/{id} - Finalizar chamado
            if ("POST".equals(method) && path.contains("/finalizar/")) {
                String idStr = extractIdFromPath(path, "/api/chamados/finalizar/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    String body = readBody(exchange);
                    Map<String, String> dados = JsonParser.parse(body);
                    finalizarChamado(id, dados.getOrDefault("solucao", ""), dados.getOrDefault("tecnico", ""), exchange);
                    return;
                }
            }

            if ("GET".equals(method)) {
                String idStr = extractIdFromPath(path, "/api/chamados/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    // Busca na Árvore Binária
                    Chamado chave = new Chamado();
                    chave.setId(id);
                    Chamado c = arvoreChamados.buscar(chave);
                    if (c != null) {
                        sendResponse(exchange, 200, c.toJson());
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Chamado nao encontrado\"}");
                    }
                } else {
                    // Listar todos os chamados da Lista Ligada
                    int tam = listaChamados.getTamanho();
                    String[] jsons = new String[tam];
                    for (int i = 0; i < tam; i++) {
                        jsons[i] = listaChamados.obter(i).toJson();
                    }
                    sendResponse(exchange, 200, JsonParser.toJsonArray(jsons));
                }
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> dados = JsonParser.parse(body);

                int prioridade = 3;
                try { prioridade = Integer.parseInt(dados.getOrDefault("prioridade", "3")); } catch (Exception ignored) {}

                Chamado c = criarChamado(
                    dados.getOrDefault("titulo", ""),
                    dados.getOrDefault("descricao", ""),
                    prioridade,
                    dados.getOrDefault("nomeUsuario", ""),
                    dados.getOrDefault("setor", ""),
                    dados.getOrDefault("equipamento", "")
                );
                sendResponse(exchange, 201, c.toJson());
            } else if ("DELETE".equals(method)) {
                String idStr = extractIdFromPath(path, "/api/chamados/");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    Chamado chave = new Chamado();
                    chave.setId(id);
                    Chamado c = arvoreChamados.buscar(chave);
                    if (c != null) {
                        arvoreChamados.remover(c);
                        listaChamados.remover(c);
                        sendResponse(exchange, 200, "{\"mensagem\":\"Chamado removido\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"erro\":\"Chamado nao encontrado\"}");
                    }
                } else {
                    sendResponse(exchange, 400, "{\"erro\":\"ID nao informado\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleFila(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                int tam = filaChamados.getTamanho();
                Chamado[] chamados = filaChamados.paraArray(new Chamado[tam]);
                String[] jsons = new String[tam];
                for (int i = 0; i < tam; i++) {
                    jsons[i] = chamados[i].toJson();
                }
                String response = "{\"tamanho\":" + tam + ",\"chamados\":" + JsonParser.toJsonArray(jsons) + "}";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleHistorico(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                int tam = pilhaHistorico.getTamanho();
                RegistroHistorico[] registros = pilhaHistorico.paraArray(new RegistroHistorico[tam]);
                String[] jsons = new String[tam];
                for (int i = 0; i < tam; i++) {
                    jsons[i] = registros[i].toJson();
                }
                String response = "{\"tamanho\":" + tam + ",\"registros\":" + JsonParser.toJsonArray(jsons) + "}";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleEstatisticas(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                int totalChamados = listaChamados.getTamanho();
                int naFila = filaChamados.getTamanho();
                int totalUsuarios = listaUsuarios.getTamanho();
                int totalEquipamentos = listaEquipamentos.getTamanho();
                int totalHistorico = pilhaHistorico.getTamanho();

                // Contar por status
                int abertos = 0, emAtendimento = 0, finalizados = 0;
                int critica = 0, alta = 0, media = 0, baixa = 0;
                for (int i = 0; i < totalChamados; i++) {
                    Chamado c = listaChamados.obter(i);
                    switch (c.getStatus()) {
                        case "ABERTO": abertos++; break;
                        case "EM_ATENDIMENTO": emAtendimento++; break;
                        case "FINALIZADO": finalizados++; break;
                    }
                    switch (c.getPrioridade()) {
                        case 1: critica++; break;
                        case 2: alta++; break;
                        case 3: media++; break;
                        case 4: baixa++; break;
                    }
                }

                String response = String.format(
                    "{\"totalChamados\":%d,\"naFila\":%d,\"totalUsuarios\":%d,\"totalEquipamentos\":%d," +
                    "\"totalHistorico\":%d,\"abertos\":%d,\"emAtendimento\":%d,\"finalizados\":%d," +
                    "\"critica\":%d,\"alta\":%d,\"media\":%d,\"baixa\":%d}",
                    totalChamados, naFila, totalUsuarios, totalEquipamentos,
                    totalHistorico, abertos, emAtendimento, finalizados,
                    critica, alta, media, baixa
                );
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleBusca(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if (handlePreflight(exchange)) return;

        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQueryString(query);

                // Busca por ID na Árvore Binária
                if (params.containsKey("id")) {
                    int id = Integer.parseInt(params.get("id"));
                    Chamado chave = new Chamado();
                    chave.setId(id);
                    Chamado c = arvoreChamados.buscar(chave);
                    if (c != null) {
                        sendResponse(exchange, 200, "{\"encontrado\":true,\"chamado\":" + c.toJson() + "}");
                    } else {
                        sendResponse(exchange, 200, "{\"encontrado\":false}");
                    }
                    return;
                }

                // Busca por termo na Lista Ligada
                if (params.containsKey("termo")) {
                    String termo = params.get("termo").toLowerCase();
                    ListaLigada<Chamado> resultados = listaChamados.filtrar(c ->
                        (c.getTitulo() != null && c.getTitulo().toLowerCase().contains(termo)) ||
                        (c.getDescricao() != null && c.getDescricao().toLowerCase().contains(termo)) ||
                        (c.getNomeUsuario() != null && c.getNomeUsuario().toLowerCase().contains(termo)) ||
                        (c.getSetor() != null && c.getSetor().toLowerCase().contains(termo))
                    );
                    int tam = resultados.getTamanho();
                    String[] jsons = new String[tam];
                    for (int i = 0; i < tam; i++) {
                        jsons[i] = resultados.obter(i).toJson();
                    }
                    sendResponse(exchange, 200, "{\"total\":" + tam + ",\"resultados\":" + JsonParser.toJsonArray(jsons) + "}");
                    return;
                }

                // Listagem em ordem pela Árvore Binária (in-order traversal)
                ListaLigada<String> ordenados = new ListaLigada<>();
                arvoreChamados.emOrdem(c -> ordenados.adicionar(c.toJson()));
                int tam = ordenados.getTamanho();
                String[] jsons = new String[tam];
                for (int i = 0; i < tam; i++) {
                    jsons[i] = ordenados.obter(i);
                }
                sendResponse(exchange, 200, "{\"total\":" + tam + ",\"resultados\":" + JsonParser.toJsonArray(jsons) + "}");

            } else {
                sendResponse(exchange, 405, "{\"erro\":\"Metodo nao permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"erro\":\"" + e.getMessage() + "\"}");
        }
    }

    // ==================== OPERAÇÕES DE NEGÓCIO ====================

    private static Usuario criarUsuario(String nome, String email, String setor, String cargo) {
        Usuario u = new Usuario(proximoIdUsuario++, nome, email, setor, cargo);
        listaUsuarios.adicionar(u);
        return u;
    }

    private static Equipamento criarEquipamento(String tipo, String marca, String patrimonio, String setor) {
        Equipamento e = new Equipamento(proximoIdEquipamento++, tipo, marca, patrimonio, setor);
        listaEquipamentos.adicionar(e);
        return e;
    }

    private static Chamado criarChamado(String titulo, String descricao, int prioridade,
                                         String nomeUsuario, String setor, String equipamento) {
        String dataAbertura = LocalDateTime.now().format(formatter);
        Chamado c = new Chamado(proximoIdChamado++, titulo, descricao, prioridade,
                                nomeUsuario, setor, equipamento, dataAbertura);

        // Adicionar na Lista Ligada (armazenamento geral)
        listaChamados.adicionar(c);

        // Adicionar na Árvore Binária (busca rápida por ID)
        arvoreChamados.inserir(c);

        // Adicionar na Fila (aguardando atendimento)
        filaChamados.enfileirar(c);

        // Registrar operação na Pilha (histórico)
        pilhaHistorico.empilhar(new RegistroHistorico(
            "CRIACAO", c.getId(),
            "Chamado #" + c.getId() + " criado: " + titulo,
            dataAbertura
        ));

        System.out.println("[CRIACAO] Chamado #" + c.getId() + " - " + titulo);
        return c;
    }

    private static void atenderChamado(int id, HttpExchange exchange) throws IOException {
        // Desenfileirar o próximo chamado da fila
        Chamado proximo = filaChamados.espiar();
        if (proximo == null) {
            sendResponse(exchange, 400, "{\"erro\":\"Fila de atendimento vazia\"}");
            return;
        }

        // Verificar se o chamado solicitado é o próximo da fila
        if (proximo.getId() != id) {
            sendResponse(exchange, 400, "{\"erro\":\"Chamado #" + id + " nao e o proximo da fila. O proximo e o #" + proximo.getId() + "\"}");
            return;
        }

        Chamado c = filaChamados.desenfileirar();
        c.setStatus("EM_ATENDIMENTO");

        String agora = LocalDateTime.now().format(formatter);
        pilhaHistorico.empilhar(new RegistroHistorico(
            "ATENDIMENTO", c.getId(),
            "Chamado #" + c.getId() + " iniciou atendimento",
            agora
        ));

        System.out.println("[ATENDIMENTO] Chamado #" + c.getId() + " retirado da fila");
        sendResponse(exchange, 200, c.toJson());
    }

    private static void finalizarChamado(int id, String solucao, String tecnico, HttpExchange exchange) throws IOException {
        Chamado chave = new Chamado();
        chave.setId(id);
        Chamado c = arvoreChamados.buscar(chave);

        if (c == null) {
            sendResponse(exchange, 404, "{\"erro\":\"Chamado nao encontrado\"}");
            return;
        }

        if (!"EM_ATENDIMENTO".equals(c.getStatus())) {
            sendResponse(exchange, 400, "{\"erro\":\"Chamado nao esta em atendimento\"}");
            return;
        }

        String agora = LocalDateTime.now().format(formatter);
        c.setStatus("FINALIZADO");
        c.setSolucao(solucao);
        c.setTecnicoResponsavel(tecnico);
        c.setDataFinalizacao(agora);

        pilhaHistorico.empilhar(new RegistroHistorico(
            "FINALIZACAO", c.getId(),
            "Chamado #" + c.getId() + " finalizado por " + tecnico + ": " + solucao,
            agora
        ));

        System.out.println("[FINALIZACAO] Chamado #" + c.getId() + " finalizado");
        sendResponse(exchange, 200, c.toJson());
    }

    // ==================== UTILITÁRIOS ====================

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static boolean handlePreflight(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }

    private static void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toString("UTF-8");
    }

    private static String extractIdFromPath(String path, String prefix) {
        if (path.length() > prefix.length() && path.startsWith(prefix)) {
            String rest = path.substring(prefix.length());
            if (rest.endsWith("/")) rest = rest.substring(0, rest.length() - 1);
            if (!rest.isEmpty() && rest.chars().allMatch(Character::isDigit)) {
                return rest;
            }
        }
        return null;
    }

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new java.util.HashMap<>();
        if (query == null || query.isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try {
                    params.put(
                        java.net.URLDecoder.decode(kv[0], "UTF-8"),
                        java.net.URLDecoder.decode(kv[1], "UTF-8")
                    );
                } catch (Exception e) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }
}
