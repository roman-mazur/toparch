package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;

import org.mazur.toparch.gui.utils.HQDrawer;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Drawer2D implements Drawer {

  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
    // TODO Auto-generated method stub

  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw2DHQ(canvas, 0, 0);
  }

  @Override
  public void drawHop(Graphics2D canvas, int i, int j) {
    // TODO Auto-generated method stub

  }

}
