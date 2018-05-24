package DataStructures;

class SplayNode<T extends Comparable<T>, U extends Comparable<U>> {
    T key;          // The key in the node
    U value;            //The value associated for the key
    SplayNode<T, U> left;         // Left child
    SplayNode<T, U> right;        // Right child

    public SplayNode(T theKey, U theValue) {
        key = theKey;
        value = theValue;
        left = right = null;
    }
}