package DataStructures;

import Server.Song;

public class SplayTree<T extends Comparable<T>, U extends Comparable<U>> {
    private SplayNode<T, U> root;
    private SplayNode<T, U> header = new SplayNode<T, U>(null, null); // For splay //Modified

    public SplayTree() {
        root = null;
    }

    /**
     * Inserta un elemento dentro del arbol.
     */
    public void insert(T key, U value) {
        SplayNode<T, U> n;
        int c;
        if (root == null) {
            root = new SplayNode<>(key, value);
            return;
        }
        splay(key);
        if ((c = key.compareTo(root.key)) == 0) {
            root.value = value;
            return;
        }
        n = new SplayNode<>(key, value);
        if (c < 0) {
            n.left = root.left;
            n.right = root;
            root.left = null;
        } else {
            n.right = root.right;
            n.left = root;
            root.right = null;
        }
        root = n;
    }

    private void splay(T key) {
        SplayNode<T, U> l, r, t, y;
        l = r = header;
        t = root;
        header.left = header.right = null;
        for (; ; ) {
            if (key.compareTo(t.key) < 0) {
                if (t.left == null) break;
                if (key.compareTo(t.left.key) < 0) {
                    y = t.left;                            /* rotate right */
                    t.left = y.right;
                    y.right = t;
                    t = y;
                    if (t.left == null) break;
                }
                r.left = t;                                 /* link right */
                r = t;
                t = t.left;
            } else if (key.compareTo(t.key) > 0) {
                if (t.right == null) break;
                if (key.compareTo(t.right.key) > 0) {
                    y = t.right;                            /* rotate left */
                    t.right = y.left;
                    y.left = t;
                    t = y;
                    if (t.right == null) break;
                }
                l.right = t;                                /* link left */
                l = t;
                t = t.right;
            } else {
                break;
            }
        }
        l.right = t.left;                                   /* assemble */
        r.left = t.right;
        t.left = header.right;
        t.right = header.left;
        root = t;
    }

    public LinkedList<U> get(String data){
        LinkedList<U> list = new LinkedList<>();
        return get(root, data, list);
    }

    private LinkedList<U> get(SplayNode<T, U> current, String data, LinkedList<U> list){
        if (current != null){
            Song info = (Song) current.value;
            get(current.left, data, list);
            get(current.right, data, list);
            if (info.getAlbum().equalsIgnoreCase(data))
                list.add(current.value);
        }
        return list;
    }
}
