package net.teamio.taam.util;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar {

	public static interface Navigator<T> {
		public WorldCoord getCoords(T object);
		public List<T> findNeighbors(T object, Node<T> predecessor);
	}

	public static class Node<T> implements Comparable<Node<T>> {
		public final T object;
		protected Integer dist = Integer.MAX_VALUE;
		protected Double value = 0d;
		protected Node<T> predecessor;

		public Node(T object, Double value) {
			this.object = object;
			this.value = value;
		}

		@Override
		public int compareTo(Node<T> o) {
			return value.compareTo(o.value);
		}

		public Node<T> getPredecessor() {
			return predecessor;
		}

		public int getStepDistance() {
			return dist;
		}
	}

	public static <T> Node<T> astar(T origin, T target, Navigator<T> navigator) {
		PriorityQueue<Node<T>> openlist = new PriorityQueue<Node<T>>();
		Set<Node<T>> closedlist = new HashSet<Node<T>>();
		openlist.add(new Node<T>(origin, 0d));

		Node<T> current;

		WorldCoord bctarget = navigator.getCoords(target);
		WorldCoord bccurrent = new WorldCoord();

		do {
			current = openlist.remove();

			if(current.object.equals(target)) {
				return current;
			}

			closedlist.add(current);

			for(T successor : navigator.findNeighbors(current.object, current)) {
				// skip attachments that are already being processed
				boolean found = false;
				for(Node<T> op : openlist) {
					if(op.object.equals(successor)) {
						found = true;
						break;
					}
				}
				for(Node<T> op : closedlist) {
					if(op.object.equals(successor)) {
						found = true;
						break;
					}
				}
				if(found) {
					continue;
				}

				int tentative_g = current.dist + 1;

				Node<T> foundS = null;
				for(Node<T> op : closedlist) {
					if(op.object.equals(successor)) {
						found = true;
						foundS = op;
						break;
					}
				}
				if(found && tentative_g >= foundS.dist) {
					continue;
				}

				if(!found) {
					foundS = new Node<T>(successor, 0d);
				}

				foundS.predecessor = current;
				foundS.dist = tentative_g;

				double f = tentative_g + bccurrent.set(navigator.getCoords(current.object)).subtract(bctarget).mag();
				foundS.value = f;
				if(found) {
					openlist.remove(foundS);
				}
				openlist.add(foundS);

			}


		} while(!openlist.isEmpty());
		return null;
	}
}
