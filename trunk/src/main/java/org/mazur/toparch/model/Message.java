package org.mazur.toparch.model;

import java.util.BitSet;


/**
 * Message sent between nodes.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Message {

  /** Source. */
  private int source;
  
  /** Destination. */
  private int destination;

  /** Visited nodes. */
  private BitSet visitedNodes;
  
  /**
   * @return the visitedNodes
   */
  public BitSet getVisitedNodes() {
    return visitedNodes;
  }

  /**
   * @param visitedNodes the visitedNodes to set
   */
  public void setVisitedNodes(final BitSet visitedNodes) {
    this.visitedNodes = visitedNodes;
  }

  public Message(final int source, final int destination, final BitSet visitedNodes) {
    this.source = source;
    this.destination = destination;
    this.visitedNodes = visitedNodes;
  }
  
  /**
   * @return the source
   */
  public int getSource() { return source; }

  /**
   * @return the destination
   */
  public int getDestination() { return destination; }

  /**
   * @param source the source to set
   */
  public void setSource(final int source) { this.source = source; }

  /**
   * @param destination the destination to set
   */
  public void setDestination(final int destination) { this.destination = destination; }
 
  @Override
  public String toString() { return "M[" + source + ", " + destination + "]"; }
  
  @Override
  public boolean equals(Object obj) {
    Message m = (Message)obj;
    return m.source == source && m.destination == destination;
  }
  
  @Override
  public int hashCode() { return (source << 10) | destination; }

  public Message copy() {
    return new Message(source, destination, visitedNodes);
  }
  
}
