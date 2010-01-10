package org.mazur.toparch.router;

import java.util.List;

/**
 * Input data for interpreter.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public interface InputData {
  List<LinkDescriptor> getKilled();
}
