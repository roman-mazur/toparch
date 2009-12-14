package org.mazur.toparch.router.many2many.personolized

import org.mazur.toparch.router.one2one.One2OneInputPanelFactory;

import javax.swing.border.EmptyBorder;

import java.util.List;

import javax.swing.JPanel;

import groovy.swing.SwingBuilder;

import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.many2many.M2MRouterInputs;

class M2MPInputsPanelFactory extends InputDataPanelFactory<M2MRouterInputs> {

  private SwingBuilder swing = new SwingBuilder()
  
  private def killedField
  
  @Override
  protected JPanel createPanel() {
    def insetBorder = swing.compoundBorder(swing.raisedEtchedBorder(), new EmptyBorder(5,5,5,5));
    return swing.panel() {
      hbox(border : insetBorder) {
        label("Killed nodes:")
        hstrut(width : 10)
        killedField = textField("enter value")
      }
    }
  }
  
  public List<LinkDescriptor> getKilled() { return One2OneInputPanelFactory.parseKilled(killedField.text) }
  
  public M2MRouterInputs formData() {
    M2MRouterInputs result = new M2MRouterInputs()
    result.setKilled(getKilled())
    return result
  }
  
}