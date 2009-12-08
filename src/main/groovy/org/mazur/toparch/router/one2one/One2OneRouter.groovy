package org.mazur.toparch.router.one2one

import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.Utils.NearInfo;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.router.GroovyRouter;
import org.mazur.toparch.router.LinkDescriptor;

/**
 * One2One.
 */
class One2OneRouter extends GroovyRouter<One2OneInputs> {
  
  /** Current node. */
  private int currentNode
  /** Step number. */
  private int step 
  
  @Override
  public String getName() { return "one-to-one routing" }

  @Override
  public void reinit() {
    println "Reinit"
    currentNode = -1
    step = 0
  }

  private HopInfo hop(int s, int d, String descr) {
    HopInfo hop = new HopInfo()
    hop.source = s
    hop.destination = d
    hop.description = descr
    return hop
  }
  
  private boolean checkLink(final int node, final List<LinkDescriptor> killed) {
    for (LinkDescriptor ld : killed) {
      if ((ld.getSource() == currentNode && ld.getDestination() == node)
          || (ld.getSource() == node && ld.getDestination() == currentNode)) {
        return false
      }
    }
    return true
  }
  
  private def routeInCluster(final int dst, final String msg, final int d, final One2OneInputs inputs) {
    int cs = d << 1
    int cnIndex = currentNode % cs
    println "Route in cluster to $dst from $cnIndex"
    def partners = [(cnIndex + 1) % cs, (cnIndex + d) % cs, cnIndex ? cnIndex - 1 : cs - 1].sort() { a, b ->
      Utils.getInClusterDistance(a, dst, d) <=> Utils.getInClusterDistance(b, dst, d)
    }
    println "Parteners: $partners"
    int currentCluster = currentNode / cs
    int next = partners.find { checkLink(currentCluster * cs + it, inputs.killed) }
    int prev = currentNode
    currentNode = currentCluster * cs + next
    return [hop(prev, currentNode, msg)]
  }
  
  @Override
  protected StepInfo next(final One2OneInputs input) {
    int destNode = input.destination
    if (currentNode == destNode) { return null }
    step++
    
    int d = State.INSTANCE.dimension
    int cs = d << 1
    if (currentNode < 0) { currentNode = input.source }
    println "Routing $currentNode -> $destNode"

    int currentCluster = currentNode / cs
    int destCluster = destNode / cs
    println "Clusters: $currentCluster -> $destCluster"

    StepInfo result = new StepInfo()
    result.step = step
    
    if (currentCluster == destCluster) {
      String msg = "Come closer to $destNode"
      result.hopsInfo = routeInCluster(destNode % cs, msg, d, input)
      return result
    } else {
      NearInfo nearInfo = Utils.compareClusters(currentCluster, destCluster, d)
      int v = Utils.getDigit(destCluster, nearInfo.getAxis())
      int nextCluster = Utils.setDigit(currentCluster, nearInfo.getAxis(), v)
      NearInfo connectionInfo = Utils.getNearInfo(currentCluster, nextCluster, d)
      int njp = connectionInfo.getSource()
      int jumpPoint = currentCluster * cs + njp
      if (jumpPoint == currentNode) {
        currentNode = nextCluster * cs + connectionInfo.getDest()
        String msg = "Jumping from $jumpPoint to $currentNode"
        result.hopsInfo = [hop(jumpPoint, currentNode, msg)]
        return result
      } else {
        String msg = "Look for $jumpPoint to jump"
        result.hopsInfo = routeInCluster(njp, msg, d, input)
        return result
      }
    }
  }

  @Override
  protected InputDataPanelFactory<One2OneInputs> createFactory() { return new One2OneInputPanelFactory() }
}