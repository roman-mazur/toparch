package org.mazur.toparch;

public class Utils {

  /**
   * We have 2 connectors for the same axis: 0 and 1.
   * ---------
   * 0       1
   * x1-0x1-0x
   */
  private static byte[][] CONNECTORS = new byte[][] {
    {-1,  1,  0},
    { 0, -1,  1},
    { 1,  0, -1}
  };
  
  public static boolean isSameCluster(final int i, final int j, int d) {
    int cs = d << 1;
    return i / cs == j / cs;
  }
  
  public static NearInfo compareClusters(final int i, final int j, final int d) {
    if (i == j) { return null; }
    
    int a = i > j ? i : j;
    int b = i > j ? j : i;
    int axis = 0;
    int rd1 = 0, rd2 = 0;
    while (a > 0) {
      int d1 = a % 3, d2 = b % 3;
      if (d1 != d2) { 
        rd1 = i > j ? d1 : d2; 
        rd2 = i > j ? d2 : d1; 
        break; 
      }
      a /= 3; b /= 3;
      axis++;
    }
    int source = d * CONNECTORS[rd1][rd2] + axis;
    int destination = d * CONNECTORS[rd2][rd1] + axis;
    return new NearInfo(axis, source, destination);
  }
  
  /**
   * @param i source cluster
   * @param j destination cluster
   * @param dimension
   * @return near info
   */
  public static NearInfo getNearInfo(final int i, final int j, final int d) {
    int a = i > j ? i : j;
    int b = i > j ? j : i;
    int dc = 0;
    int axisIndex = 0, axis = -1;
    int rd1 = 0, rd2 = 0;
    while (a > 0) {
      int d1 = a % 3, d2 = b % 3;
      if (d1 != d2) { dc++; }
      if (dc > 1) { return null; }
      if (d1 != d2) {
        axis = axisIndex;
        rd1 = i > j ? d1 : d2; 
        rd2 = i > j ? d2 : d1; 
      }
      a /= 3; b /= 3;
      axisIndex++;
    }
    int source = d * CONNECTORS[rd1][rd2] + axis;
    int destination = d * CONNECTORS[rd2][rd1] + axis;
    return new NearInfo(axis, source, destination);
  }
  
  public static boolean isNear(final int i, final int j) {
    return getNearInfo(i, j, 0) != null;
  }

  public static boolean isConnected(final int i, final int j, final int k) {
    final int clusterSize = k << 1;
    int ci = i / clusterSize, cj = j / clusterSize; 
    if (ci == cj) {
      int d = Math.abs(i - j);
      return d == 1 || d == clusterSize - 1; 
    }
    NearInfo info = getNearInfo(ci, cj, k);
    if (info == null) { return false; }
    
    return info.source == i % clusterSize && info.dest == j % clusterSize;
  }
  
  public static int getDigit(int number, int d) {
    while(d-- > 0) { number /= 3; }
    return number % 3;
  }
  
  public static int setDigit(int number, final int d, final int v) {
    // save lower digits
    int[] saved = new int[d];
    for (int i = 0; i < d; i++) {
      saved[i] = number % 3;
      number /= 3;
    }
    // correction
    number = (number / 3) * 3 + v;
    // restore lower digits
    for (int i = d - 1; i >= 0; i--) {
      number *= 3;
      number += saved[i];
    }
    return number;
  }
  
  private static int getNearClusterConnection(final int node, final int d) {
    int cs = d << 1;
    int ci = node / cs;
    int ni = node % cs;
    int axis = ni % d;
    int sourceConnector = ni / d;
    int nearDigit = getDigit(ci, axis);
    System.out.println(nearDigit + " " + axis + " " + ci);
    nearDigit += sourceConnector == 0 ? -1 : 1;
    nearDigit %= 3;
    int nearCluster = setDigit(ci, axis, nearDigit);
    return nearCluster * cs + (ni + d) % cs;
  }
  
  /**
   * @param node node
   * @return array of connected nodes
   */
  public static Integer[] getConnected(final int node, final int d) {
    int cs = d << 1;
    int startNode = node / cs * cs;
    return new Integer[] {
      startNode + (node + 1) % cs,
      startNode + (node > 0 ? (node - 1) % cs : cs - 1),
      startNode + (node + d) % cs,
      getNearClusterConnection(node, d)
    };
  }

  public static int getInClusterDistance(final int i, final int j, final int d) {
    int dif = j - i;
    if (dif == 0) { return 0; }
    int sign = dif > 0 ? 1 : -1;
    dif = Math.abs(dif);
    int cs = d << 1;
    int next = 0;
    if (dif > d) {
      next = (i + (dif < d + (d >> 1) ? d : -sign)) % cs;
    } else {
      next = (i + (dif > d >> 1 ? d : sign)) % cs;
    }
    if (next < 0) { next = cs + next; }
    return getInClusterDistance(next, j, d) + 1; 
  }
  
  private Utils() {}
  
  public static class NearInfo {
    private int axis, source, dest;
    public NearInfo(final int axis, final int source, final int dest) { 
      this.axis = axis; 
      this.source = source; 
      this.dest = dest; 
    }
    /**
     * @return the axis
     */
    public int getAxis() { return axis; }
    /**
     * @return the source
     */
    public int getSource() { return source; }
    /**
     * @return the dest
     */
    public int getDest() { return dest; }
    
    @Override
    public String toString() {
      return "Near[axis = " + axis + ", " + source + " <-> " + dest + "]";
    }
  }
  
  public static void main(final String[] args) {
    System.out.println(getNearInfo(0, 3, 3));
  }
}
