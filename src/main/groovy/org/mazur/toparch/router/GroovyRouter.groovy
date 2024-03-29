package org.mazur.toparch.router;

import org.mazur.toparch.play.StepInfo;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public abstract class GroovyRouter<T extends InputData> extends Router<T> {

  @Override
  public StepInfo next() { 
    return next(formData()) 
  }
  
  protected StepInfo next(final T input) { return null }
  
  protected T formData() { return inputDataFactory.formData() }
}
