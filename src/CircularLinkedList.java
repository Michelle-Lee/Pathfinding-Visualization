package sample;

/*Minimally defined circular Linked List used to cycle
        through colors and create gradient for pathfinding*/

public class CircularLinkedList {

    class ListNode {
        String value;
        ListNode next;

        public ListNode(String val) {
            value = val;
        }

        public boolean hasNext() {
            return this.next != null;
        }
    }

    ListNode head;
    ListNode tail;
    ListNode currNode;

    public CircularLinkedList (){}

    public void add(String val) {
        ListNode newNode = new ListNode(val);

        if (head == null) {
            head = newNode;
            currNode = head;
        }
        else {
            tail.next = newNode;
        }

        tail = newNode;
        tail.next = head;
    }

    public String getNext() {
        currNode = currNode.next;
        return currNode.value;
    }
}