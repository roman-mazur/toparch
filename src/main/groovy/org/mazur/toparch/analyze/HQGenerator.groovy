package org.mazur.toparch.analyze

class HQGenerator implements TopologyGenerator {
  
  /** Current step. */
  private int step = 0
  
  public boolean[][] nextMatrix() {
    step++
    int clusterSize = 2 * d
    def n = (3 ** d) * clusterSize
    boolean[][] result = new boolean[n][n]
    for (int i in 0..<n) {
      for (int j in 0..<n) {
        if ((int)(i / clusterSize) == (int)(j / clusterSize)) {
          result[i][j] = (Math.abs(i - j) == 1) || (Math.abs(i - j) == (clusterSize >> 1)) 
        } else {
          
        }
      }
    }
  }
}
