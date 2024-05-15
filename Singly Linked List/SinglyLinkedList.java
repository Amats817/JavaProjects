import java.util.*;

// Programming assignment 4
// Mark Joshua Sto Domingo
// Brandon Ovwigho
public class SinglyLinkedList<E> {

	public Entry<E> head;
	public int size;

	public static class Entry<E> {
		E element; // data
		Entry<E> next; // next node

		Entry(E element, Entry<E> next) {
			this.element = element;
			this.next = next;
		}
	}

	public boolean add(E element) {
		if (head == null) {
			head = new Entry<>(element, null);
		} else {
			Entry<E> current = head;

			while (current.next != null) { // find the last node
				current = current.next;
			}

			current.next = new Entry<>(element, null);
		}

		size++;
		return true;
	}

	public void add(int index, E element) {
		if (index < 0 || index > size) {
			throw new UnsupportedOperationException();
		}

		if (index == 0) {
			head = new Entry<>(element, head);
		} else {
			Entry<E> current = head;

			for (int i = 0; i < index - 1; i++) {
				current = current.next;
			}

			current.next = new Entry<>(element, current.next);
		}
		size++;
	}

	public E get(int index) {
		if (index < 0 || index >= size) {
			throw new UnsupportedOperationException();
		}

		Entry<E> current = head;
		for (int i = 0; i < index; i++) {
			current = current.next;
		}

		return current.element;
	}

	public E set(int index, E element) {
		if (index < 0 || index >= size) {
			throw new UnsupportedOperationException();
		}

		Entry<E> current = head;

		for (int i = 0; i < index; i++) {
			current = current.next;
		}

		E previous = current.element;
		current.element = element;
		return previous;
	}
}