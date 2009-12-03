package org.mazur.toparch.play;

import java.util.List;

/**
 * Play list of steps.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class PlayList {

  /** Name. */
  private String name;
  
  /** Steps list. */
  private List<StepInfo> stepsInfo;

  /**
   * @return the name
   */
  public String getName() { return name; }
  /**
   * @return the stepsInfo
   */
  public List<StepInfo> getStepsInfo() { return stepsInfo; }
  /**
   * @param name the name to set
   */
  public void setName(final String name) { this.name = name; }
  /**
   * @param stepsInfo the stepsInfo to set
   */
  public void setStepsInfo(final List<StepInfo> stepsInfo) { this.stepsInfo = stepsInfo; }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(name).append(":\n");
    if (stepsInfo == null) { return result.append("null").toString(); }
    for (StepInfo info : stepsInfo) { result.append(info).append("\n"); }
    return result.toString();
  }
}
