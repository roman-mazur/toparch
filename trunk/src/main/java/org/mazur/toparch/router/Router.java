package org.mazur.toparch.router;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JPanel;

import org.mazur.toparch.State;
import org.mazur.toparch.model.Node;
import org.mazur.toparch.play.PlayList;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.Utils;

/**
 * Router that does a model process.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public abstract class Router<T extends InputData> {

  /** Factory. */
  private InputDataPanelFactory<T> inputDataFactory;
  
  protected static HopResolver[] STANDARD_RESOLVERS = new HopResolver[] {
      // =================== circle routing ===================
      new HopResolver() {
        @Override
        public int getNext(final int current) {
          int cs = State.INSTANCE.getDimension() << 1;
          int cluster = current / cs;
          int ci = current % cs;
          ci--;
          if (ci < 0) { ci += cs; }
          return cluster * cs + ci;
        }
      },

     // =================== opposite routing ===================
     new HopResolver() {
        @Override
        public int getNext(final int current) {
          int d = State.INSTANCE.getDimension();
          int cs = d << 1;
          int cluster = current / cs;
          int ci = current % cs;
          ci += d; ci %= cs;
          return cluster * cs + ci;
        }
    },
    
    // =================== circle routing ===================
    new HopResolver() {
      @Override
      public int getNext(final int current) {
        int cs = State.INSTANCE.getDimension() << 1;
        int cluster = current / cs;
        int ci = (current % cs + 1) % cs;
        return cluster * cs + ci;
      }
    },
    
    // =================== clusters routing ===================
    new HopResolver() {
      @Override
      public int getNext(final int current) {
        return Utils.getNearClusterConnection(current, State.INSTANCE.getDimension());
      }
    }
  };
  
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
  
  public Set<? extends Node> getMarkedNodes() { return Collections.emptySet(); }
  
  /**
   * Prepare.
   */
  public abstract void reinit();
  
  /**
   * @return the next step info
   */
  public StepInfo next() { return next(formData()); }
  
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
