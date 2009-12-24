package org.mazur.toparch.router.all2all;

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
    public int compare(final Object o1, final Object o2) {
      int[] m1 = (int[])o1, m2 = (int[])o2;
      if (m1[0] == m2[0] && m1[1] == m2[1]) { return 0; }
      int d1 = Utils.getRouteDistance(number, m1[1], State.INSTANCE.getDimension());
      int d2 = Utils.getRouteDistance(number, m2[1], State.INSTANCE.getDimension());
      int dif = d2 - d1;
      if (dif != 0) { return dif; }
//      if (m1[0] == number) { return 1; }
//      if (m2[0] == number) { return -1; }
      return 1000 * (m1[0] - m2[0]) + (m1[1] - m2[1]);
//      if (number == m1[0]) {
//        return -1;
//      } else if (number == m2[0]) {
//        return 1;
//      }
//      return 0;
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
  public void addMessage(final int from, final int to, final int variant) {
    this.messages.add(new int[] {from, to, variant});
  }
  
  public int removeMessage(final int from, final int to) {
    int[] m = (int[])this.messages.floor(new int[] {from, to, 0});
    assert m != null;
    this.messages.remove(m);
    return m[2];
  }
  
  public void copyMessages(final Node node) {
    this.messages = new TreeSet<Object>(node.messages);
  }
  
  public void markMessages(final int nextNode) {
    List<Object> toRemove = new LinkedList<Object>();
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int mNext = Utils.getNextNode(number, m[1], State.INSTANCE.getDimension());
      if (nextNode == mNext) { 
        m[2]++;
        if (m[2] == 4) { toRemove.add(m); }
      }
      if (mNext == -1) { toRemove.add(m); }
    }
    messages.removeAll(toRemove);
  }
  
  public int[] selectMessage(final int nextNode) {
    List<Object> toRemove = new LinkedList<Object>();
    int[] result = null;
    for (Object mo : messages) {
      int[] m = (int[])mo;
      int mNext = Utils.getNextNode(number, m[1], State.INSTANCE.getDimension());
      if (mNext == -1) { toRemove.add(m); }
      if (nextNode == mNext) { result = m; break; }
    }
    messages.removeAll(toRemove);
    return result;
  }
}
