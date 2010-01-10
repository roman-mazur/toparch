package org.mazur.toparch.analyze

import org.mazur.toparch.Utils;

class HQClassicGenerator implements TopologyGenerator {
  
  /** Current step. */
  private int d = 4
  
  public boolean[][] nextMatrix() {
    d++
    def n = 2 ** d
    boolean[][] result = new boolean[n][n]
    for (int i in 0..<n) {
      d.times { 
        int j = i ^ (1 << it)
        result[i][j] = true
      }
    }
    return result
  }
}
