package org.mazur.toparch.router.many2many.personolized;

import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.Router;
import org.mazur.toparch.router.many2many.M2MRouterInputs;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class M2MPRouter extends Router<M2MRouterInputs> {

  @Override
  protected InputDataPanelFactory<M2MRouterInputs> createFactory() {
    return new M2MPInputsPanelFactory();
  }

  @Override
  public String getName() { return "many-to-many-personolized routing"; }

  @Override
  protected StepInfo next(final M2MRouterInputs input) {
    return null;
  }

  @Override
  public void reinit() {
  }

}
