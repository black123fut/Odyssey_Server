package DataStructures;

public class AvlNode<T extends Comparable> {
    private T data;
    private AvlNode<T> left;
    private AvlNode<T> right;
    private int height;

    public AvlNode(T data){
        this.data = data;
        this.left = null;
        this.right = null;
        this.height  = 0;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public AvlNode<T> getLeft() {
        return left;
    }

    public void setLeft(AvlNode<T> left) {
        this.left = left;
    }

    public AvlNode<T> getRight() {
        return right;
    }

    public void setRight(AvlNode<T> right) {
        this.right = right;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
