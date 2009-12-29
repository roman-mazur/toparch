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
import org.mazur.toparch.router.LinkDescriptor

import org.mazur.toparch.model.Node;
import org.mazur.toparch.router.all2all.personolized.M2MPRouter;

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
  private static def routers = [new M2MPRouter(), new One2OneRouter()]
  
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
  
  /** Play period text area. */
  static def playTextArea
  
  /** Current state. */
  private static RoutingState currentState = RoutingState.NONE
  
  /** Last step info. */
  private static StepInfo lastStep
  
  /** Current canvas. */
  private static Canvas currentCanvas
  
  public static JSVGCanvas getSvgCanvas() { return currentCanvas?.svgCanvas }
  
  public static void setSvgCanvas(final JSVGCanvas c) {
    currentCanvas = new Canvas(svgCanvas : c)
    c.setSize(500, 400)
    //c.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
  }
  
  private static Document createDocument() {
    DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation()
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    Document document = domImpl.createDocument(svgNS, "svg", null)
    return document
  }
  
  private static void redraw() {
    currentCanvas.commitDraw() {
      currentCanvas.document = (SVGDocument)createDocument()
      currentCanvas.graphics = new SVGGraphics2D(currentCanvas.document)
      selectedDrawer.drawBG(currentCanvas.graphics)
    }
  }
  
  public static def getRouterNames() {
    return routers.collect { Router r -> r?.name }
  }
  
  private static void clearLastStep() {
//    lastStep?.hopsInfo.each() { HopInfo hopInfo ->
//      selectedDrawer.clearHop(currentCanvas.graphics, hopInfo.source, hopInfo.destination)
//    }
//    currentCanvas.commitDraw()
    redraw()
  }
  
  private static void drawCurrentStep(final StepInfo info) {
    currentCanvas.commitDraw() {
      info?.hopsInfo.each() { HopInfo hopInfo ->
        selectedDrawer.drawHop(currentCanvas.graphics, hopInfo.source, hopInfo.destination)
      }
    }
  }
  
  private static void drawKilled(final List<LinkDescriptor> killed) {
    currentCanvas.commitDraw() {
      selectedDrawer.drawKilled(currentCanvas.graphics, killed)
    }
  }

  public static def buildLastStepMessages(def filterNodes) {
    String[][] messages = null
    if (!lastStep?.messagesDistribution) {
      try {
        messages = selectedRouter.formMDistrib()
      } catch (def e) { return swing.label("No messages") }
    } else {
      messages = lastStep.messagesDistribution
    }
    return swing.panel(layout: swing.gridLayout(cols: 1, rows: messages.length)) {
      messages.eachWithIndex { nodeMessages, nodeIndex ->
        if (!filterNodes || filterNodes.contains(nodeIndex)) {
          hbox() {
            String index = Node.formNumber(nodeIndex, 3)
            label("Node ${index}: ")
            nodeMessages.each { label("  $it  |") }
          }
        }
      }
    }
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
    name : "Збільшити",
    closure : {
      AffineTransform at = svgCanvas.getRenderingTransform()
      at.setToScale(at.getScaleX() * 2, at.getScaleY() * 2)
      svgCanvas.setRenderingTransform(at)
    }
  )
  static ZOOM_OUT = swing.action(
    name : "Зменшити",
    closure : {
      AffineTransform at = svgCanvas.getRenderingTransform()
      at.setToScale(at.getScaleX() / 2, at.getScaleY() / 2)
      svgCanvas.setRenderingTransform(at)
    }
  )
  
  static MODEL_ALL = swing.action(
    name : "Усі кроки",
    closure : {
      if (currentState == RoutingState.ONE) {
        statusLabel.text = "You cannot model all at the current state. Finish the current process."
        return
      }
      currentState = RoutingState.ALL
      selectedRouter.reinit()
      PlayList pl = selectedRouter.process()
      drawKilled(selectedRouter.getInputDataFactory().getKilled())
      pl.stepsInfo.each() {
        drawCurrentStep(it)
        routeTextArea.text = routeTextArea.text + it.toString() + "\n" 
      }
    }
  )
  static MODEL_NEXT = swing.action(
    name : "Наступний крок",
    closure : {
      if (currentState == RoutingState.NONE) { selectedRouter.reinit() }
      if (currentState != RoutingState.ALL) { currentState = RoutingState.ONE }
      clearLastStep()
      drawKilled(selectedRouter.getInputDataFactory().getKilled())
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
    name : "Скинути",
    closure : {
      currentState = RoutingState.NONE
      lastStep = null
      redraw()
      routeTextArea.text = ''
    }
  )

  private static Thread playThread = null
  private static boolean playStop = false
  
  static PLAY_ACTION = swing.action(
    name : "Старт",
    closure : {
      playStop = false
      long delay = Long.parseLong(playTextArea.text)
      playThread = new CThread(action : {
        while (lastStep && !playStop) {
          MODEL_NEXT.actionPerformed(null)
          Thread.sleep delay
        }
        playThread = null
      })
      playThread.start()
    }
  )
  
  static STOP_ACTION = swing.action(
    name : "Стоп",
    closure : {
      playStop = true
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
  
  void commitDraw(c) {
//    def a = {
//      c()
//      Element root = document.getDocumentElement()
//      graphics.getRoot(root)
//      svgCanvas.setDocument(document)
//    }
//    def um = svgCanvas.getUpdateManager()
//    if (um) {
//      um.getUpdateRunnableQueue().invokeLater(new CThread(action : a))
//    } else {
//      a()
//    }
    c()
    Element root = document.getDocumentElement()
    graphics.getRoot(root)
    svgCanvas.setDocument(document)

  }
}

class CThread extends Thread {
  def action = null
  void run() { action() }
}
