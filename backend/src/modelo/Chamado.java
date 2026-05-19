package helpdesk.modelo;

/**
 * Modelo que representa um chamado técnico de suporte.
 */
public class Chamado implements Comparable<Chamado> {
    private int id;
    private String titulo;
    private String descricao;
    private int prioridade; // 1=Crítica, 2=Alta, 3=Média, 4=Baixa
    private String status; // ABERTO, EM_ATENDIMENTO, FINALIZADO
    private String nomeUsuario;
    private String setor;
    private String equipamento;
    private String dataAbertura;
    private String dataFinalizacao;
    private String tecnicoResponsavel;
    private String solucao;

    public Chamado() {
        this.status = "ABERTO";
    }

    public Chamado(int id, String titulo, String descricao, int prioridade,
                   String nomeUsuario, String setor, String equipamento, String dataAbertura) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = "ABERTO";
        this.nomeUsuario = nomeUsuario;
        this.setor = setor;
        this.equipamento = equipamento;
        this.dataAbertura = dataAbertura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getEquipamento() { return equipamento; }
    public void setEquipamento(String equipamento) { this.equipamento = equipamento; }
    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }
    public String getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(String dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }
    public String getTecnicoResponsavel() { return tecnicoResponsavel; }
    public void setTecnicoResponsavel(String tecnicoResponsavel) { this.tecnicoResponsavel = tecnicoResponsavel; }
    public String getSolucao() { return solucao; }
    public void setSolucao(String solucao) { this.solucao = solucao; }

    public String getPrioridadeTexto() {
        switch (prioridade) {
            case 1: return "Critica";
            case 2: return "Alta";
            case 3: return "Media";
            default: return "Baixa";
        }
    }

    @Override
    public int compareTo(Chamado outro) {
        return Integer.compare(this.id, outro.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Chamado chamado = (Chamado) obj;
        return id == chamado.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"titulo\":\"%s\",\"descricao\":\"%s\",\"prioridade\":%d,\"prioridadeTexto\":\"%s\"," +
            "\"status\":\"%s\",\"nomeUsuario\":\"%s\",\"setor\":\"%s\",\"equipamento\":\"%s\"," +
            "\"dataAbertura\":\"%s\",\"dataFinalizacao\":\"%s\",\"tecnicoResponsavel\":\"%s\",\"solucao\":\"%s\"}",
            id, esc(titulo), esc(descricao), prioridade, getPrioridadeTexto(),
            esc(status), esc(nomeUsuario), esc(setor), esc(equipamento),
            esc(dataAbertura), esc(dataFinalizacao), esc(tecnicoResponsavel), esc(solucao)
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
