package helpdesk.estruturas;

/**
 * Implementação de Fila (Queue) usando nós encadeados.
 * Utilizada para organizar chamados aguardando atendimento (FIFO).
 */
public class Fila<T> {

    private static class No<T> {
        T dado;
        No<T> proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public Fila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void enfileirar(T dado) {
        No<T> novoNo = new No<>(dado);
        if (fim != null) {
            fim.proximo = novoNo;
        }
        fim = novoNo;
        if (inicio == null) {
            inicio = novoNo;
        }
        tamanho++;
    }

    public T desenfileirar() {
        if (estaVazia()) {
            return null;
        }
        T dado = inicio.dado;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return dado;
    }

    public T espiar() {
        if (estaVazia()) {
            return null;
        }
        return inicio.dado;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int getTamanho() {
        return tamanho;
    }

    @SuppressWarnings("unchecked")
    public T[] paraArray(T[] array) {
        No<T> atual = inicio;
        int i = 0;
        while (atual != null && i < array.length) {
            array[i++] = atual.dado;
            atual = atual.proximo;
        }
        return array;
    }
}
