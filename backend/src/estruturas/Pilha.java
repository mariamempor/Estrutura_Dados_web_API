package helpdesk.estruturas;

/**
 * Implementação de Pilha (Stack) usando nós encadeados.
 * Utilizada para registrar histórico de chamados atendidos e operações realizadas (LIFO).
 */
public class Pilha<T> {

    private static class No<T> {
        T dado;
        No<T> proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No<T> topo;
    private int tamanho;

    public Pilha() {
        this.topo = null;
        this.tamanho = 0;
    }

    public void empilhar(T dado) {
        No<T> novoNo = new No<>(dado);
        novoNo.proximo = topo;
        topo = novoNo;
        tamanho++;
    }

    public T desempilhar() {
        if (estaVazia()) {
            return null;
        }
        T dado = topo.dado;
        topo = topo.proximo;
        tamanho--;
        return dado;
    }

    public T espiar() {
        if (estaVazia()) {
            return null;
        }
        return topo.dado;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int getTamanho() {
        return tamanho;
    }

    @SuppressWarnings("unchecked")
    public T[] paraArray(T[] array) {
        No<T> atual = topo;
        int i = 0;
        while (atual != null && i < array.length) {
            array[i++] = atual.dado;
            atual = atual.proximo;
        }
        return array;
    }
}
