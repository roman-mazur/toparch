package org.mazur.toparch.router.multicast;

import groovy.swing.SwingBuilder;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class MulticastInputPanelFactory {

  private static SwingBuilder swing = new SwingBuilder()
  
  static def createPanel(def setter) {
    def insetBorder = swing.compoundBorder(swing.raisedEtchedBorder(), swing.emptyBorder(5,5,5,5));
    def killedField, sourceField, destField, allCheckbox
    def res = swing.vbox() {
      hbox(border : insetBorder) {
        label("Відправник:")
        hstrut(width : 10)
        sourceField = textField("0")
      }
      hbox(border : insetBorder) {
        label("Отримувачі:")
        hstrut(width : 10)
        destField = textField("5, 10, 11, 35, 33")
        hstrut(width : 10)
        allCheckbox = checkBox(action : action(
          name : "Усі",
          closure : { destField.enabled = !allCheckbox.selected }
        ))
      }
      hbox(border : insetBorder) {
        label("Відмови:")
        hstrut(width : 10)
        killedField = textField("enter value")
      }
    }
    setter(sourceField, destField, killedField, allCheckbox)
    return swing.panel() { borderLayout(); widget(res) }
  }
  
  static List<Integer> parseDest(final String text) {
    return text.split(/\s*,\s*/).collect { Integer.parseInt(it) }
  }
  
}
