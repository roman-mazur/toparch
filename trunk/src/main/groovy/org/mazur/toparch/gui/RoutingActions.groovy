package org.mazur.toparch.gui

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas
import org.w3c.dom.DOMImplementation;


import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel
import javax.swing.JPanel;
import org.mazur.toparch.State
import org.mazur.toparch.gui.drawer.Drawer
import org.mazur.toparch.gui.drawer.Drawer2D
import org.mazur.toparch.gui.drawer.Drawer3D
import org.mazur.toparch.router.Router
import org.mazur.toparch.play.HopInfo
import org.mazur.toparch.play.StepInfo
import org.mazur.toparch.router.one2one.One2OneRouter
import org.mazur.toparch.play.PlayList

import org.w3c.dom.Document

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
  private static Drawer selectedDrawer = drawers[2]
                                                    
  /** Status label. */
  static JLabel statusLabel
  
  /** Inputs data container. */
  static JPanel inputsContainer
  
  /** Main frame. */
  static def mainFrame
  
  /** Control buttons. */
  static def controlButtons
  
  /** Routes text area. */
  static def routeTextArea
  
  /** Current state. */
  private static RoutingState currentState = RoutingState.NONE
  
  /** Last step info. */
  private static StepInfo lastStep
  
  /** Current canvas. */
  private static Canvas currentCanvas
  
  public static JSVGCanvas getSvgCanvas() { return currentCanvas?.svgCanvas }
  
  public static void setSvgCanvas(final JSVGCanvas c) {
    currentCanvas = new Canvas(svgCanvas : c)
    AffineTransform at = c.getInitialTransform()
    at.setToScale(2, 2)
    c.resetRenderingTransform()
  }
  
  private static Document createDocument() {
    DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation()
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    Document document = domImpl.createDocument(svgNS, "svg", null)
    return document
  }
  
  private static void redraw() {
    currentCanvas.document = (SVGDocument)createDocument()
    currentCanvas.graphics = new SVGGraphics2D(currentCanvas.document)
    selectedDrawer.drawBG(currentCanvas.graphics)
    currentCanvas.commitDraw()
  }
  
  public static def getRouterNames() {
    return routers.collect { Router r -> r?.name }
  }
  
  private static void clearLastStep() {
    lastStep?.hopsInfo.each() { HopInfo hopInfo ->
      selectedDrawer.clearHop(currentCanvas.graphics, hopInfo.source, hopInfo.destination)
    }
    currentCanvas.commitDraw()
  }
  
  private static void drawCurrentStep(final StepInfo info) {
    info?.hopsInfo.each() { HopInfo hopInfo ->
      selectedDrawer.drawHop(currentCanvas.graphics, hopInfo.source, hopInfo.destination)
    }
    currentCanvas.commitDraw()
  }
  
  static SELECT_ROUTER = swing.action(
    name : "Select router",
    closure : { ActionEvent actionEvent ->
      def selName = actionEvent?.source?.selectedItem
      if (selName) {
        selectedRouter = routers.find { it?.name == selName }
      }
      statusLabel.text = "Selected router: ${selectedRouter?.name}"
      
      if (selectedRouter) {
        def panel = selectedRouter.getGUIPanel()
        inputsContainer.removeAll()
        inputsContainer.add(panel)
        mainFrame.pack()
        controlButtons.visible = true
      } else {
        controlButtons.visible = false
      }
    }
  )
  
  static SELECT_DIMENSION = swing.action(
    name : "Select dimension",
    closure : { ActionEvent actionEvent ->
      def selName = actionEvent?.source?.selectedItem
      switch (selName) {
      case '2D' : State.INSTANCE.dimension = 2; break 
      case '3D' : State.INSTANCE.dimension = 3; break 
      }
      statusLabel.text = "Selected dimension: ${State.INSTANCE.dimension}D"
 
      selectedDrawer = drawers[State.INSTANCE.dimension]
      redraw()
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
  
  static MODEL_ALL = swing.action(
    name : "Model all",
    closure : {
      if (currentState == RoutingState.ONE) {
        statusLabel.text = "You cannot model all at the current state. Finish the current process."
        return
      }
      currentState = RoutingState.ALL
      selectedRouter.reinit()
      PlayList pl = selectedRouter.process()
      pl.stepsInfo.each() {
        drawCurrentStep(it)
        routeTextArea.text = routeTextArea.text + it.toString() + "\n" 
      }
    }
  )
  static MODEL_NEXT = swing.action(
    name : "Model next",
    closure : {
      if (currentState == RoutingState.NONE) { selectedRouter.reinit() }
      if (currentState != RoutingState.ALL) { currentState = RoutingState.ONE }
      clearLastStep()
      StepInfo newStep = selectedRouter.next()
      if (!newStep) {
        currentState = RoutingState.NONE
        statusLabel.text = "Finished"
      } else {
        drawCurrentStep(newStep)
        String t = (newStep.hopsInfo.collect { it.description }).toListString()
        statusLabel.text = "$newStep.step $t"
      }
      lastStep = newStep
      routeTextArea.text = routeTextArea.text + lastStep.toString() + '\n'  
    }
  )
  
  static RESET_ACTION = swing.action(
    name : "Reset",
    closure : {
      currentState = RoutingState.NONE
      lastStep = null
      redraw()
      routeTextArea.text = ''
    }
  )
  
  static void initialize() {
    State.INSTANCE.dimension = 2
    [SELECT_DIMENSION, SELECT_ROUTER].each() { it.actionPerformed null }
  }
}

private enum RoutingState { ALL, ONE, NONE }

class Canvas {
  SVGGraphics2D graphics
  SVGDocument document
  JSVGCanvas svgCanvas
  
  void commitDraw() {
    Element root = document.getDocumentElement()
    graphics.getRoot(root)
    svgCanvas.setDocument(document)
  }
}
