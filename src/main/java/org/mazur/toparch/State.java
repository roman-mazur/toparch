package org.mazur.toparch;

/**
 * State holder.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public final class State {

  public static final String[][] TEST_MESSAGES = new String[][] {
    {"123", "223"},
    {"123", "223", "323"}
  };
  
  public static final State INSTANCE = new State();
  
  private State() { /* hidden */ } 
  
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
