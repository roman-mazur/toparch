package org.mazur.toparch.router.one2one


import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.BoxLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.JPanel;

import groovy.swing.SwingBuilder;

import org.mazur.toparch.router.InputDataPanelFactory;

class One2OneInputPanelFactory extends InputDataPanelFactory<One2OneInputs> {
  
  private def sourceField, destField, killedFilled
  
  @Override
  protected JPanel createPanel() {
    SwingBuilder swing = new SwingBuilder()
    def insetBorder = new CompoundBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED), new EmptyBorder(5,5,5,5));
    return swing. panel(name : "Input parameters for one-to-one") {
      vbox() {
        hbox(border : insetBorder) {
          label("Source:")
          hstrut(width : 10)
          sourceField = textField("enter value")
        }
        hbox(border : insetBorder) {
          label("Destination:")
          hstrut(width : 10)
          destField = textField("enter value")
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
    return new One2OneInputs()
  }
}
