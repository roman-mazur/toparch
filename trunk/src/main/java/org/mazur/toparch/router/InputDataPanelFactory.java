package org.mazur.toparch.router;

import java.util.List;

import javax.swing.JPanel;

/**
 * Input data panel.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public abstract class InputDataPanelFactory<T extends InputData> {

  /** GUI. */
  private JPanel panel;
  
  /**
   * @return Swing panel
   */
  protected abstract JPanel createPanel(); 
  
  /**
   * @return GUI panel
   */
  public JPanel getPanel() {
    if (panel == null) { panel = createPanel(); }
    return panel;
  }
  
  /**
   * @return input data
   */
  public abstract T formData();
  
  public abstract List<LinkDescriptor> getKilled();
  
}
