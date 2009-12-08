package org.mazur.toparch.router.one2one;

import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.InputData;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class One2OneInputs implements InputData {

  /** Source node. */
  int source
  
  /** Destination node. */
  int destination
  
  /** Killed nodes. */
  List<LinkDescriptor> killed
  
}
