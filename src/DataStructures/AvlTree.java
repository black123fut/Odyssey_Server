package DataStructures;

import Server.Song;

public class AvlTree<T extends Comparable> {
    private AvlNode<T> root;

    public AvlTree(){
        root = null;
    }

    private int height(AvlNode<T> tmp){
        return tmp == null? -1 : tmp.getHeight();
    }

    private int max(int left, int right){
        return left > right? left : right;
    }

    public void add(T data){
        this.root = add(data, root);
    }

    private AvlNode<T> add(T data, AvlNode<T> current){
        if (current == null){
            current = new AvlNode<>(data);
        } else if(current.getData().compareTo(data) > 0){
            current.setLeft(add(data, current.getLeft()));
            if (height(current.getLeft()) - height(current.getRight()) == 2){
                if (current.getLeft().getData().compareTo(data) > 0){
                    current = rotateWithLeftChild(current);
                } else {
                    current = doubleWithLeftChild(current);
                }
            }
        } else if (current.getData().compareTo(data) < 0){
            current.setRight(add(data, current.getRight()));
            if(height(current.getRight()) - height(current.getLeft()) == 2){
                if (current.getRight().getData().compareTo(data) < 0){
                    current = rotateWithRightChild(current);
                } else{
                    current = doubleWithRightChild(current);
                }
            }
        }
        current.setHeight(max(height(current.getLeft()), height(current.getRight())) + 1);
        return current;
    }

    private AvlNode<T> doubleWithRightChild(AvlNode<T> tmp) {
        tmp.setRight( rotateWithLeftChild(tmp.getRight()) );
        return rotateWithRightChild(tmp);
    }

    private AvlNode<T> rotateWithRightChild(AvlNode<T> n1) {
        AvlNode<T> n2 = n1.getRight();
        n1.setRight(n2.getLeft());
        n2.setLeft(n1);
        n1.setHeight(max( height(n1.getLeft()), height(n1.getRight()) ) + 1 );
        n2.setHeight( max( height(n2.getRight()), n1.getHeight() ) + 1 );
        return n2;
    }

    private AvlNode<T> doubleWithLeftChild(AvlNode<T> tmp) {
        tmp.setLeft(rotateWithRightChild(tmp.getLeft()));
        return rotateWithLeftChild(tmp);
    }

    private AvlNode<T> rotateWithLeftChild(AvlNode<T> n2) {
        AvlNode<T> n1 = n2.getLeft();
        n2.setLeft(n1.getRight());
        n1.setRight(n2);
        n2.setHeight(max(height(n2.getLeft()), height(n2.getRight())) + 1);
        n1.setHeight(max(height(n1.getLeft()), n2.getHeight()) + 1);
        return n1;
    }

    public LinkedList<T> get(String data){
        LinkedList<T> list = new LinkedList<>();
        return get(root, data, list);
    }

    private LinkedList<T> get(AvlNode<T> current, String data, LinkedList<T> list){
        if (current != null){
            Song info = (Song) current.getData();
            get(current.getLeft(), data, list);
            get(current.getRight(), data, list);
            if (info.getArtista().equalsIgnoreCase(data))
                list.add(current.getData());
            list.add(current.getData());
        }
        return list;
    }
}