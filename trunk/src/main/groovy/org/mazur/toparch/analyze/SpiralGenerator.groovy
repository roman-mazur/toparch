package org.mazur.toparch.analyze

import java.util.Arrays/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class SpiralGenerator implements TopologyGenerator {

  /** Square length. */
  private int a = 3

  /** Current matix. */
  private boolean[][] current
  
  /** Last edge indexes. */
  private def lastEdges
  
  private void init() {
    int n = 9
    current = new int[n][n]
    for (int i in 0..<n) {
      for (int j in 0..<n) {
        current[i][j] = i == j || i == j + 1 || i + 1 == j
      }
    }
    lastEdges = [7, 5, 3, 1]
    lastEdges.each() {
      current[0][it] = current[it][0] = true
    }
  }
  
  private boolean[][] extendMatrix() {
    int b = a + 2
    int n1 = a * a, n2 = b * b
    boolean[][] res = new boolean[n2][n2]
    n2.times() { Arrays.fill(res[it], false) }
    n1.times() { System.arraycopy(current[it], 0, res[it], 0, n1) }
    // line
    for (int i in n1..<(n2 - 1)) {
      res[i][i] = res[i][i - 1] = res[i][i + 1] = true
    }
    res[n2 - 1][n2 - 2] = res[n2 - 1][n2 - 1] = true
    // additional 4 links
    int index = n2 - (int)(b / 2) - 1
    def edges = new ArrayList(4)
    4.times() {
      edges += index
      index -= b - 1
    }
    for (int k in 0..<edges.size()) {
      int i = lastEdges[k], j = edges[k] 
      res[i][j] = res[j][i] = true
    }
    lastEdges = edges
    a = b
    return res
  }

  public boolean[][] nextMatrix(){
    if (!current) {
      init()
      return current
    }
    return current = extendMatrix()
  }
  
}
