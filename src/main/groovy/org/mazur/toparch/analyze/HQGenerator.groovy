package org.mazur.toparch.analyze

import org.mazur.toparch.Utils;

class HQGenerator implements TopologyGenerator {
  
  /** Current step. */
  private int d = 4
  
  public boolean[][] nextMatrix() {
    d++
    int clusterSize = 2 * d
    def n = (3 ** d) * clusterSize
    boolean[][] result = new boolean[n][n]
    for (int i in 0..<n) {
      int[] near = Utils.getConnected(i, d)
      near.each { j -> result[i][j] = true  }
    }
    return result
  }
}
