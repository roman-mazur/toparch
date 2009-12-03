package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;

/**
 * The view drawer.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public interface Drawer {

  public void drawBG(final Graphics2D canvas);
  
  public void drawHop(final Graphics2D canvas, final int i, final int j);

  public void clearHop(final Graphics2D canvas, final int i, final int j);
}
