package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;

import org.mazur.toparch.gui.utils.HQDrawer;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Drawer3D implements Drawer {

  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw3DHQ(canvas, 0, 0);
  }

  /* (non-Javadoc)
   * @see org.mazur.toparch.gui.drawer.Drawer#drawHop(java.awt.Graphics2D, int, int)
   */
  @Override
  public void drawHop(Graphics2D canvas, int i, int j) {
    // TODO Auto-generated method stub

  }

}
