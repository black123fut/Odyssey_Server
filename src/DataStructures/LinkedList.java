package DataStructures;

public class LinkedList<T> {
    private ListNode<T> head;
    private int size;

    public LinkedList(){
        this.head = null;
        this.size = 0;
    }

    public void add(T data){
        if (head != null){
            ListNode<T> tmp = head;

            while (tmp.getNext() != null){
                tmp = tmp.getNext();
            }
            tmp.setNext(new ListNode<>(data));
        }else {
            head = new ListNode<>(data);
        }
        size++;
    }

    public T get(int index){
        if (head != null){
            ListNode<T> tmp = head;

            for (int i = 0; i < index; i++) {
                tmp = tmp.getNext();
            }
            return tmp.getData();
        }
        return null;
    }

    public void remove(int index){
        if (head != null){
            ListNode<T> tmp = head;
            int counter = 0;

            while (tmp.getNext() != null){
                if (index == counter){
                    size--;
                    tmp.setNext(tmp.getNext().getNext());
                    return;
                }
                tmp = tmp.getNext();
                counter++;
            }
        }
    }

    public void remove(T data){
        if (head != null){
            if (data == head.getData()){
                size--;
                head = head.getNext();
            } else {
                ListNode<T> tmp = head;

                while(tmp.getNext() != null){
                    if (data == tmp.getNext().getData()){
                        size--;
                        tmp.setNext(tmp.getNext().getNext());
                        return;
                    }
                    tmp = tmp.getNext();
                }
            }
        }
    }

    public int length(){
        return size;
    }
}
