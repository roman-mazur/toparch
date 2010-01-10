package org.mazur.toparch.gui.drawer;

import java.awt.Graphics2D;
import java.util.Collection;

import org.mazur.toparch.router.LinkDescriptor;

/**
 * The view drawer.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public interface Drawer {

  void drawBG(final Graphics2D canvas);
  
  void drawHop(final Graphics2D canvas, final int i, final int j);

  void clearHop(final Graphics2D canvas, final int i, final int j);
  
  void drawKilled(final Graphics2D canvas, final Collection<LinkDescriptor> killed);
  
  void drawMarked(final Graphics2D canvas, final Collection<Integer> nodes);
  
}
