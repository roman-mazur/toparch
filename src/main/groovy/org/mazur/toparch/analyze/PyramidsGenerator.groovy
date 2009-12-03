package org.mazur.toparch.analyze

import java.util.Arrays
/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class PyramidsGenerator implements TopologyGenerator{

  private static boolean[][] START_MATRIX = [
    [true, true, true, true],
    [true, true, true, true],
    [true, true, true, true],
    [true, true, true, true]
  ]
  
  private int n = 4, base = 0
  
  private boolean[][] current, template = START_MATRIX
  
  private boolean[][] generate() {
    int n1 = current.length
    n += 12;
    println "N=$n"
    boolean[][] result = new boolean[n][n]
    n.times() { Arrays.fill(result[it], false) }
    //copy prior matrix
    current.length.times() {
      System.arraycopy(current[it], 0, result[it], 0, current.length)
    }
    // locate template (+12)
    int col = n1
    3.times() {
      template.eachWithIndex() { t, i ->
        System.arraycopy(t, 0, result[col + i], col, t.length)
      }
      result[base + it + 1][col] = result[col][base + it + 1] = true 
      col += template.length
    }
    base += template.length
    col -= template.length
    [
      [col + 1, col - 5],
      [col + 2, col - 1],
      [col - 3, col - 6]
    ].each() { result[it[0]][it[1]] = result[it[1]][it[0]] = true }
    
    return result
  }
  
  public boolean[][] nextMatrix(){
    return current = (current ? generate() : START_MATRIX)  
  }
  
}
