package org.mazur.toparch.test;

import static junit.framework.Assert.*;
import org.junit.Test;
import org.mazur.toparch.Utils;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class RoutingTest {

  @Test
  public void testNextInCluster() {
    final int d = 4;
    assertEquals(1, Utils.getNextInCluster(0, 1, d));
    assertEquals(4, Utils.getNextInCluster(0, 3, d));
  }
  
}
