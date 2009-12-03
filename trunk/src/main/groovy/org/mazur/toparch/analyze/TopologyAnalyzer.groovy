package org.mazur.toparch.analyze

import java.util.BitSetimport java.util.Comparator/**
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class TopologyAnalyzer {
  
  /** Matrix. */
  boolean[][] matrix

  private int calcS() {
    return matrix.inject(0) { resultS, row ->
      int s = row.inject(0) { sum, value ->
        if (value) { sum++ }
        sum
      }
      s--
      if (resultS < s) { resultS = s }
      resultS
    }
  }
  
  private int minNotVisited(final int[] row, final BitSet visited) {
    int n = visited.nextClearBit(0), minValue = row[n] 
    int s = n
    for (int i in s..<row.length) {
      if (visited.get(i)) { continue; }
      if ((minValue > row[i] && row[i] >= 0) || minValue < 0) {
        minValue = row[i]
        n = i
      }
    }
    return visited.get(n) ? -1 : n
  }
  
  private int[][] formSorted(final int[] row) {
    int n = row.length
    int[][] result = new int[n][2]
    n.times() { result[it][0] = row[it]; result[it][1] = it }
    result = result.sort() { a, b->
      if (a[0] == b[0]) { return 0 }
      if (a[0] < b[0] && a[0] >= 0) { return -1 }
      if (a[0] > b[0] && b[0] >= 0) { return 1 }
      return a[0] < 0 ? 1 : -1
    }
    println "----\n$result"
    return result
  }
  
  private int[][] minDistances() {
    int n = matrix.length 
    int[][] result = new int[n][n]
    for (int i in 0 .. n - 1) {
      for (int j in 0 .. n - 1) { 
        result[i][j] = i == j ? 0 : matrix[i][j] ? 1 : -1 
      }
    }
    def sourceDistances = { int srcVertexIndex ->
      BitSet visited = new BitSet(n)
      int[] row = result[srcVertexIndex]
      int haveToFind = n - srcVertexIndex - 1
      row[srcVertexIndex] = 0
      visited.set(srcVertexIndex)
      n.times() {
        if (!haveToFind) { return }
        // i - current vertex index
        int i = minNotVisited(row, visited)
        matrix[i].eachWithIndex() { mv, index ->
          // if connected -> modify distance
          if (mv && i != index && !visited.get(index)) {  
            int d = row[i] + 1
            if (row[index] == -1 || row[index] > d) { row[index] = d }   
          }
        }
        visited.set(i)
        if (i >= srcVertexIndex) { haveToFind-- }
      }
    }
    int k = n.div(2)
    if (n % 2) { k++; }
    
    Thread t1 = new CThread(closure : {
      k.times() { sourceDistances(it) }
    })
    Thread t2 = new CThread(closure : {
      (n - k).times() { sourceDistances(k + it) }
    })
    t1.start()
    t2.start()
    t1.join()
    t2.join()
    // Duplicate symmetric
    result.eachWithIndex() { row, int index ->
      result.length.times() {
        result[it][index] = row[it]
      }
    }
    return result
  }
  
  public TopologyInfo analyze() {
    TopologyInfo result = new TopologyInfo()
    result.n = matrix.length
    result.s = calcS()
    int[][] md = minDistances()
    result.d = md.inject(md[0][0]) { resultMax, row ->
      int m = row.inject(resultMax) { max, current ->
        return max > current ? max : current
      }
      return resultMax > m ? resultMax : m
    }
    int x = 0, y = 0, max = md[0][0]
    for (int i in 0..<result.n) {
      for (int j in 0..<result.n) {
        if (md[i][j] > max) { x = i; y =j; max = md[i][j] }
      }
    }
    println "maxd: $x, $y -> $max"
    result.ad = md.inject(0) { resultSum, row ->
      resultSum += row.inject(0) { sum, current -> sum += current }
    }
    result.ad /= result.n * (result.n - 1)
    result.t = 2 * result.ad / result.s
    result.c = result.n * result.d * result.s
    return result
  }

}

class CThread extends Thread {
  def closure
  void run() { closure() }
}
