package DataStructures;

import Server.Song;

@SuppressWarnings("ALL")
public class BTree<T extends Comparable<T>> {
    private BNode root;

    /**
     * Creates an empty balanced tree.
     */
    public BTree() {
        root = null;
    }

    /**
     * Inserts an element into the tree.
     */
    public void insert(T info) {
        insert(info, root, null, false);
    }

    private void insert(T info, BNode node, BNode parent, boolean right) {

        if (node == null) {
            if (parent == null) {
                root = node = new BNode(info, parent);
            } else if (right) {
                parent.right = node = new BNode(info, parent);
            } else {
                parent.left = node = new BNode(info, parent);
            }
            restructInsert(node, false);
        } else if (info.compareTo((T) node.information) == 0) {
            node.information = info;
        } else if (info.compareTo((T) node.information) > 0) {
            insert(info, node.right, node, true);
        } else {
            insert(info, node.left, node, false);
        }
    }

    private void restructInsert(BNode node, boolean wasRight) {

        if (node != root) {
            if (node.parent.balance == '_') {
                if (node.isLeftNode()) {
                    node.parent.balance = '/';
                    restructInsert(node.parent, false);
                } else {
                    node.parent.balance = '\\';
                    restructInsert(node.parent, true);
                }
            } else if (node.parent.balance == '/') {
                if (node.isRightNode()) {
                    node.parent.balance = '_';
                } else {
                    if (!wasRight) {
                        rotateRight(node.parent);
                    } else {
                        doubleRotateRight(node.parent);
                    }
                }
            } else if (node.parent.balance == '\\') {
                if (node.isLeftNode()) {
                    node.parent.balance = '_';
                } else {
                    if (wasRight) {
                        rotateLeft(node.parent);
                    } else {
                        doubleRotateLeft(node.parent);
                    }
                }
            }
        }
    }

    private void rotateLeft(BNode a) {

        BNode b = a.right;

        if (a.parent == null) {
            root = b;
        } else {
            if (a.isLeftNode()) {
                a.parent.left = b;
            } else {
                a.parent.right = b;
            }
        }

        a.right = b.left;
        if (a.right != null) {
            a.right.parent = a;
        }

        b.parent = a.parent;
        a.parent = b;
        b.left = a;

        if (b.balance == '_') {
            a.balance = '\\';
            b.balance = '/';
        } else {
            a.balance = '_';
            b.balance = '_';
        }
    }

    private void rotateRight(BNode a) {

        BNode b = a.left;

        if (a.parent == null) {
            root = b;
        } else {
            if (a.isLeftNode()) {
                a.parent.left = b;
            } else {
                a.parent.right = b;
            }
        }

        a.left = b.right;
        if (a.left != null) {
            a.left.parent = a;
        }

        b.parent = a.parent;
        a.parent = b;
        b.right = a;

        if (b.balance == '_') {
            a.balance = '/';
            b.balance = '\\';
        } else {
            a.balance = '_';
            b.balance = '_';
        }
    }

    private void doubleRotateLeft(BNode a) {

        BNode b = a.right;
        BNode c = b.left;

        if (a.parent == null) {
            root = c;
        } else {
            if (a.isLeftNode()) {
                a.parent.left = c;
            } else {
                a.parent.right = c;
            }
        }

        c.parent = a.parent;

        a.right = c.left;
        if (a.right != null) {
            a.right.parent = a;
        }
        b.left = c.right;
        if (b.left != null) {
            b.left.parent = b;
        }

        c.left = a;
        c.right = b;

        a.parent = c;
        b.parent = c;

        if (c.balance == '/') {
            a.balance = '_';
            b.balance = '\\';
        } else if (c.balance == '\\') {
            a.balance = '/';
            b.balance = '_';
        } else {
            a.balance = '_';
            b.balance = '_';
        }

        c.balance = '_';
    }

    private void doubleRotateRight(BNode a) {

        BNode b = a.left;
        BNode c = b.right;

        if (a.parent == null) {
            root = c;
        } else {
            if (a.isLeftNode()) {
                a.parent.left = c;
            } else {
                a.parent.right = c;
            }
        }

        c.parent = a.parent;

        a.left = c.right;
        if (a.left != null) {
            a.left.parent = a;
        }
        b.right = c.left;
        if (b.right != null) {
            b.right.parent = b;
        }

        c.right = a;
        c.left = b;

        a.parent = c;
        b.parent = c;

        if (c.balance == '/') {
            b.balance = '_';
            a.balance = '\\';
        } else if (c.balance == '\\') {
            b.balance = '/';
            a.balance = '_';
        } else {
            b.balance = '_';
            a.balance = '_';
        }
        c.balance = '_';
    }

    public LinkedList<T> get(String data){
        LinkedList<T> list = new LinkedList<>();
        return get(root, data, list);
    }

    private LinkedList<T> get(BNode<T> current, String data, LinkedList<T> list){
        if (current != null){
            Song info = (Song) current.information;
            get(current.left, data, list);
            get(current.right, data, list);
            if (info.getTitulo().equalsIgnoreCase(data))
                list.add(current.information);
        }
        return list;
    }
}