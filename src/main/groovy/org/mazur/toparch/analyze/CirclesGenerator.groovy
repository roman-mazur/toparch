package org.mazur.toparch.analyze

/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class CirclesGenerator implements TopologyGenerator {
 
  private static final boolean[][] START_MATRIX = [
    [true, true, false, false, false, true],
    [true, true, true, false, false, false],
    [false, true, true, true, false, false],
    [false, false, true, true, true, false],
    [false, false, false, true, true, true],
    [true, false, false, false, true, true]
  ]
  
  private int lastIncrement = 6, iteration = 1
  
  private List<Integer> lastEdges = [0, 1, 2, 3, 4, 5]
  
  private boolean[][] currentMatrix
  
  private boolean[][] extendMatrix() {
    iteration++
    lastIncrement += 6
    int prevN = currentMatrix.length 
    int n = prevN + lastIncrement
    println "N = $n"
    boolean[][] result = new boolean[n][n]
    n.times() { Arrays.fill(result[it], false) }
    prevN.times() { System.arraycopy(currentMatrix[it], 0, result[it], 0, prevN) }
    // new circle
    lastIncrement.times() {
      int i = prevN + it
      [it, (it + 1) % lastIncrement, (it + lastIncrement - 1) % lastIncrement].each() {
        result[i][prevN + it] = true
      }
    }
    // additinal links
    def edges = []
    int e = prevN
    6.times() { 
      edges += e 
      int i = lastEdges[it], j = e + 1
      result[i][j] = result[j][i] = true
      e += iteration 
    }
    lastEdges = edges
    println "edges: $edges"
    return result
  }
  
  public boolean[][] nextMatrix() {
    return (currentMatrix = (currentMatrix ? extendMatrix() : START_MATRIX)) 
  }
  
}
