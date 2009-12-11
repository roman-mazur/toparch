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
    System.out.println(inputDataFactory);
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
    System.out.println("111: " + inputDataFactory);
    return next(formData());
  }
  
  protected abstract StepInfo next(final T input);
  
  protected T formData() { return inputDataFactory.formData(); }
  
  /**
   * @return play list
   */
  public PlayList process() {
    reinit();
    //T inputData = formData();
    PlayList result = new PlayList();
    result.setName("Full play list for " + getName());
    result.setStepsInfo(new LinkedList<StepInfo>());
    while (true) {
      StepInfo si = next();
      if (si == null) { break; }
      result.getStepsInfo().add(si);
    }
    return result;
  }
  
  protected InputDataPanelFactory<T> getInputDataFactory() {
    return inputDataFactory;
  }
  
}
