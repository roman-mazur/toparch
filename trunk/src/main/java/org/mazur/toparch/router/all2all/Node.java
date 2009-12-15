package org.mazur.toparch.router.all2all;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;

/**
 * Node containing messages.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Node {

  private int number;
  
  private Comparator<Object> mComparator = new Comparator<Object>() {
    @Override
    public int compare(Object o1, Object o2) {
      int[] m1 = (int[])o1, m2 = (int[])o2;
      return (m1[1] - m2[1]) * 1000 + (m1[0] - m2[0]);
    }
  };
  
  private TreeSet<Object> messages = new TreeSet<Object>(mComparator);

  /**
   * @return the number
   */
  public int getNumber() { return number; }

  public static String formNumber(final int number, final int length) {
    String res = String.valueOf(number);
    if (res.length() >= length) { return res; }
    StringBuilder sb = new StringBuilder(res);
    for (int i = 0; i < length - res.length(); i++) { sb.insert(0, "0"); }
    return sb.toString();
  }
  
  /**
   * @return the messages
   */
  public String[] getMessages() {
    String[] result = new String[messages.size()];
    int index = 0;
    int d = State.INSTANCE.getDimension();
    int n = Utils.getNodesCount(d);
    int zerosCount = 0;
    while (n > 0) { zerosCount++; n /= 10; }
    for (Object m : messages) {
      int[] message = (int[])m;
      result[index++] = formNumber(message[0], zerosCount) + "," + formNumber(message[1], zerosCount);
    }
    return result; 
  }

  /**
   * @param number the number to set
   */
  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * @param messages the messages to set
   */
  public void addMessage(final int from, final int to) {
    this.messages.add(new int[] {from, to});
  }
  
  public boolean removeMessage(final int from, final int to) {
    return this.messages.remove(new int[] {from, to});
  }
  
  public List<Object> findMessagesForJP(final int jumpPoint) {
    LinkedList<Object> result = new LinkedList<Object>();
    int d = State.INSTANCE.getDimension();
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int destination = m[1];
      if (destination == jumpPoint) {
        result.add(mo);
      } else {
        int connectJumpPoint = Utils.getFirstDestinationJP(number, destination, d);
        if (connectJumpPoint == jumpPoint) {
          result.add(mo);
        }
      }
    }
    return result;
  }

  public int[] findFirstForJump() {
    int d = State.INSTANCE.getDimension();
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int destination = m[1];
      if (destination == number) { continue; }
      int connectJumpPoint = Utils.getFirstDestinationJP(number, destination, d);
      if (connectJumpPoint == number) { return m; }
    }
    return null;
  }

  public List<Object> findNotMyMessages() {
    LinkedList<Object> result = new LinkedList<Object>();
    int d = State.INSTANCE.getDimension();
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int destination = m[1];
      if (destination == number) { continue; }
      int connectJumpPoint = Utils.getFirstDestinationJP(number, destination, d);
      if (connectJumpPoint == number) { continue; }
      result.add(mo);
    }  
    return new ArrayList<Object>(result);
  }

  public int[] findMaxNotMyMessage() {
    int[] result = null;
    int d = State.INSTANCE.getDimension();
    int cs = d << 1;
    int nodeIndex = number % cs;
    int maxD = 0;
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int destination = m[1];
      if (destination == number) { continue; }
      int connectJumpPoint = Utils.getFirstDestinationJP(number, destination, d);
      if (connectJumpPoint == number) { continue; }
      int distance = Utils.getInClusterCircleDistance(nodeIndex, connectJumpPoint % cs, d);
      if (result == null) {
        result = m;
        maxD = distance;
      } else if (maxD < distance) {
        result = m;
        maxD = distance;
      }
    }  
    return result;
  }
  
  public int[] findFirstNotMyMessage() {
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int destination = m[1];
      if (destination != number) { return m; }
    }  
    return null;
  }

  public void copyMessages(final Node node) {
    this.messages = new TreeSet<Object>(node.messages);
  }
}
