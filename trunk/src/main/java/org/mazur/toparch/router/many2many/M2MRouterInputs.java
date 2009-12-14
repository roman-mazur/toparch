package org.mazur.toparch.router.many2many;

import java.util.List;

import org.mazur.toparch.router.InputData;
import org.mazur.toparch.router.LinkDescriptor;


/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class M2MRouterInputs implements InputData {

  /** Killed nodes. */
  List<LinkDescriptor> killed;

  /**
   * @return the killed
   */
  public List<LinkDescriptor> getKilled() {
    return killed;
  }

  /**
   * @param killed the killed to set
   */
  public void setKilled(final List<LinkDescriptor> killed) {
    this.killed = killed;
  }

}
