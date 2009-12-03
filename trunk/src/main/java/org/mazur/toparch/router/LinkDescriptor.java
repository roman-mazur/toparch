package org.mazur.toparch.router;

/**
 * Link descriptor.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class LinkDescriptor {

  /** Source node. */
  private int source;
  
  /** Destination. */
  private int destination;

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
  
  
  
}
