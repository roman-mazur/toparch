package org.mazur.toparch.gui

import groovy.swing.SwingBuilder

import javax.swing.BoxLayout;
import java.awt.BorderLayout as BL
import javax.swing.WindowConstants as WC
import org.apache.batik.swing.JSVGCanvas
import org.apache.batik.swing.JSVGScrollPane

import static org.mazur.toparch.gui.RoutingActions.*

SwingBuilder.build {
  (mainFrame = frame(title : "Routing display", pack : true, defaultCloseOperation : WC.EXIT_ON_CLOSE) {
    borderLayout()
    panel(constraints : BL.EAST) {
      borderLayout()
      vbox(constraints : BL.NORTH) {
        comboBox(items : ['2D', '3D'], action : SELECT_DIMENSION)
        comboBox(items : getRouterNames(), action : SELECT_ROUTER)
        button(action : ZOOM_IN)
        button(action : ZOOM_OUT)
        inputsContainer = panel()
        controlButtons = vbox(visible : false) {
          hbox() {
            button(action : MODEL_ALL) 
            button(action : MODEL_NEXT)
            button(action : RESET_ACTION)
          }
        }
      }
      scrollPane(size : [300, 100], constraints : BL.CENTER) {
        routeTextArea = textArea()
      }
    }
    panel(constraints : BL.CENTER) {
      borderLayout()
      setSvgCanvas new JSVGCanvas()
      JSVGScrollPane scroll = new JSVGScrollPane(getSvgCanvas())
      widget(scroll)
    }
    panel(constraints : BL.SOUTH) {
      statusLabel = label("Ready to work ;)")
    }
  }).visible = true
}

initialize()
