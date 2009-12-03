package org.mazur.toparch.router.one2one

import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.Router;

/**
 * One2One.
 */
class One2OneRouter extends Router<One2OneInputs> {
  
  /** Current node. */
  private int currentNode
  
  @Override
  public String getName() { return "one-to-one routing" }
  
  @Override
  public void reinit() {
    currentNode = -1
  }

  @Override
  protected StepInfo next(final One2OneInputs input) {
    if (currentNode < 0) { currentNode = input.source }
    return null
  }

  @Override
  protected InputDataPanelFactory<One2OneInputs> createFactory() { return new One2OneInputPanelFactory() }
}