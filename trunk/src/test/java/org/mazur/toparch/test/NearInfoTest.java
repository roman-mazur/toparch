package org.mazur.toparch.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mazur.toparch.Utils.getNearInfo;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mazur.toparch.Utils;
import org.mazur.toparch.Utils.NearInfo;

/**
 * Near info test.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class NearInfoTest {

  private static void assertIndexes(final int dimension, final int source, final int dest,
      final int sourceInClusterIndex, final int destInClusterIndex) {
    NearInfo info = getNearInfo(source, dest, dimension);
    assertEquals(sourceInClusterIndex, info.getSource());
    assertEquals(destInClusterIndex, info.getDest());
  }
  
  @Test
  public void test2D() {
    assertIndexes(2, 0, 1, 2, 0);
    assertIndexes(2, 0, 2, 0, 2);
    assertIndexes(2, 1, 0, 0, 2);
    assertIndexes(2, 1, 4, 3, 1);
    assertIndexes(2, 7, 1, 3, 1);
  }
  
  @Test
  public void test3D() {
    assertIndexes(3, 0, 1, 3, 0);
    assertIndexes(3, 0, 2, 0, 3);
    assertIndexes(3, 0, 3, 4, 1);
  }
  
  private static void assertSum(final int d) {
    int count = (int)Math.pow(3, d);
    for (int i = 0; i < count; i++) {
      int sum = 0;
      for (int j = 0; j < count; j++) {
        if (i != j && getNearInfo(i, j, d) != null) {
          sum++; 
        }
      }
      assertEquals("Got " + sum + " nearbyes for " + i + "th node. d=" + d, d << 1, sum);
    }
  }
  
  @Test
  public void testSum2D() { assertSum(2); }
  @Test
  public void testSum3D() { assertSum(3); }
  
  @Test
  public void testConnections() {
    List<Integer> connections = Arrays.asList(Utils.getConnected(13, 2));
    assertEquals(4, connections.size());
    System.out.println(connections);
    assertTrue(connections.contains(12));
    assertTrue(connections.contains(14));
    assertTrue(connections.contains(15));
    assertTrue(connections.contains(3));
    
    connections = Arrays.asList(Utils.getConnected(10, 2));
    assertTrue(connections.contains(9));
    assertTrue(connections.contains(11));
    assertTrue(connections.contains(8));
    assertTrue(connections.contains(0));
  }
}
