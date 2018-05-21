package DataStructures;

import Server.Song;

import java.util.Arrays;

public class Sorts {
    private LinkedList<Song> tempList;

    public Sorts(){
    }

    public void bubblesort(LinkedList<Song> hilera) {
        int k=0;

        while(k != hilera.length()) {
            int i=0;
            int j=1;
            while(j!=hilera.length()) {
                if(hilera.get(i).compareAlbum(hilera.get(j))> 0){
                    hilera.intercambiar(i, j);
                    i++;
                    j++;

                }else {
                    i++;
                    j++;
                }
            }k++;
        }
    }

    public void quickSort(LinkedList<Song> list) {
        sort(list);
    }

    private void sort(LinkedList<Song> list) {
        if (list.vacia() || list.length() == 0) {
            return;
        }
        this.tempList = list;
        quickSort(0, list.length() - 1);
    }

    private void quickSort(int lowerIndex, int higherIndex) {
        int i = lowerIndex;
        int j = higherIndex;
        Song pivot = this.tempList.get(lowerIndex + (higherIndex - lowerIndex) / 2);

        while (i <= j) {
            while (this.tempList.get(i).compareTo(pivot) < 0) {
                i++;
            }
            while (this.tempList.get(j).compareTo(pivot) > 0) {
                j--;
            }
            if (i <= j) {
                exchangeNames(i, j);
                i++;
                j--;
            }
        }
        //call quickSort recursively
        if (lowerIndex < j) {
            quickSort(lowerIndex, j);
        }
        if (i < higherIndex) {
            quickSort(i, higherIndex);
        }
    }

    private void exchangeNames(int i, int j) {
        Song temp = this.tempList.get(i);
        this.tempList.getNode(i).setData(this.tempList.get(j));
        this.tempList.getNode(j).setData(temp);
    }

    public void radixSort(LinkedList<Song> list){
        radixSort(list, 'a', 'z');
    }

    private void radixSort(LinkedList<Song> arr, char lower, char upper){
        int maxIndex = 0;
        for(int i = 0; i < arr.length();i++){
            if(arr.get(i).getArtista().length() - 1 > maxIndex){
                maxIndex = arr.get(i).getArtista().length() - 1;
            }
        }

        for(int i = maxIndex; i >= 0; i--){
            countingSort(arr,i,lower,upper);
        }
    }

    private void countingSort(LinkedList<Song> arr,int index,char lower,char upper){
        int[] countArray = new int[(upper-lower)+2];
        Song[] tempArray = new Song[arr.length()];
        Arrays.fill(countArray,0);

        for(int i = 0; i < arr.length(); i++){
            int charIndex = (arr.get(i).getArtista().toLowerCase().replace(" ", "").length() - 1 < index) ?
                    0 : ((arr.get(i).getArtista().toLowerCase().replace(" ", "").charAt(index) - lower) + 1);

            countArray[charIndex]++;
        }

        for(int i = 1; i < countArray.length; i++){
            countArray[i] += countArray[i - 1];
        }

        for(int i = arr.length() - 1; i >= 0; i--){
            int charIndex = (arr.get(i).getArtista().toLowerCase().replace(" ", "").length()-1 < index) ?
                    0 : (arr.get(i).getArtista().toLowerCase().replace(" ", "").charAt(index) - lower) + 1;

            tempArray[countArray[charIndex]-1] = arr.get(i);
            countArray[charIndex]--;
        }

        for(int i = 0; i < tempArray.length; i++){
            arr.getNode(i).setData(tempArray[i]);
        }
    }


}
