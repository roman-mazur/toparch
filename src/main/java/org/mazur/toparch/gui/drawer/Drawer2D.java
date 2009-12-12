package org.mazur.toparch.gui.drawer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mazur.toparch.Utils;
import org.mazur.toparch.gui.utils.HQDrawer;
import org.mazur.toparch.router.LinkDescriptor;

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
  
  private void drawHop(Graphics2D canvas, int i, int j, final Color colorN, final Color colorL, final Color colorT) {
    if (Utils.isSameCluster(i, j, 2) || isClustersDrawnNear(i / 4, j / 4)) {
      HQDrawer.link2D(canvas, i, j, colorL);
    } else {
      HQDrawer.arc2D(canvas, i, j, colorL);
    }
    HQDrawer.node2D(canvas, i, colorN, colorT);
    HQDrawer.node2D(canvas, j, colorN, colorT);
  }
  
  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, HQDrawer.NODES_COLOR, HQDrawer.LINK_COLOR, HQDrawer.TEXT_COLOR);
  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw2DHQ(canvas, 0, 0);
  }

  @Override
  public void drawHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, HQDrawer.NODES_COLOR_H, HQDrawer.LINK_COLOR_H, HQDrawer.TEXT_COLOR_H);
  }

  @Override
  public void drawKilled(Graphics2D canvas, Collection<LinkDescriptor> killed) {
    Map<Integer, Integer> counters = new HashMap<Integer, Integer>();
    for (LinkDescriptor ld : killed) {
      int i = ld.getSource();
      int j = ld.getDestination();
      if (Utils.isSameCluster(i, j, 2) || isClustersDrawnNear(i / 4, j / 4)) {
        HQDrawer.link2D(canvas, i, j, HQDrawer.LINK_COLOR_K);
      } else {
        HQDrawer.arc2D(canvas, i, j, HQDrawer.LINK_COLOR_K);
      }
      Integer counter = counters.get(i);
      if (counter == null) { counter = 0; }
      counter++;
      counters.put(i, counter);
      counter = counters.get(j);
      if (counter == null) { counter = 0; }
      counter++;
      counters.put(j, counter);
    }
    for (Entry<Integer, Integer> e : counters.entrySet()) {
      if (e.getValue() >= 4) { HQDrawer.node2D(canvas, e.getKey(), HQDrawer.NODES_COLOR_K, HQDrawer.TEXT_COLOR_K); }
    }
  }

}
