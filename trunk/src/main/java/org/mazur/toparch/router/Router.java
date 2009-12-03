package org.mazur.toparch.router;

import java.util.LinkedList;

import javax.swing.JPanel;

import org.mazur.toparch.play.PlayList;
import org.mazur.toparch.play.StepInfo;

/**
 * Router that does a model process.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public abstract class Router<T extends InputData> {

  /** Factory. */
  private InputDataPanelFactory<T> inputDataFactory;
  
  public Router() {
    inputDataFactory = createFactory();
  }
  
  /**
   * @return name
   */
  public abstract String getName();
  
  /**
   * @return GUI panel
   */
  public JPanel getGUIPanel() { return inputDataFactory.getPanel(); }
  
  protected abstract InputDataPanelFactory<T> createFactory();
  
  /**
   * Prepare.
   */
  public abstract void reinit();
  
  /**
   * @return the next step info
   */
  public StepInfo next() {
    return next(inputDataFactory.formData());
  }
  
  protected abstract StepInfo next(final T input);
  
  /**
   * @return play list
   */
  public PlayList process() {
    reinit();
    T inputData = inputDataFactory.formData();
    PlayList result = new PlayList();
    result.setName("Full play list for " + getName());
    result.setStepsInfo(new LinkedList<StepInfo>());
    while (true) {
      StepInfo si = next(inputData);
      if (si == null) { break; }
      result.getStepsInfo().add(si);
    }
    return result;
  }
  
}
