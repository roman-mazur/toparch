package org.mazur.toparch.analyze

import java.util.Arrays

/**
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class FibonachiGenerator implements TopologyGenerator {

  private static boolean[][] START_TOPOLOGY = [
    [true, true, false],
    [true, true, true],
    [false, true, true]
  ]
  
  private int f1 = 2, f2 = 3
  
  private boolean[][] current = null
  
  private boolean[][] extendMatrix() {
    int f = f1 + f2
    f1 = f2
    f2 = f
    boolean[][] res = new boolean[f][f]
    f.times() { Arrays.fill(res[it], false) }
    f1.times() { System.arraycopy(current[it], 0, res[it], 0, f1) }
    res[f1 - 1][f1] = true
    res[f1 - 1][f - 1] = res[f - 1][f1 - 1] = true
    for (int i in f1..<(f - 1)) {
      res[i][i] = res[i][i - 1] = res[i][i + 1] = true
    }
    res[f - 1][f - 1] = res[f - 1][f - 2] = true
    return res
  }
  
  boolean[][] nextMatrix() {
    def res = !current ? (current = START_TOPOLOGY) : (current = extendMatrix())
//    res.each() {
//      it.each() { print "${it? 1 : 0} " }
//      println "" 
//    }
    return res
  }
  
}
