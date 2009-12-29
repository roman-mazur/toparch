package org.mazur.toparch.gui

import groovy.swing.SwingBuilder

import javax.swing.BoxLayout;
import javax.swing.UIManager;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.BorderLayout as BL
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.WindowConstants as WC
import org.apache.batik.swing.JSVGCanvas
import org.apache.batik.swing.JSVGScrollPane

import com.jgoodies.looks.FontSizeHints;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

import static org.mazur.toparch.gui.RoutingActions.*

UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE)
Options.setGlobalFontSizeHints(FontSizeHints.MIXED)
Options.setDefaultIconSize(new Dimension(18, 18))

try {
  UIManager.setLookAndFeel(LookUtils.IS_OS_WINDOWS_XP 
      ? Options.crossPlatformLookAndFeelClassName
      : Options.systemLookAndFeelClassName)
} catch (Exception e) {
  println ("Can't set look & feel:" + e)
}

def showDistribFrame = {
  SwingBuilder.build {
    JPanel internalCotainer
    JFrame internalFrame 
    internalFrame = frame(title : "������� ����������", pack : true, visible : true) {
      borderLayout()
      panel(constraints : BL.NORTH, border : titledBorder('Գ����')) {
        borderLayout()
        label(text : '�����', constraints : BL.WEST)
        def filterNodesText
        filterNodesText = textField(text : 'all', action : action(closure : {
          internalCotainer.removeAll()
          def filterNodes = null
          try {
            filterNodes = filterNodesText.text.split(/\s*,\s*/).collect() { Integer.parseInt it }
          } catch (def ignored) { println ignored.message }
          internalCotainer.add(buildLastStepMessages(filterNodes))
          internalFrame.pack()
        }))
      }
      scrollPane() {
        internalCotainer = panel() { borderLayout() }
      }
    }
  }
}

SwingBuilder.build {
  (mainFrame = frame(title : "toparch", pack : true, defaultCloseOperation : WC.EXIT_ON_CLOSE) {
    borderLayout()
    splitPane(constraints : BL.CENTER,
      dividerLocation : 500, 
      leftComponent : panel(border : compoundBorder(emptyBorder(2), titledBorder('���������� ������㳿'), emptyBorder(2))) {
        borderLayout()
        setSvgCanvas new JSVGCanvas()
        JSVGScrollPane scroll = new JSVGScrollPane(getSvgCanvas())
        scroll.setPreferredSize new Dimension(500, 500)
        widget(scroll)
      },
      rightComponent : panel(constraints : BL.EAST) {
        borderLayout()
        vbox(constraints : BL.NORTH) {
          panel(border : compoundBorder(emptyBorder(3), titledBorder('���������'))) {
            hbox() {
              vbox {
                panel(border : emptyBorder(1)) { label('���������') }
                panel(border : emptyBorder(1)) { label('�������������') }
              }
              vbox {
                comboBox(items : ['2D', '3D'], action : SELECT_DIMENSION)
                comboBox(items : getRouterNames(), action : SELECT_ROUTER)
              }
            }
          }
          hbox() {
            button(action : ZOOM_IN)
            button(action : ZOOM_OUT)
          }
          inputsContainer = panel()
          controlButtons = vbox(visible : false) {
            hbox() {
              button(action : MODEL_ALL) 
              button(action : MODEL_NEXT)
              button(action : RESET_ACTION)
            }
            hbox(border : compoundBorder(emptyBorder(2), titledBorder('�������'))) {
              label('��������  ')
              playTextArea = textField(text : '500')
              button(action : PLAY_ACTION)
              button(action : STOP_ACTION)
            }
          }
        }
        panel(border : compoundBorder(emptyBorder(2), titledBorder('���')), constraints : BL.CENTER) {
          borderLayout()
          scrollPane(size : [300, 100]) {
            routeTextArea = textArea(size : [300, 100])
          }
        }
      }
    )
    
    panel(constraints : BL.SOUTH) {
      borderLayout()
      scrollPane(constraints : BL.CENTER) {
        statusLabel = label(text : "������� �� ������ ;)")
      }
      button(constraints : BL.EAST, action : action(name : "�����������", closure : {
        showDistribFrame()
      }))
    }
  }).visible = true
}

initialize()
mainFrame.pack()
Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize()
int scrWidth = scrSize.width
int scrHeight = scrSize.height
int w = mainFrame.width, h = mainFrame.height
mainFrame.setLocation((scrWidth - w) >> 1,(scrHeight - h) >> 1)
