package org.mazur.toparch.gui

import static org.mazur.toparch.gui.utils.HQDrawer.*

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas
import org.w3c.dom.DOMImplementation;

import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel
import javax.swing.JPanel;
import org.mazur.toparch.gui.drawer.Drawer
import org.mazur.toparch.State;
import org.mazur.toparch.gui.drawer.Drawer2D
import org.mazur.toparch.gui.drawer.Drawer3D
import org.mazur.toparch.router.Router;
import org.mazur.toparch.router.one2one.One2OneRouter;

import org.w3c.dom.Document;

import groovy.swing.SwingBuilder

/**
 * @author Roman Mazur
 */
class RoutingActions {

  private static SwingBuilder swing = new SwingBuilder()

  /** Drawers. */
  private static def drawers = [
    2 : new Drawer2D(),
    3 : new Drawer3D()
  ]
  
  /** Routers. */
  private static def routers = [new One2OneRouter(), null]
  
  /** Selected router. */
  private static Router<?> selectedRouter = routers[0]
  /** Selected drawer. */
  private static Drawer selectedDrawer
                                                    
  /** SVG canvas. */
  static JSVGCanvas svgCanvas

  /** Status label. */
  static JLabel statusLabel
  
  /** Inputs data container. */
  static JPanel inputsContainer
  
  /** Main frame. */
  static def mainFrame
  
  public static def getRouterNames() {
    return routers.collect { Router r -> r?.name }
  }
  
  private static Document createDocument() {
    DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation()
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    Document document = domImpl.createDocument(svgNS, "svg", null)
    return document
  }
  
  static SELECT_ROUTER = swing.action(
    name : "Select router",
    closure : { ActionEvent actionEvent ->
      def selName = actionEvent.source.selectedItem
      selectedRouter = routers.find { it?.name == selName }
      statusLabel.text = "Selected router: ${selectedRouter?.name}"
      
      if (selectedRouter) {
        def panel = selectedRouter.getGUIPanel()
        inputsContainer.removeAll()
        inputsContainer.add(panel)
        mainFrame.pack()
      }
    }
  )
  
  static SELECT_DIMENSION = swing.action(
    name : "Select dimension",
    closure : { ActionEvent actionEvent ->
      def selName = actionEvent.source.selectedItem
      switch (selName) {
      case '2D' : State.INSTANCE.dimension = 2; break 
      case '3D' : State.INSTANCE.dimension = 3; break 
      }
      statusLabel.text = "Selected dimension: ${State.INSTANCE.dimension}D"
 
      selectedDrawer = drawers[State.INSTANCE.dimension]
      SVGDocument doc = (SVGDocument)createDocument()
      SVGGraphics2D canvas = new SVGGraphics2D(doc)
      selectedDrawer.drawBG(canvas)
      Element root = doc.getDocumentElement()
      canvas.getRoot(root)
      svgCanvas.setSVGDocument(doc)
    }
  )
  
  static ZOOM_IN = swing.action(
    name : "Zoom in",
    closure : {
      AffineTransform at = svgCanvas.getRenderingTransform()
      at.setToScale(at.getScaleX() * 2, at.getScaleY() * 2)
      svgCanvas.setRenderingTransform(at)
    }
  )
  
  static ZOOM_OUT = swing.action(
    name : "Zoom out",
    closure : {
      AffineTransform at = svgCanvas.getRenderingTransform()
      at.setToScale(at.getScaleX() / 2, at.getScaleY() / 2)
      svgCanvas.setRenderingTransform(at)
    }
  )
}
