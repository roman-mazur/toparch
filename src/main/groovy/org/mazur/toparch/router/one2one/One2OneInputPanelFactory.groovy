package org.mazur.toparch.router.one2one


import groovy.swing.SwingBuilder;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;

class One2OneInputPanelFactory extends InputDataPanelFactory<One2OneInputs> {
  
  private def sourceField, destField, killedField
  
  @Override
  protected JPanel createPanel() {
    SwingBuilder swing = new SwingBuilder()
    def insetBorder = swing.compoundBorder(swing.raisedEtchedBorder(), new EmptyBorder(5,5,5,5));
    return swing.panel(name : "Input parameters for one-to-one") {
      vbox() {
        hbox(border : insetBorder) {
          label("Source:")
          hstrut(width : 10)
          sourceField = textField("0")
        }
        hbox(border : insetBorder) {
          label("Destination:")
          hstrut(width : 10)
          destField = textField("3")
        }
        hbox(border : insetBorder) {
          label("Killed nodes:")
          hstrut(width : 10)
          killedField = textField("enter value")
        }
      }
    }
  }
  
  @Override
  public One2OneInputs formData() {
    String killedString = killedField.text
    def killedLinks = []
    if (killedString.trim().length() > 0) {
      killedLinks = killedString.split(/\s*[,;]\s*/).collect() {
        def nodes = it.split(/\s*-\s*/)
        if (nodes.size() > 1) {
          LinkDescriptor ld = new LinkDescriptor()
          ld.setSource(Integer.parseInt(nodes[0]))
          ld.setDestination(Integer.parseInt(nodes[1]))
          return ld
        }
        int s = Integer.parseInt(nodes[0])
        def connections = Utils.getConnected(s, State.INSTANCE.getDimension())
        return connections.collect() {
          LinkDescriptor ld = new LinkDescriptor()
          ld.setSource(s)
          ld.setDestination(it)
          return ld
        }
      }
    }
    println "killed: $killedLinks"
    return new One2OneInputs(
      source : Integer.parseInt(sourceField.text),
      destination : Integer.parseInt(destField.text)
    )
  }
}
