package org.mazur.toparch.play;

/**
 * Hop information.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class HopInfo {
  /** Source. */
  private int source;
  /** Destination. */
  private int destination;
  /** Description. */
  private String description;

  /**
   * @return the source
   */
  public int getSource() { return source; }
  /**
   * @return the destination
   */
  public int getDestination() { return destination; }
  /**
   * @return the description
   */
  public String getDescription() { return description; }
  /**
   * @param source the source to set
   */
  public void setSource(final int source) { this.source = source; }
  /**
   * @param destination the destination to set
   */
  public void setDestination(final int destination) { this.destination = destination; }
  /**
   * @param description the description to set
   */
  public void setDescription(final String description) { this.description = description; }
  
  @Override
  public String toString() {
    return "{" + source + " -> " + destination + ": " + description + "}";
  }
}
