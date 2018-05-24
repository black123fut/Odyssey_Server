package DataStructures;

class BNode<T extends Comparable<T>> {

    T information;

    BNode parent;

    BNode left;

    BNode right;

    char balance;

    public BNode(T information, BNode parent) {
        this.information = information;
        this.parent = parent;
        this.left = null;
        this.right = null;
        this.balance = '_';
    }

    boolean isLeftNode() {
        return (parent.left == this);
    }

    boolean isRightNode() {
        return (parent.right == this);
    }
}
