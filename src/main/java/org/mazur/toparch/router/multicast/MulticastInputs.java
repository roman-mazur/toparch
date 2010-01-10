package org.mazur.toparch.router.multicast;

import java.util.List;

import org.mazur.toparch.router.InputData;
import org.mazur.toparch.router.LinkDescriptor;

/**
 * Input data for multicast routing.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class MulticastInputs implements InputData {

  private int source;
  
  private List<Integer> destinations;
  
  
  private List<LinkDescriptor> killed;


  /**
   * @param source the source to set
   */
  public void setSource(int source) {
    this.source = source;
  }

  /**
   * @param destinations the destinations to set
   */
  public void setDestinations(List<Integer> destinations) {
    this.destinations = destinations;
  }

  /**
   * @param killed the killed to set
   */
  public void setKilled(List<LinkDescriptor> killed) {
    this.killed = killed;
  }

  /**
   * @return the source
   */
  public int getSource() { return source; }

  /**
   * @return the destinations
   */
  public List<Integer> getDestinations() { return destinations; }

  /**
   * @return the killed
   */
  @Override
  public List<LinkDescriptor> getKilled() { return killed; }
  
}
