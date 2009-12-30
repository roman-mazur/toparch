package org.mazur.toparch;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public interface RouteFilter {

  boolean accept(final int nextNode);
  
}
