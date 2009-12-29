package org.mazur.toparch.router.all2all.personolized


import org.mazur.toparch.router.all2all.A2AInputsFactory;
import org.mazur.toparch.router.one2one.One2OneInputPanelFactory;

import groovy.swing.SwingBuilder;

import javax.swing.border.EmptyBorder;

import java.util.List;

import javax.swing.JPanel;

import org.mazur.toparch.router.LinkDescriptor;

public class A2APInputsPanelFactory  {

  private static SwingBuilder swing = new SwingBuilder()
  
  @Override
  public static JPanel createPanel(def setter) {
    def insetBorder = swing.compoundBorder(swing.raisedEtchedBorder(), new EmptyBorder(5,5,5,5));
    return swing.panel() {
      hbox(border : insetBorder) {
        label("Killed nodes:")
        hstrut(width : 10)
        def killedField = textField("enter value")
        setter(killedField)
      }
    }
  }
  
}