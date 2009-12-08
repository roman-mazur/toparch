package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;

import org.mazur.toparch.Utils;
import org.mazur.toparch.gui.utils.HQDrawer;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Drawer2D implements Drawer {

  private static boolean isClustersDrawnNear(final int ci, final int cj) {
    int ix = ci % 3, iy = ci / 3;
    int jx = cj % 3, jy = cj / 3;
    return (Math.abs(ix - jx) == 1 || Math.abs(iy - jy) == 1);
  }
  
  private void drawHop(Graphics2D canvas, int i, int j, final boolean h) {
    if (Utils.isSameCluster(i, j, 2) || isClustersDrawnNear(i / 4, j / 4)) {
      HQDrawer.link2D(canvas, i, j, h);
    } else {
      HQDrawer.arc2D(canvas, i, j, h);
    }
    HQDrawer.node2D(canvas, i, h);
    HQDrawer.node2D(canvas, j, h);
  }
  
  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, false);
  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw2DHQ(canvas, 0, 0);
  }

  @Override
  public void drawHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, true);
  }

}
