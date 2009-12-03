package org.mazur.toparch.analyze

class MeshGenerator implements TopologyGenerator {
  
  /** Current dimension. */
  private int dimension = 0;
  
  private void setToResult(final boolean[][] result, def list) {
    list.each { result[it[0]][it[1]] = result[it[1]][it[0]] = true }
  }
  
  private void setCluster(final boolean[][] result, final int i) {
    int base = i * 6;
    setToResult result, [
      [base,     base + 1],
      [base,     base + 2],
      [base,     base + 3],
      [base,     base + 4],
      [base + 1, base + 3],
      [base + 1, base + 5],
      [base + 2, base + 3],
      [base + 2, base + 4],
      [base + 2, base + 5],
      [base + 3, base + 5],
      [base + 4, base + 5]
    ]
  }
  
  private void setInRowConnections(final boolean[][] result, final int row, final int dimension) {
    int base = row * dimension * 6;
    (dimension - 1).times {
      setToResult result, [
        [base + 1, base + 7],
        [base + 3, base + 6],
        [base + 5, base + 8],
        [base + 4, base + 10]
      ]
      base += 6
    }
  }
  
  private void setInColConnections(final boolean[][] result, final int col, final int dimension) {
    int base = col * 6;
    int inc = dimension * 6
    (dimension - 1).times {
      6.times { 
        int x = base + it, y = base + it + inc
        result[x][y] = result[y][x] = true
      }
      base += inc
    }
  }
  
  private void setMeshRowConnections(final boolean[][] result, final int row, final int dimension) {
    int base = row * dimension * 6
    int margin = base + dimension * 6
    setToResult result, [
      [base,     margin - 3],
      [base + 1, margin - 5],
      [base + 2, margin - 1],
      [base + 4, margin - 2]
    ]
  }
  
  private void setMeshColConnections(final boolean[][] result, final int col, final int dimension) {
    int base = col * 6
    int margin = base + (dimension - 1) * dimension * 6
    6.times { 
      int x = base + it, y = margin + it
      result[x][y] = result[y][x] = true
    }
  }

  boolean[][] nextMatrix() {
    dimension++
    println "Dimesion: $dimension"
    int clustersCount = dimension * dimension
    int n = clustersCount * 6
    println "N = $n"
    boolean[][] result = new boolean[n][n]
    n.times() { 
      Arrays.fill(result[it], false)
      result[it][it] = true
    }
    // set clusters
    clustersCount.times { setCluster(result, it) }

    dimension.times { 
      // set row
      setInRowConnections(result, it, dimension);
      // set column
      setInColConnections(result, it, dimension);
      // set mesh for rows
      setMeshRowConnections(result, it, dimension);
      // set mesh for cols
      setMeshColConnections(result, it, dimension);
    }
    return result               
  }
  
}