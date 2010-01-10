package org.mazur.toparch.model;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.mazur.toparch.RouteFilter;
import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.router.LinkDescriptor;

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
    int l = length - res.length();
    char[] zeros = new char[l];
    for (int i = 0; i < l; i++) { zeros[i] = '0'; }
    sb.insert(0, zeros);
    return sb.toString();
  }
  
  protected String describeMessage(final Message m, final int zerosCount) {
    return formNumber(m.getSource(), zerosCount) + "," + formNumber(m.getDestination(), zerosCount);
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
      result[index++] = describeMessage(m, zerosCount);
    }
    return result; 
  }

  /**
   * @param number the number to set
   */
  public void setNumber(int number) { this.number = number; }

  /**
   * @param messages the messages to set
   */
  public void addMessage(final Message m) {
    assert m != null : "Attempt to add null message to node " + this;
    this.buffers.add(m.copy());
  }
  
  public Message removeMessage(final int from, final int to) {
    Message m = new Message(from, to, null);
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
  
  protected static boolean isKilled(final int src, final int dst, final List<LinkDescriptor> killed) {
    if (killed == null || killed.isEmpty()) { return false; }
    for (LinkDescriptor ld : killed) {
      if ((ld.getSource() == src && ld.getDestination() == dst)
          || (ld.getSource() == dst && ld.getDestination() == src)) {
        return true;
      }
    }
    return false;
  }
  
  public static RouteFilter createRouteFilter(final int number, final Message m, final List<LinkDescriptor> killed) {
    return new RouteFilter() {
      @Override
      public boolean accept(int nextNode) { 
        return !m.getVisitedNodes().get(nextNode) && !isKilled(number, nextNode, killed);
      }
    };
  }
  
  public Message selectMessage(final int nextNode, final List<LinkDescriptor> killed, final List<Message> removed) {
    Message result = null;
    int d = State.INSTANCE.getDimension();
    for (final Message m : buffers) {
      int mNext = Utils.getNextNode(number, m.getDestination(), d, createRouteFilter(number, m, killed));
      if (mNext == -1 && m.getDestination() != number) { 
        removed.add(m); 
      }
      if (nextNode == mNext) { result = m; break; }
    }
    return result;
  }
  
  protected TreeSet<Message> getBuffers() {
    return buffers;
  }
  
  @Override
  public String toString() {
    return "N" + number + "{buffersSize:" + buffers.size() + "}";
  }
}
