package org.mazur.toparch.router.one2all;

import groovy.lang.Closure;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.one2one.One2OneInputPanelFactory;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class O2AInputsFactory extends InputDataPanelFactory<O2AInputs> {

  /** Text fields. */
  private JTextField sourceField, killedField;
  
  @Override
  protected JPanel createPanel() {
    return O2AInputsPanelFactory.createPanel(new Closure(this) {
      private static final long serialVersionUID = 6097704068915570105L;
      @Override
      public Object call(final Object[] args) {
        sourceField = (JTextField)args[0];
        killedField = (JTextField)args[1];
        return null;
      }
    });
  }

  @Override
  public O2AInputs formData() {
    O2AInputs inputs = new O2AInputs();
    inputs.setSource(Integer.parseInt(sourceField.getText()));
    inputs.setKilled(getKilled());
    return inputs;
  }

  @Override
  public List<LinkDescriptor> getKilled() {
    return One2OneInputPanelFactory.parseKilled(killedField.getText());
  }

}
