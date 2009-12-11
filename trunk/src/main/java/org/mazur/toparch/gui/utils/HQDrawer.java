package org.mazur.toparch.gui.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import org.mazur.toparch.Utils;
import org.mazur.toparch.Utils.NearInfo;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public final class HQDrawer {

  private static final int CLUSTER_GAP = 5;
  private static final int CLUSTER_SIZE = 40;
  private static final int NODES_RADIUS = 5;
  
  private static final Color NODES_COLOR = Color.BLUE; 
  private static final Color NODES_COLOR_H = Color.GREEN; 
  private static final Color LINK_COLOR = Color.GRAY; 
  private static final Color LINK_COLOR_H = Color.MAGENTA; 
  private static final Color TEXT_COLOR = Color.RED; 
  private static final Color TEXT_COLOR_H = Color.BLACK; 
  
  private static final int COUNT_3D = 27 * 6;
  private static final int RADIUS_3D = (int)(NODES_RADIUS * (COUNT_3D >> 1) * 0.8);
  private static final double ANGLED_3D = 2 * Math.PI / COUNT_3D;
  
  public static void node3D(final Graphics2D canvas, final int number, final boolean h) {
    node(canvas, get3DNodeX(number), get3DNodeY(number), number, h);
  }
  public static void node2D(final Graphics2D canvas, final int number, final boolean h) {
    node(canvas, get2DNodeX(number), get2DNodeY(number), number, h);
  }
  private static void node(final Graphics2D canvas, final int x, final int y, final int number, final boolean h) {
    int r = NODES_RADIUS, d = r << 1;
    Ellipse2D.Double circle = new Ellipse2D.Double(x - r, y - r, d, d);
    canvas.setPaint(h ? NODES_COLOR_H : NODES_COLOR);
    canvas.fill(circle);
    canvas.setPaint(h ? TEXT_COLOR_H : TEXT_COLOR);
    canvas.setFont(new Font(Font.MONOSPACED, 0, 5));
    canvas.drawString(String.valueOf(number), x - 3, y + 1);
  }
  
  public static void link2D(final Graphics2D canvas, final int i, final int j, final boolean h) {
    link(canvas, get2DNodeX(i), get2DNodeY(i), get2DNodeX(j), get2DNodeY(j), h);
  }
  public static void link3D(final Graphics2D canvas, final int i, final int j, final boolean h) {
    link(canvas, get3DNodeX(i), get3DNodeY(i), get3DNodeX(j), get3DNodeY(j), h);
  }
  private static void link(final Graphics2D canvas, final int x1, final int y1, final int x2, final int y2) {
    link(canvas, x1, y1, x2, y2, false);
  }
  private static void link(final Graphics2D canvas, final int x1, final int y1, final int x2, final int y2, final boolean h) {
    canvas.setPaint(h ? LINK_COLOR_H : LINK_COLOR);
    canvas.drawLine(x1, y1, x2, y2);
  }

  private static void draw4Cluster(final Graphics2D canvas, final int x, final int y, final int clusterNumber) {
    int a = CLUSTER_SIZE >> 1, b = a >> 1;
    int x1 = x + a, y1 = y + b;
    int x2 = x + CLUSTER_SIZE - b, y2 = y + a;
    int x3 = x + a, y3 = y + CLUSTER_SIZE - b;
    int x4 = x + b, y4 = y + a;
    link(canvas, x1, y1, x2, y2);
    link(canvas, x2, y2, x3, y3);
    link(canvas, x3, y3, x4, y4);
    link(canvas, x4, y4, x1, y1);
    link(canvas, x1, y1, x3, y3);
    link(canvas, x2, y2, x4, y4);
    int base = clusterNumber << 2;
    node(canvas, x1, y1, base + 1, false);
    node(canvas, x2, y2, base + 2, false);
    node(canvas, x3, y3, base + 3, false);
    node(canvas, x4, y4, base, false);
  }

  public static void arc2D(final Graphics2D canvas, final int i, final int j, final boolean h) {
    final int a = CLUSTER_SIZE >> 1, b = a >> 1;
    int x1 = get2DNodeX(i), y1 = get2DNodeY(i);
    int x2 = get2DNodeX(j), y2 = get2DNodeY(j);
    final int arc = 135;
    if (Math.abs(x1 - x2) < 2) { // vertical
      int topY = y1 > y2 ? y2 : y1;
      int bottomY = y1 > y2 ? y1 : y2;
      int rx = x1 + a;
      link(canvas, rx, topY + b, rx, bottomY - b, h);
      canvas.drawArc(x1, topY - b, a, CLUSTER_SIZE, 0, arc);
      canvas.drawArc(x1, bottomY - a - b, a, CLUSTER_SIZE, arc + 90, arc);
    } else {  // horizontal
      int leftX = x1 > x2 ? x2 : x1;
      int rightX = x1 > x2 ? x1 : x2;
      final int height = a + (CLUSTER_GAP >> 1);
      final int yy = y1 + height;
      link(canvas, leftX + b, yy, rightX - b, yy, h); 
      canvas.drawArc(leftX - b, y1, CLUSTER_SIZE, height, arc, arc);
      canvas.drawArc(rightX - a - b, y1, CLUSTER_SIZE, height, 2 * arc, arc);
    }
  }
  
  public static void draw2DHQ(final Graphics2D canvas, final int x0, final int y0) {
    int a = CLUSTER_SIZE >> 1;
    final int difference = CLUSTER_SIZE + CLUSTER_GAP;
    final int topY = a, bottomY = 3 * difference - CLUSTER_GAP - a,
              leftX = topY, rightX = bottomY;
    final int arc = 135;

    for (int i = 0; i < 3; i++) {
      int x = x0 + i * difference + a;
      for (int j = 0; j < 3; j++) {
        int y = y0 + j * difference + a;
        if (j > 0) { link(canvas, x, y, x, y - difference); }
        if (i > 0) { 
          link(canvas, x, y, x - difference, y);
        } else {
          final int h = a + (CLUSTER_GAP >> 1);
          final int yy = y + h;
          link(canvas, leftX, yy, rightX, yy); 
          canvas.drawArc(0, y, CLUSTER_SIZE, h, arc, arc);
          canvas.drawArc(2 * difference, y, CLUSTER_SIZE, h, 2 * arc, arc);
        }
        
      }
      int rx = x + a;
      link(canvas, rx, topY, rx, bottomY);
      canvas.drawArc(x, 0, a, CLUSTER_SIZE, 0, arc);
      canvas.drawArc(x, 2 * difference, a, CLUSTER_SIZE, arc + 90, arc);
    }

    
    for (int i = 0; i < 3; i++) {
      int x = x0 + i * (CLUSTER_SIZE + CLUSTER_GAP);
      for (int j = 0; j < 3; j++) {
        int y = y0 + j * (CLUSTER_SIZE + CLUSTER_GAP);
        draw4Cluster(canvas, y, x, i * 3 + j);
      }
    }
  }
  
  private static int get2DNodeX(final int number) {
    int b = CLUSTER_SIZE >> 2;
    int cluster = number / 4;
    int x = (cluster % 3) * (CLUSTER_SIZE + CLUSTER_GAP);
    int ni = number % 4;
    switch (ni) {
    case 2: x += b;
    case 3:
    case 1: x += b;
    case 0: x += b; 
    }
    return x;
  }
  private static int get2DNodeY(final int number) {
    int b = CLUSTER_SIZE >> 2;
    int cluster = number / 4;
    int x = (cluster / 3) * (CLUSTER_SIZE + CLUSTER_GAP);
    int ni = number % 4;
    switch (ni) {
    case 3: x += b;
    case 2:
    case 0: x += b;
    case 1: x += b; 
    }
    return x;
  }

  private static int get3DNodeX(final int number) {
    double a = ANGLED_3D * number;
    return (int)(RADIUS_3D * (1 + Math.cos(a))) + NODES_RADIUS;
  }
  private static int get3DNodeY(final int number) {
    double a = ANGLED_3D * number;
    return (int)(RADIUS_3D * (1 + Math.sin(a))) + NODES_RADIUS;
    
  }
  
  public static void drawArcBetween(final Graphics2D canvas, final int i, final int j, final int k, boolean highlight) {
    canvas.setPaint(highlight ? LINK_COLOR_H : LINK_COLOR);
    int x1 = get3DNodeX(i), y1 = get3DNodeY(i), x2 = get3DNodeX(j), y2 = get3DNodeY(j);
    int h = Math.abs(y1 - y2), w = Math.abs(x1 - x2); 
    int arc = 90;
    if (x2 > x1) { // top
      if (y2 > y1) { // right
        canvas.drawArc(x1, y1 - k * h, w * (1 + k) , h * (1 + k), 180, arc);
      } else { // left
        canvas.drawArc(x1 - k * w, y2 - k * h, w * (1 + k), h * (1 + k), 270, arc);
      }
    } else { // bottom
      if (y2 > y1) { // right
        canvas.drawArc(x2, y1, w * (1 + k), h * (1 + k), 90, arc);
      } else { // left
        canvas.drawArc(x2 - k * w, y2, w * (1 + k), h * (1 + k), 0, arc);
      }
    }
  }
  
  public static void draw3DHQ(final Graphics2D canvas, final int x, final int y) {
    final int clustersCount = 27;
    final int clusterSize = 6;
    for (int i = 0; i < clustersCount; i++) {
      int startNode = i * clusterSize;
      for (int j = 0; j < clusterSize - 1; j++) {
        int ni = startNode + j;
        link(canvas, get3DNodeX(ni), get3DNodeY(ni), get3DNodeX(ni + 1), get3DNodeY(ni + 1));
      }
      drawArcBetween(canvas, startNode, startNode + clusterSize - 1, 1, false);
      for (int j = 0; j < 3; j++) {
        drawArcBetween(canvas, startNode + j, startNode + j + 3, 1, false);
      }
      for (int j = i + 1; j < clustersCount; j++) {
        NearInfo info = Utils.getNearInfo(i, j, 3);
        if (info == null) { continue; }
        int ni = clusterSize * i + info.getSource();
        int nj = clusterSize * j + info.getDest();
        drawArcBetween(canvas, ni, nj, 1, false);
        //link(canvas, get3DNodeX(ni), get3DNodeY(ni), get3DNodeX(nj), get3DNodeY(nj));
      }
    }
    
    for (int i = 0; i < COUNT_3D; i++) {
      node(canvas, get3DNodeX(i), get3DNodeY(i), i, false);
    }
  }
  
  private HQDrawer() { }
}
