package DataStructures;

public class TreeNode<T> {
    private T data;
    private TreeNode<T> right;
    private TreeNode<T> left;

    public TreeNode(T data){
        this.data = data;
        this.right = null;
        this.left = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public TreeNode<T> getRight() {
        return right;
    }

    public void setRight(TreeNode<T> right) {
        this.right = right;
    }

    public TreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(TreeNode<T> left) {
        this.left = left;
    }
}
