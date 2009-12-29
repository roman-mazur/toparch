package org.mazur.toparch.router.all2all;

import groovy.lang.Closure;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.all2all.personolized.A2APInputsPanelFactory;
import org.mazur.toparch.router.one2one.One2OneInputPanelFactory;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class A2AInputsFactory extends InputDataPanelFactory<A2ARouterInputs> {

  private JTextField killedField;
  
  @Override
  public A2ARouterInputs formData() {
    A2ARouterInputs result = new A2ARouterInputs();
    result.setKilled(getKilled());
    return result;
  }

  @Override
  protected JPanel createPanel() {
    return A2APInputsPanelFactory.createPanel(new Closure(this) {
      private static final long serialVersionUID = 7049912459813675722L;
      @Override
      public Object call(final Object[] args) {
        return killedField = (JTextField)args[0];
      }
    });
  }

  @Override
  public List<LinkDescriptor> getKilled() {
    return One2OneInputPanelFactory.parseKilled(killedField.getText());
  }
  
}
