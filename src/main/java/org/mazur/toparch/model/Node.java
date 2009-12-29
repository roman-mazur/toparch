package org.mazur.toparch.model;

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
  
  private Comparator<Message> mComparator = new Comparator<Message>() {
    @Override
    public int compare(final Message m1, final Message m2) {
      if (m1.equals(m2)) { return 0; }
      int d = State.INSTANCE.getDimension();
      int d1 = Utils.getRouteDistance(number, m1.getDestination(), d);
      int d2 = Utils.getRouteDistance(number, m2.getDestination(), d);
      int dif = d2 - d1;
      if (dif != 0) { return dif; }
      return 1000 * (m1.getSource() - m2.getSource()) + (m1.getDestination() - m2.getDestination());
    }
  };
  
  private TreeSet<Message> buffers = new TreeSet<Message>(mComparator);

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
    String[] result = new String[buffers.size()];
    int index = 0;
    int d = State.INSTANCE.getDimension();
    int n = Utils.getNodesCount(d);
    int zerosCount = 0;
    while (n > 0) { zerosCount++; n /= 10; }
    for (Message m : buffers) {
      result[index++] = formNumber(m.getSource(), zerosCount) + "," + formNumber(m.getDestination(), zerosCount);
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
  public void addMessage(final Message m) {
    assert m != null : "Attempt to add null message to node " + this;
    this.buffers.add(m.copy());
  }
  
  public Message removeMessage(final int from, final int to) {
    Message m = new Message(from, to);
    return removeMessage(m);
  }

  public Message removeMessage(final Message m) {
    Message result = buffers.floor(m);
    assert result != null;
    this.buffers.remove(result);
    return result;
  }
  
  public void copyMessages(final Node node) {
    this.buffers = new TreeSet<Message>(node.buffers);
  }
  
  public Message selectMessage(final int nextNode) {
    List<Object> toRemove = new LinkedList<Object>();
    Message result = null;
    for (Message m : buffers) {
      int mNext = Utils.getNextNode(number, m.getDestination(), State.INSTANCE.getDimension());
      if (mNext == -1) { toRemove.add(m); }
      if (nextNode == mNext) { result = m; break; }
    }
    buffers.removeAll(toRemove);
    return result;
  }
  
  @Override
  public String toString() {
    return "N" + number + "{buffersSize:" + buffers.size() + "}";
  }
}
