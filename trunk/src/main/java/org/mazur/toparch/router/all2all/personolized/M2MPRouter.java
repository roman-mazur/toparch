package org.mazur.toparch.router.all2all.personolized;

import static org.mazur.toparch.Utils.*;

import org.mazur.toparch.State;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.Router;
import org.mazur.toparch.router.all2all.M2MRouterInputs;
import org.mazur.toparch.router.all2all.personolized.M2MPInputsPanelFactory;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class M2MPRouter extends Router<M2MRouterInputs> {

  private int[][] messagesDistribution;
  
  @Override
  protected InputDataPanelFactory<M2MRouterInputs> createFactory() { return new M2MPInputsPanelFactory(); }

  @Override
  public String getName() { return "many-to-many-personolized routing"; }

  @Override
  protected StepInfo next(final M2MRouterInputs input) {
    return null;
  }

  @Override
  public void reinit() {
    int d = State.INSTANCE.getDimension();
    int n = getNodesCount(d);
    messagesDistribution = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) { messagesDistribution[i][j] = j; }
    }
  }

}
