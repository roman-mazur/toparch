package org.mazur.toparch.router.multicast;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.one2one.One2OneInputPanelFactory;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class MulticastInputsFactory extends InputDataPanelFactory<MulticastInputs> {

  /** Text fields. */
  private JTextField sourceField, destField, killedField;
  private JCheckBox allCheckbox;
  
  @Override
  protected JPanel createPanel() {
    return (JPanel)MulticastInputPanelFactory.createPanel(new Closure(this) {
      private static final long serialVersionUID = 5923208476747988311L;
      @Override
      public Object call(Object[] args) {
        sourceField = (JTextField)args[0];
        destField = (JTextField)args[1];
        killedField = (JTextField)args[2];
        allCheckbox = (JCheckBox)args[3];
        return null;
      }
    });
  }

  @Override
  public MulticastInputs formData() {
    MulticastInputs result = new MulticastInputs();
    result.setSource(Integer.parseInt(sourceField.getText()));
    result.setKilled(getKilled());
    if (allCheckbox.isSelected()) {
      int n = Utils.getNodesCount(State.INSTANCE.getDimension());
      Integer[] dest = new Integer[n];
      for (int i = 0; i < n; i++) { dest[i] = i; }
      result.setDestinations(Arrays.asList(dest));
    } else {
      result.setDestinations(MulticastInputPanelFactory.parseDest(destField.getText()));
    }
    return result;
  }

  @Override
  public List<LinkDescriptor> getKilled() {
    return One2OneInputPanelFactory.parseKilled(killedField.getText());
  }

}
