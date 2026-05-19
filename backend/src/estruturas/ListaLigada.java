package helpdesk.estruturas;

/**
 * Implementação de Lista Ligada (Linked List) usando nós encadeados.
 * Utilizada para armazenar usuários, equipamentos, setores e chamados.
 */
public class ListaLigada<T> {

    private static class No<T> {
        T dado;
        No<T> proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No<T> cabeca;
    private int tamanho;

    public ListaLigada() {
        this.cabeca = null;
        this.tamanho = 0;
    }

    public void adicionar(T dado) {
        No<T> novoNo = new No<>(dado);
        if (cabeca == null) {
            cabeca = novoNo;
        } else {
            No<T> atual = cabeca;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novoNo;
        }
        tamanho++;
    }

    public T obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            return null;
        }
        No<T> atual = cabeca;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    public boolean remover(T dado) {
        if (cabeca == null) return false;

        if (cabeca.dado.equals(dado)) {
            cabeca = cabeca.proximo;
            tamanho--;
            return true;
        }

        No<T> atual = cabeca;
        while (atual.proximo != null) {
            if (atual.proximo.dado.equals(dado)) {
                atual.proximo = atual.proximo.proximo;
                tamanho--;
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    public boolean removerPorIndice(int indice) {
        if (indice < 0 || indice >= tamanho) return false;

        if (indice == 0) {
            cabeca = cabeca.proximo;
            tamanho--;
            return true;
        }

        No<T> atual = cabeca;
        for (int i = 0; i < indice - 1; i++) {
            atual = atual.proximo;
        }
        atual.proximo = atual.proximo.proximo;
        tamanho--;
        return true;
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    @SuppressWarnings("unchecked")
    public T[] paraArray(T[] array) {
        No<T> atual = cabeca;
        int i = 0;
        while (atual != null && i < array.length) {
            array[i++] = atual.dado;
            atual = atual.proximo;
        }
        return array;
    }

    public interface Filtro<T> {
        boolean aceitar(T item);
    }

    public T buscar(Filtro<T> filtro) {
        No<T> atual = cabeca;
        while (atual != null) {
            if (filtro.aceitar(atual.dado)) {
                return atual.dado;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public ListaLigada<T> filtrar(Filtro<T> filtro) {
        ListaLigada<T> resultado = new ListaLigada<>();
        No<T> atual = cabeca;
        while (atual != null) {
            if (filtro.aceitar(atual.dado)) {
                resultado.adicionar(atual.dado);
            }
            atual = atual.proximo;
        }
        return resultado;
    }
}
