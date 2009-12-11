package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;

import org.mazur.toparch.Utils;
import org.mazur.toparch.gui.utils.HQDrawer;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Drawer3D implements Drawer {

  private void drawHop(Graphics2D canvas, int i, int j, final boolean h) {
    if (Utils.isSameCluster(i, j, 3) && Math.abs(j - i) == 1) {
      HQDrawer.link3D(canvas, i, j, h);
    } else {
      if (i < j) {
        HQDrawer.drawArcBetween(canvas, i, j, 1, h);
      } else {
        HQDrawer.drawArcBetween(canvas, j, i, 1, h);
      }
    }
    HQDrawer.node3D(canvas, i, h);
    HQDrawer.node3D(canvas, j, h);
  }

  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, false);
  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw3DHQ(canvas, 0, 0);
  }

  @Override
  public void drawHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, true);
  }

}
