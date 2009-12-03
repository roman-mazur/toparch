package org.mazur.toparch;

/**
 * State holder.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public final class State {

  private static final State INSTANCE = new State();
  
  private State() { } 
  
  /** Dimension. */
  private int dimension;

  /**
   * @return the dimension
   */
  public int getDimension() { return dimension; }

  /**
   * @param dimension the dimension to set
   */
  public void setDimension(final int dimension) {
    this.dimension = dimension;
  }
  
}
