/**
 * Esta clase se usa para representar cada uno de los "sub-árboles" de Huffman.
 * La raíz es un par (o cualquier otro tipo T) con un caracter y su frecuencia de aparición.
 * Inicialmente, las raíces son hojas (nodos sin hijos) pero conforme se corra el programa, se van agregando hijos para
 * formar el árbol de Huffman final.
 *
 * @author Eduardo Biazzetti - B40999.
 */
class Arbol<T> {
    private Arbol hijoIzq;
    private Arbol hijoDer;
    private T raiz;

    /**
     * Constructor de la clase Arbol.
     * @param p: Representa la raíz del nuevo árbol.
     * @param hi: Representa al hijo izquierdo del nuevo árbol.
     * @param hd: Representa al hijo derecho del nuevo árbol.
     */
    Arbol(T p, Arbol hi, Arbol hd) {
        hijoIzq = hi;
        hijoDer = hd;
        raiz = p;
    }

    /**
     * Permite 'recuperar' la raíz del árbol.
     * @return raiz.
     */
    T getRaiz() {
        return raiz;
    }

    /**
     * Permite 'recuperar' el hijo izquierdo del árbol.
     * @return hijoIzq.
     */
    Arbol getHijoIzq() {
        return hijoIzq;
    }

    /**
     * Permite 'recuperar' el hijo derecho del árbol.
     * @return hijoDer.
     */
    Arbol getHijoDer() {
        return hijoDer;
    }

    /**
     * Modifica y establece una nueva raíz en el árbol.
     * @param p: La nueva raíz del árbol. Tiene que ser de tipo T.
     */
    void setRaiz(T p) {
        raiz = p;
    }

    /**
     * Modifica y establece un nuevo hijo izquierdo. Tiene que ser de tipo Arbol.
     * @param hi el nuevo árbol que representa el hijo izquierdo.
     */
    void setHijoIzq(Arbol hi) { hijoIzq = hi; }

    /**
     * Modifica y establece un nuevo hijo derecho. Tiene que ser de tipo Arbol.
     * @param hd el nuevo árbol que representa el hijo derecho.
     */
    void setHijoDer(Arbol hd) { hijoDer = hd; }

    /**
     * Verifica si la raíz del árbol tiene o no tiene hijos.
     * @return true si hijoDerecho e hijoIzquierdo son nulos; en caso contario, devuelve false.
     */
    boolean esHoja() { return (hijoIzq == null) && (hijoDer == null); }
}