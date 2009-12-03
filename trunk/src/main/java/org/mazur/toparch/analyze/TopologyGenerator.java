package org.mazur.toparch.analyze;

/**
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public interface TopologyGenerator {

  /**
   * @return matrix for the next generation
   */
  boolean[][] nextMatrix();
  
}
