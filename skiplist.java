
package dsa;

//package skiplist;
// Starter code for Project 2: skip lists
// Do not rename the class, or change names/signatures of methods that are declared to be public.
// BZX180000, KAC180002
//package dsa;
import java.util.Iterator;
//import java.util.NoSuchElementException;
import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {

    static final int POSSIBLELEVELS = 33;
    Entry<T> head, tail;//dummy nodes
    int size, maxLevel;
    Entry[] pred; //used by find
    Random random;//random for filling

    static class Entry<E> {

        E element;
        Entry[] next;//pointer to next entry
        Entry prev;
        int[] span; //for indexing the nnumbers
        int height;

        public Entry(E x, int lev) {
            element = x;
            next = new Entry[lev];
            span = new int[lev];
            height = lev;
        }

        public E getElement() {
            return element;
        }

    }

    // Constructor
    public SkipList() {
        head = new Entry(null, POSSIBLELEVELS);
        tail = new Entry(null, POSSIBLELEVELS);
        size = 0;
        maxLevel = 1;
        pred = new Entry[POSSIBLELEVELS];
        for (int i = 0; i < POSSIBLELEVELS; i++) {
            head.span[i] = 0;
            head.next[i] = tail;
            pred[i] = head;
        }

    }

    public void findPred(T x) {// trace predecessors while finding x

        Entry<T> top = head; // initialize top to head
        //for loop traces the predecessor for every level down
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (top.next[i].element != null && 
                    ((T) top.next[i].element).compareTo(x) < 0)//if next is lesser then x
            {
                top = top.next[i]; //set top to right most element less than x
            }
            pred[i] = top; //set pred[i] to right most element
        }
    }

    public static int chooseLevel() { // function randomizes level from 32 to 0
        return ((int) (Math.random() * (POSSIBLELEVELS - 1)));
    }

    // Add x to list. If x already exists, return null. 
    //Returns true if new node is added to list
    public boolean add(T x) {
        //if element already exists don't add it
        if (contains(x)) {
            return false;
        }
        
        int lvl = chooseLevel(); //get random level
        Entry<T> entry = new Entry<>(x, lvl); //declare new entry
        for (int i = 0; i <= lvl - 1; i++) {
            entry.next[i] = pred[i].next[i]; //set next for all levels from pred
            pred[i].next[i] = entry; //set next for pred
        }
        size++; // increase size
        return true;
    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) {
        if (isEmpty()) { //return null if empty
            return null;
        }
        findPred(x); //trace path
        if (pred[0].next != null) {// check if next is tail
            Entry<T> ceiling = pred[0].next[0]; //ceiling will be element after pred
            return ceiling.element; //return ceiling
        }
        return null;// if next was tail return null
    }

    // Does list contain x?
    public boolean contains(T x) {
        //return false in case of empty list
        if (isEmpty()) {
            return false;
        }
        findPred(x); // trace path
        //if element after pred exists and is the element return true
        return (pred[0].next[0].element != null && 
        ((T) pred[0].next[0].element).compareTo(x) == 0);
    }

    // Return first element of list
    public T first() {
        //return null if list is empty
        if (isEmpty()) {
            return null;
        }
        //find element after head and return
        Entry<T> first = head.next[0];
        return first.element;
    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
    	//return null if list is empty
        if (isEmpty()) {
            return null;
        }
        //trace path to floor
        findPred(x);

        if (contains(x)){
            Entry<T> floor = pred[0].next[0];
            return floor.element;
        }
        //returns the element right after pred on the lowest level
        if (head.next != pred) {// check if previous is tail
            Entry<T> floor = pred[0];
            return floor.element;
        }
        //if previous is tail return null
        return null;
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n) {
    	//if n is bigger than the size return null
        if (n > size - 1) {
            return null;
        }
        //if list empty return null
        if (isEmpty()) {
            return null;
        }

        Entry<T> p = head;//initialize p to head
        //loop through until element n is reached
        for (int k = 0; k <= n; k++) {
            p = p.next[0];
        }
        //return element
        return p.element;
    }

    // Is the list empty?
    public boolean isEmpty() {
        //check if the head and tail are the same index, then return 1 or 0
        return head.next[0] == tail;
    }

    // Iterate through the elements of list in sorted order
    public Iterator<T> iterator() {
        return (new SLItr(head)); //return from slitr class
    }

    private class SLItr implements Iterator<T> {

        Entry<T> head = null;//set next as null

        private SLItr(Entry<T> head) {
            this.head = head; //initialize head to the head of the list
        }
        
        @Override
        public T next() {
            Entry<T> next = head.next[0]; //set next to next element
            head = head.next[0]; //set head to head.next
            return next.element; //return next element
        }

        @Override
        public boolean hasNext() {
            //if the next element is not tail
            //element and not null, then return true otherwise return false
            return !(head.next[0] == tail || head.next[0] == null);

        }
    }

    //return last element in list
    public T last() {
    	  if (isEmpty()) { //return null if the list is empty
              return null;
          }
          Entry<T> p = head;//initialize p to head
          //loop through until element n is reached
          for (int k = 0; k < size; k++) {
              p = p.next[0];
          }
          //return element
          return p.element;
    }
    
    //remove element x
    public T remove(T x) {
    	 //return null if x is not in list
        //if element already exists don't add it
        if (!contains(x)) {
            return null;
        }

        Entry<T> entries2 = pred[0].next[0]; //craete new entry list
        int lvl = pred[0].next[0].height; //declare new entry
        for (int i = 0; i <= lvl - 1; i++) { //loop  through the list and increment each element up one place
            pred[i].next[i] = entries2.next[i];
        }
        size = size - 1; //decrease size by one
        return entries2.element;
    }

    // Return the number of elements in the list
    public int size() {
        return size;
    }
}
