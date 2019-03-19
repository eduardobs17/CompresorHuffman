import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase Main. Aquí empieza la ejecución del programa.
 */
public class Main {

    /**
     * Constructor del main.
     */
    private Main() {}

    /**
     * Método que permite averiguar si el caracter 'c' se encuentra en la lista l.
     * @param c El caracter que queremos buscar.
     * @param l La lista en donde queremos buscar.
     * @return true si el elemento se encuentra; false si sucede lo contrario.
     */
    private boolean buscar(char c, Lista<Arbol<Par<Character,Integer>>> l) {
        Elemento<Arbol<Par<Character,Integer>>> it1 = l.primero;
        while (it1 != null) {
            if (it1.objeto.getRaiz().getPrimero() == c) {
                return true;
            } else {
                it1 = it1.siguiente;
            }
        }
        return false;
    }

    /**
     * Método que permite crear el árbol de Huffman correspondiente a los elementos (caracteres y frecuencias) que se
     * encuentran en la lista l.
     * @param l la lista de la cual queremos crear el árbol.
     * @return el arbol de Huffman completamente terminado.
     */
    private Arbol<Par<Character,Integer>> crearArbolHuffman(Lista<Arbol<Par<Character,Integer>>> l) {
        while (l.primero.siguiente != null) {
            l = this.burbuja(l);
            Arbol<Par<Character,Integer>> hijoIzq = l.primero.objeto;
            Arbol<Par<Character,Integer>> hijoDer = l.primero.siguiente.objeto;
            Arbol<Par<Character,Integer>> nuevoArbol = new Arbol<>(new Par('_',(hijoIzq.getRaiz().getSegundo() + hijoDer.getRaiz().getSegundo())),hijoIzq,hijoDer);
            l.BorrarPrimero();
            l.BorrarPrimero();
            l.agregar(nuevoArbol);
        }
        return l.primero.objeto;
    }

    /**
     * Aplica el algoritmo burbuja para ordenar los elementos de la lista de menor a mayor, según la frecuencia con la que
     * aparece cada elemento.
     * @param l la lista que queremos ordenar.
     * @return lista ya ordenada de menor a mayor.
     */
    private Lista<Arbol<Par<Character,Integer>>> burbuja(Lista<Arbol<Par<Character,Integer>>> l) {
        Elemento<Arbol<Par<Character,Integer>>> it1 = l.ultimo;
        while (it1 != l.primero) {
            Elemento<Arbol<Par<Character,Integer>>> it2 = l.primero;
            int contador = 0;
            while (it2.siguiente != null) {
                if (it2.objeto.getRaiz().getSegundo() > it2.siguiente.objeto.getRaiz().getSegundo()) {
                    l.intercambiar(contador,contador+1);
                }
                contador++;
                it2 = it2.siguiente;
            }
            it1 = it1.anterior;
        }
        return l;
    }

    /**
     * Crea una 'tabla' a apartir de un arbol de Huffman en especifico. Esta tabla será una lista de pares,
     * donde cada par es de tipo char y String. El char representa el caracter y el String su codigo asociado.
     * Esta tabla será la que se guarde en el archivo comprimido. Sin esta, el archivo no se podría descomprimir.
     * @param arbol el Arbol de Huffman del cual queremos hacer la tabla.
     * @param hilera hilera auxiliar. Al ser un algoritmo recursivo, se ocupa conocer el codigo del padre, para 'seguir la secuencia'.
     * @param listaAnterior al igual que el parametro anterior, es auxiliar. Se ocupa conocer la secuencia del nodo anterior.
     * @return lista con todos los caracteres identificados con sus respectivos codigos.
     */
    private Lista<Par<Character,String>> crearTabla(Arbol<Par<Character,Integer>> arbol,String hilera, Lista<Par<Character,String>> listaAnterior) {
        Lista<Par<Character,String>> lista = listaAnterior;
        if (arbol.esHoja()) {
            lista.agregar(new Par<>(arbol.getRaiz().getPrimero(),hilera));
        } else {
            lista = crearTabla(arbol.getHijoIzq(), hilera + "0", lista);
            lista = crearTabla(arbol.getHijoDer(), hilera + "1", lista);
        }
        return lista;
    }

    /**
     * Método que se encarga de la compresión. Entre sus acciones están: crear el árbold de Huffman y su tabla asociada,
     * Crear el archivo de salida, guardar la tabla y guardar los datos.
     * @param file1 el archivo que queremos comprimir.
     * @param file2 una copia auxiliar del archivo que queremos comprimir.
     * @param tipo Se refiere a si el usuario desea un nombre para el archivo de salida.
     *             Si así fuera, tipo posee ese nombre.
     *             Si así no fuera, tipo posee el nombre original.
     * @param nombreDestino opcional. Es el nombre del archivo de salida.
     */
    private void comprimir(BitInputStream file1, BitInputStream file2, String tipo, String nombreDestino) {
        Lista<Arbol<Par<Character, Integer>>> l = new Lista<>();
        int tamArchivo = 0;
        while (file1.hasNextBit()) {
            int readByte = file1.read();
            if (buscar((char) readByte, l)) {
                Arbol<Par<Character, Integer>> elem = null;
                for (int x = 0; x < l.numElementos(); x++) {
                    if (l.recuperar(x).getRaiz().getPrimero() == (char) readByte) {
                        elem = l.recuperar(x);
                        x = l.numElementos() + 50;
                    }
                }
                elem.getRaiz().modificarSegundo(elem.getRaiz().getSegundo() + 1);
            } else {
                Arbol<Par<Character, Integer>> a = new Arbol<>(new Par((char) readByte, 1), null, null);
                l.agregar(a);
            }
            tamArchivo++;
        }

        Arbol<Par<Character, Integer>> arbolHuffman = crearArbolHuffman(l);
        Lista<Par<Character, String>> tabla = crearTabla(arbolHuffman, "", new Lista<>());

        int tamano = tipo.length();
        String tipoArchivo = tipo.substring(tamano - 3);

        try {
            BitOutputStream nuevoBitOutputStream;
            if (nombreDestino.equals("")) {
                nuevoBitOutputStream = new BitOutputStream(new FileOutputStream(tipo.substring(0,tamano-4) + ".huf"));
            } else {
                nuevoBitOutputStream = new BitOutputStream(new FileOutputStream(nombreDestino + ".huf"));
            }
            nuevoBitOutputStream.write('h');
            nuevoBitOutputStream.write('u');
            nuevoBitOutputStream.write('f');
            for (int x = 0; x < tipoArchivo.length(); x++) { //Extension del archivo original
                nuevoBitOutputStream.write(tipoArchivo.charAt(x));
            }
            nuevoBitOutputStream.write(' ');
            String hilera = "";
            hilera += tabla.numElementos(); // cantidad de caracteres diferentes en el archivo
            for (int x = 0; x < hilera.length(); x++) {
                nuevoBitOutputStream.write(hilera.charAt(x));
            }
            nuevoBitOutputStream.write(' ');
            hilera = "";
            hilera += tamArchivo; // tamaño del archivo original
            for (int x = 0; x < hilera.length(); x++) {
                nuevoBitOutputStream.write(hilera.charAt(x));
            }
            nuevoBitOutputStream.write(' ');

            Elemento<Par<Character, String>> it1 = tabla.primero;
            while (it1 != null) {
                nuevoBitOutputStream.write(it1.objeto.getPrimero());
                hilera = "";
                hilera += it1.objeto.getSegundo().length();
                if (hilera.length() > 1) {
                    nuevoBitOutputStream.write(255);
                    for (int x = 0; x < hilera.length(); x++) {
                        nuevoBitOutputStream.write(hilera.charAt(x));
                    }
                    nuevoBitOutputStream.write(255);
                } else {
                    nuevoBitOutputStream.write(hilera.charAt(0));
                }
                for (int x = 0; x < it1.objeto.getSegundo().length(); x++) {
                    nuevoBitOutputStream.write(it1.objeto.getSegundo().charAt(x));
                }
                it1 = it1.siguiente;
            }

            file2.setBitMode(false);
            int contador = 0;

            while (file2.hasNextBit()) {
                int readByte = file2.read();
                Par<Character, String> par = null;
                for (int x = 0; x < tabla.numElementos(); x++) {
                    if (tabla.recuperar(x).getPrimero() == (char) readByte) {
                        par = tabla.recuperar(x);
                        x = tabla.numElementos() + 31;
                    }
                }
                for (int x = 0; x < par.getSegundo().length(); x++) {
                    char bit = par.getSegundo().charAt(x);
                    contador++;
                    if (bit == '0') {
                        nuevoBitOutputStream.writeBit(0);
                    } else {
                        nuevoBitOutputStream.writeBit(1);
                    }
                }
            }
            while (contador % 8 != 0) {
                nuevoBitOutputStream.writeBit(0);
                contador++;
            }
            System.out.println("El archivo se comprimó correctamente.");
            System.exit(0);
        } catch (IOException exception) {
            System.out.println("Hubo un error al crear el archivo comprimido");
            System.exit(-3);
        }
    }

    /**
     * Método que se encarga de la descompresión del archivo. Tiene una función inversa al método anterior.
     * Entre sus acciones están: recrear el árbol de Huffman a partir de los datos en el archivo,
     * crear el archivo de salida, y guardar los datos originales.
     * @param file el archivo que queremos descomprimir. Tiene que ser un archivo anteriormente comprimido, osea, con extension .huf
     * @param nombreDestino opcional. Posee el nombre que el archivo de salida llevará.
     * @param tieneNombre false si el usuario no digita ninguno. Si fuera así, el archivo llevará el nombre original + "Descomprimido"
     */
    private void descomprimir(BitInputStream file, String nombreDestino, boolean tieneNombre) {
        String tipo = "";
        boolean continuar = true;
        while (continuar) {
            int bytes = file.read();
            if (bytes != 32) {
                tipo += (char) bytes;
            } else {
                continuar = false;
            }
        }
        String basura = "";
        continuar = true;
        while (continuar) {
            int bytes = file.read();
            if (bytes != 32) {
                basura += (char) bytes;
            } else {
                continuar = false;
            }
        }
        int cantidadCaracteres = Integer.parseInt(basura);

        basura = "";
        continuar = true;
        while (continuar) {
            int bytes = file.read();
            if (bytes != 32) {
                basura += (char) bytes;
            } else {
                continuar = false;
            }
        }
        int tamArchivo = Integer.parseInt(basura);

        Lista<Par<Character,String>> tabla = new Lista<>();
        while (tabla.numElementos() != cantidadCaracteres) {
            String codigo = "";
            char caracter = (char) file.read();
            String temp = "";
            int var = file.read();
            if (var != 255) {
                temp += (char) var;
            } else {
                var = file.read();
                while (var != 255) {
                    temp += (char) var;
                    var = file.read();
                }
            }
            for (int x = 0; x < Integer.parseInt(temp); x++) {
                codigo += (char) file.read();
            }
            Par<Character,String> par = new Par<>(caracter, codigo);
            tabla.agregar(par);
        }
        Arbol<Character> arbolHuffman = reconstruirArbol(tabla);

        try {
            BitOutputStream nuevoBitOutputStream;
            if (tieneNombre) {
                nuevoBitOutputStream = new BitOutputStream(new FileOutputStream(nombreDestino + "." + tipo));
            } else {
                String nombreArchivo = nombreDestino.substring(0, nombreDestino.length() - 4);
                nuevoBitOutputStream = new BitOutputStream(new FileOutputStream(nombreArchivo + "Descomprimido." + tipo));
            }

            file.setBitMode(true);
            int contador = 0;
            while (contador < tamArchivo) {
                Arbol<Character> it1 = arbolHuffman;
                do {
                    if (file.hasNextBit()) {
                        int bit = file.readBit();
                        if (bit == 0) {
                            it1 = it1.getHijoIzq();
                        } else {
                            it1 = it1.getHijoDer();
                        }
                    } else {
                        System.out.println("Sucedió un error inesperado");
                        System.exit(2);
                    }
                } while (!it1.esHoja());
                nuevoBitOutputStream.write(it1.getRaiz());
                contador++;
            }
            System.out.println("El archivo se descomprimó correctamente.");
            System.exit(0);
        } catch (IOException exception) {
            System.out.println("Hubo un error al crear el archivo descomprimido");
            System.exit(-3);
        }
    }

    /**
     * Método auxiliar de la descompresión. Recibe los datos que se encontraban en el archivo comprimido y recrea el arbol de Huffman,
     * siguiendo sus codigos.
     * @param lista una lista de pares. Cada par será de tipo (char,String), donde char representa el caracter y String su codigo asociado.
     * @return Arbol de Huffman reconstruido.
     */
    private Arbol<Character> reconstruirArbol(Lista<Par<Character,String>> lista) {
        Arbol<Character> arbol = new Arbol<>('\n', null, null);
        for (int x = 0; x < lista.numElementos(); x++) {
            Arbol<Character> aux = arbol;
            Arbol<Character> it1 = aux;
            String codigo = (lista.recuperar(x)).getSegundo();
            for (int m = 0; m < codigo.length(); m++) {
                char bit = codigo.charAt(m);
                if (bit == '0') {
                    if (it1.getHijoIzq() == null) {
                        it1.setHijoIzq(new Arbol<>('\n', null, null));
                        it1 = it1.getHijoIzq();
                    } else {
                        it1 = it1.getHijoIzq();
                    }
                } else {
                    if (it1.getHijoDer() == null) {
                        it1.setHijoDer(new Arbol<>('\n', null, null));
                        it1 = it1.getHijoDer();
                    } else {
                        it1 = it1.getHijoDer();
                    }
                }
            }
            it1.setRaiz((lista.recuperar(x)).getPrimero());
            arbol.setRaiz(aux.getRaiz());
            arbol.setHijoIzq(aux.getHijoIzq());
            arbol.setHijoDer(aux.getHijoDer());
        }
        return arbol;
    }

    /**
     * Método desde el cual se inicia el programa.
     * @param parametros los datos que el programa recibe. el primero debe ser la instrucción (-c para compresión o -d para descompresión).
     *                   El segundo es la ruta del archivo. tiene que ser un archivo válido.
     *                   El tercero es opcional, si se desea que el archivo de salida tenga un nombre en especial.
     */
    public static void main(String[] parametros) {
        if (parametros.length == 0) {
            System.out.println("ERROR, No se recibió ninguna indicación");
            System.exit(-5);
        }

        Main main = new Main();
        File file = null;
        if (!(parametros.length < 2)) {
            file = new File(parametros[1]);
        } else {
            System.out.println("ERROR, No se recibió ningún archivo");
            System.exit(-4);
        }
        try {
            if (parametros[0].equals("-c")) {
                BitInputStream bitInputStream1 = new BitInputStream(new FileInputStream(file));
                BitInputStream bitInputStream2 = new BitInputStream(new FileInputStream(file));
                BitInputStream bitInputStream3 = new BitInputStream(new FileInputStream(file));

                bitInputStream1.setBitMode(false);
                String mode = "";
                while (mode.length() < 3) {
                    if (bitInputStream1.hasNextBit()) {
                        mode += (char) bitInputStream1.read();
                    }
                }

                if (!mode.equals("huf")) {
                    if (parametros.length > 2) {
                        main.comprimir(bitInputStream2, bitInputStream3, parametros[1], parametros[2]);
                    } else {
                        main.comprimir(bitInputStream2, bitInputStream3, parametros[1], "");
                    }
                } else {
                    System.out.println("Sucedió un error, el archivo ya está comprimido. Favor revisarlo.");
                    System.exit(-3);
                }
            }
            if (parametros[0].equals("-d")) {
                BitInputStream bitInputStream1 = new BitInputStream(new FileInputStream(file));

                bitInputStream1.setBitMode(false);
                String mode = "";
                while (mode.length() < 3) {
                    if (bitInputStream1.hasNextBit()) {
                        mode += (char) bitInputStream1.read();
                    }
                }
                if (mode.equals("huf")) {
                    if (parametros.length > 2) {
                        main.descomprimir(bitInputStream1, parametros[2], true);
                    } else {
                        main.descomprimir(bitInputStream1, parametros[1], false);
                    }
                } else {
                    System.out.println("Sucedió un error, el archivo no está comprimido, o sucedió un error de formato. Favor revisarlo.");
                    System.exit(-3);
                }
            } else {
                System.out.println("ERROR, no se reconoció la instrucción. Intente de nuevo");
                System.exit(-2);
            }
        } catch (IOException exception) {
            System.out.println("Hubo un error al intentar abrir el archivo");
            System.exit(-1);
        }
    }
}