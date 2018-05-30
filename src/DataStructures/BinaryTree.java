package DataStructures;

import Server.Song;

public class BinaryTree<T extends Comparable> {
    private TreeNode<T> root;

    public BinaryTree(){
        root = null;
    }

    /**
     * Inserta un elemento en el arbol.
     * @param data Elemento que se insertara en el arbol.
     */
    public void add(T data){
        this.root = this.add(data, this.root);
    }


    private TreeNode add(T data, TreeNode<T> current){
        if (current == null){
            return new TreeNode<>(data);
        } else if (current.getData().compareTo(data) > 0){
            current.setLeft(this.add(data, current.getLeft()));
        } else if (current.getData().compareTo(data) < 0){
            current.setRight(this.add(data, current.getRight()));
        }
        return current;
    }

    /**
     * Remueve un nodo del arbol.
     * @param data elemnto que se busca eliminar.
     */
    public void remove(T data){
        this.root = remove(data, this.root);
    }

    private TreeNode<T> remove(T data, TreeNode<T> current){
        if(current == null){
            return current;
        }
        if(current.getData().compareTo(data) > 0){
            current.setLeft(remove(data, current.getLeft()));
        } else if(current.getData().compareTo(data) < 0){
            current.setRight(remove(data, current.getRight()));
        } else if(current.getLeft() != null && current.getData() != null){
            current.setData(findMin(current.getRight()).getData());
            current.setRight(remove(current.getData(), current.getRight()));
        } else{
            current  = current.getLeft() != null? current.getLeft(): current.getRight();
        }
        return current;
    }

    /**
     * Busca el elemento mas pequeno del arbol.
     * @param current Elemento en el que busca.
     * @return Elementos a la izquierda en el arbol.
     */
    private TreeNode<T> findMin(TreeNode<T> current){
        if (current == null){
            return null;
        } else if (current.getLeft() == null){
            return current;
        } else{
            return findMin(current.getLeft());
        }
    }

    /**
     * Obtiene una lista de los elementos del arbol.
     * @param object Nombre que se busca.
     * @return La lista con los elementos que se buscaban.
     */
    public LinkedList<T> get(String object){
        LinkedList<T> list = new LinkedList<>();
        return this.get(root, object, list);
    }

    /**
     * Recorre el arbol para almacenarlos en una lista.
     * @param current Nodo actual del recorrido.
     * @param list Lista en la que se almacenan los valores de los nodos.
     * @return La lista con los elementos del arbol.
     */
    private LinkedList<T> get(TreeNode<T> current, String data, LinkedList<T> list){
        if (current != null){
            Song info = (Song) current.getData();
            get(current.getLeft(), data, list);
            get(current.getRight(), data, list);
                if (info.getTitulo().equalsIgnoreCase(data))
                    list.add(current.getData());

        }
        return list;
    }

    public void printInorden(){
        printInorden(root);
    }

    private void printInorden(TreeNode<T> current){
        if (current != null){
            printInorden(current.getLeft());
            System.out.println(current.getData() + "");
            printInorden(current.getRight());
        }
    }
}