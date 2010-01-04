package org.mazur.toparch.router.one2all;

import groovy.swing.SwingBuilder;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class O2AInputsPanelFactory {

  private static SwingBuilder swing = new SwingBuilder()
  
  @Override
  public static JPanel createPanel(def setter) {
    def insetBorder = swing.compoundBorder(swing.raisedEtchedBorder(), new EmptyBorder(5,5,5,5));
    return swing.panel(name : "Input parameters for one-to-one") {
      vbox() {
        hbox(border : insetBorder) {
          label("Відправник:")
          hstrut(width : 10)
          sourceField = textField("0")
        }
        hbox(border : insetBorder) {
          label("Відмови:")
          hstrut(width : 10)
          killedField = textField("enter value")
        }
      }
    }
  }
  
}
