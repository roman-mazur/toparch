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
    return [hop(prev, currentNode, msg + "; go to $currentNode")]
  }
  
  private def routeBetweenClusters(final int destCluster, final int d, final One2OneInputs input) {
    int cs = d << 1
    int currentCluster = currentNode / cs 
    int[] differentAxises = Utils.compareClusters(currentCluster, destCluster, d)
    def transitionClusters = differentAxises.collect() {
      int v = Utils.getDigit(destCluster, it)
      return Utils.setDigit(currentCluster, it, v)
    }
    println "Possible transition clusters: $transitionClusters"
    int currentNodeIndex = currentNode % cs
    def jumpPoints = transitionClusters.collect { Utils.getNearInfo(currentCluster, it, d).getSource() }
    jumpPoints = jumpPoints.sort() { a, b -> 
      Utils.getInClusterDistance(a, currentNodeIndex, d) <=> Utils.getInClusterDistance(b, currentNodeIndex, d) 
    }
    jumpPoints += jumpPoints.collect { it + (cs >> 1) } // other jump points
    println "Jump points: $jumpPoints"
    int njp = jumpPoints[0]
    int jumpPoint = currentCluster * cs + njp
    println "Jump point node: $jumpPoint"
    if (jumpPoint == currentNode) {
      currentNode = Utils.getNearClusterConnection(currentNode, d)
      String msg = "Jumping from $jumpPoint to $currentNode"
      return [hop(jumpPoint, currentNode, msg)]
    } else {
      String msg = "Look for $jumpPoint to jump"
      return routeInCluster(njp, msg, d, input)
    }
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
    } else {
      result.hopsInfo = routeBetweenClusters(destCluster, d, input)
    }
    return result
  }

  @Override
  protected InputDataPanelFactory<One2OneInputs> createFactory() { return new One2OneInputPanelFactory() }
}