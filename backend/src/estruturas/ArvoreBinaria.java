package helpdesk.estruturas;

/**
 * Implementação de Árvore Binária de Busca (BST).
 * Utilizada para buscar e organizar chamados por código/prioridade.
 */
public class ArvoreBinaria<T extends Comparable<T>> {

    private static class No<T> {
        T dado;
        No<T> esquerda;
        No<T> direita;

        No(T dado) {
            this.dado = dado;
            this.esquerda = null;
            this.direita = null;
        }
    }

    private No<T> raiz;
    private int tamanho;

    public ArvoreBinaria() {
        this.raiz = null;
        this.tamanho = 0;
    }

    public void inserir(T dado) {
        raiz = inserirRecursivo(raiz, dado);
        tamanho++;
    }

    private No<T> inserirRecursivo(No<T> no, T dado) {
        if (no == null) {
            return new No<>(dado);
        }
        int cmp = dado.compareTo(no.dado);
        if (cmp < 0) {
            no.esquerda = inserirRecursivo(no.esquerda, dado);
        } else if (cmp > 0) {
            no.direita = inserirRecursivo(no.direita, dado);
        } else {
            no.dado = dado;
            tamanho--;
        }
        return no;
    }

    public T buscar(T chave) {
        return buscarRecursivo(raiz, chave);
    }

    private T buscarRecursivo(No<T> no, T chave) {
        if (no == null) {
            return null;
        }
        int cmp = chave.compareTo(no.dado);
        if (cmp == 0) {
            return no.dado;
        } else if (cmp < 0) {
            return buscarRecursivo(no.esquerda, chave);
        } else {
            return buscarRecursivo(no.direita, chave);
        }
    }

    public boolean remover(T dado) {
        int tamanhoAntes = tamanho;
        raiz = removerRecursivo(raiz, dado);
        return tamanho < tamanhoAntes;
    }

    private No<T> removerRecursivo(No<T> no, T dado) {
        if (no == null) return null;

        int cmp = dado.compareTo(no.dado);
        if (cmp < 0) {
            no.esquerda = removerRecursivo(no.esquerda, dado);
        } else if (cmp > 0) {
            no.direita = removerRecursivo(no.direita, dado);
        } else {
            tamanho--;
            if (no.esquerda == null) return no.direita;
            if (no.direita == null) return no.esquerda;

            No<T> sucessor = encontrarMinimo(no.direita);
            no.dado = sucessor.dado;
            no.direita = removerRecursivo(no.direita, sucessor.dado);
            tamanho++;
        }
        return no;
    }

    private No<T> encontrarMinimo(No<T> no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public interface Visitante<T> {
        void visitar(T dado);
    }

    public void emOrdem(Visitante<T> visitante) {
        emOrdemRecursivo(raiz, visitante);
    }

    private void emOrdemRecursivo(No<T> no, Visitante<T> visitante) {
        if (no != null) {
            emOrdemRecursivo(no.esquerda, visitante);
            visitante.visitar(no.dado);
            emOrdemRecursivo(no.direita, visitante);
        }
    }

    public void preOrdem(Visitante<T> visitante) {
        preOrdemRecursivo(raiz, visitante);
    }

    private void preOrdemRecursivo(No<T> no, Visitante<T> visitante) {
        if (no != null) {
            visitante.visitar(no.dado);
            preOrdemRecursivo(no.esquerda, visitante);
            preOrdemRecursivo(no.direita, visitante);
        }
    }
}
