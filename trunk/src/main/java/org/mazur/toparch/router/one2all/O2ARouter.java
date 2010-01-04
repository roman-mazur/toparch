package org.mazur.toparch.router.one2all;

import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.Router;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class O2ARouter extends Router<O2AInputs> {

  @Override
  protected InputDataPanelFactory<O2AInputs> createFactory() { return new O2AInputsFactory(); }

  @Override
  public String getName() { return "ќдин до вс≥х"; }

  @Override
  protected StepInfo next(final O2AInputs input) {
    return null;
  }

  @Override
  public void reinit() {
  }

}
