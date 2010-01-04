package org.mazur.toparch.router.one2all;

import java.util.List;

import org.mazur.toparch.router.InputData;
import org.mazur.toparch.router.LinkDescriptor;

public class O2AInputs implements InputData {

  /** Source. */
  private int source;
  
  /** Killed. */
  private List<LinkDescriptor> killed;

  /**
   * @return the source
   */
  public int getSource() { return source; }

  /**
   * @return the killed
   */
  public List<LinkDescriptor> getKilled() { return killed; }

  /**
   * @param source the source to set
   */
  public void setSource(final int source) { this.source = source; }

  /**
   * @param killed the killed to set
   */
  public void setKilled(final List<LinkDescriptor> killed) { this.killed = killed; }
  
}
