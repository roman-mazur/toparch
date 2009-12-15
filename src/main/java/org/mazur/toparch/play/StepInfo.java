package org.mazur.toparch.play;

import java.util.List;

/**
 * Collection of one step hops.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class StepInfo {

  /** Step. */
  private int step;
  
  /** List of hops. */
  private List<HopInfo> hopsInfo = null;

  /** Messages distribution. */
  private String[][] messagesDistribution;
  
  /**
   * @return the messagesDistribution
   */
  public String[][] getMessagesDistribution() {
    return messagesDistribution;
  }
  /**
   * @param messagesDistribution the messagesDistribution to set
   */
  public void setMessagesDistribution(String[][] messagesDistribution) {
    this.messagesDistribution = messagesDistribution;
  }

  /**
   * @return the step
   */
  public int getStep() { return step; }
  /**
   * @param step the step to set
   */
  public void setStep(final int step) { this.step = step; }
  /**
   * @return the hopsInfo
   */
  public List<HopInfo> getHopsInfo() { return hopsInfo; }
  /**
   * @param hopsInfo the hopsInfo to set
   */
  public void setHopsInfo(final List<HopInfo> hopsInfo) { this.hopsInfo = hopsInfo; }
  
  @Override
  public String toString() {
    if (hopsInfo == null || hopsInfo.size() < 3) {
      return "<Step" + step + ". " + hopsInfo + ">";
    }
    StringBuilder hopsString = new StringBuilder();
    if (hopsInfo != null) {
      for (HopInfo hi : hopsInfo) {
        hopsString.append("  ").append(hi).append("\n");
      }
    }
    return "<===============Step" + step + ".===============\n" + hopsString + "==============================>";
  }
}
