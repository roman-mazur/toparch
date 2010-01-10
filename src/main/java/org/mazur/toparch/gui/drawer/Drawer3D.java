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
public class Drawer3D implements Drawer {

  private void drawHop(Graphics2D canvas, int i, int j, final Color colorN, final Color colorL, final Color colorT) {
    if (Utils.isSameCluster(i, j, 3) && Math.abs(j - i) == 1) {
      HQDrawer.link3D(canvas, i, j, colorL);
    } else {
      if (i < j) {
        HQDrawer.drawArcBetween(canvas, i, j, 1, colorL);
      } else {
        HQDrawer.drawArcBetween(canvas, j, i, 1, colorL);
      }
    }
    HQDrawer.node3D(canvas, i, colorN, colorT);
    HQDrawer.node3D(canvas, j, colorN, colorT);
  }

  @Override
  public void clearHop(Graphics2D canvas, int i, int j) {
    drawHop(canvas, i, j, HQDrawer.NODES_COLOR, HQDrawer.LINK_COLOR, HQDrawer.TEXT_COLOR);
  }

  @Override
  public void drawBG(final Graphics2D canvas) {
    HQDrawer.draw3DHQ(canvas, 0, 0);
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
      if (Utils.isSameCluster(i, j, 3) && Math.abs(j - i) == 1) {
        HQDrawer.link3D(canvas, i, j, HQDrawer.LINK_COLOR_K);
      } else {
        if (i < j) {
          HQDrawer.drawArcBetween(canvas, i, j, 1, HQDrawer.LINK_COLOR_K);
        } else {
          HQDrawer.drawArcBetween(canvas, j, i, 1, HQDrawer.LINK_COLOR_K);
        }
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
      if (e.getValue() >= 4) { HQDrawer.node3D(canvas, e.getKey(), HQDrawer.NODES_COLOR_K, HQDrawer.TEXT_COLOR_K); }
    }
  }

  @Override
  public void drawMarked(Graphics2D canvas, Collection<Integer> nodes) {
    for (int n : nodes) {
      HQDrawer.node3D(canvas, n, HQDrawer.NODES_COLOR_M, HQDrawer.TEXT_COLOR);
    }
  }
}
