package org.mazur.toparch.router.one2one

import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.Utils.NearInfo;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import java.util.HashSet;

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
  /** Visited nodes. */
  private HashSet<Integer> visited = new HashSet<Integer>()
  
  @Override
  public String getName() { return "one-to-one routing" }

  @Override
  public void reinit() {
    println "Reinit"
    currentNode = -1
    step = 0
    visited.clear()
  }

  private HopInfo hop(int s, int d, String descr) {
    HopInfo hop = new HopInfo()
    hop.source = s
    hop.destination = d
    hop.description = descr
    return hop
  }
  
  private boolean checkLink(final int node, final List<LinkDescriptor> killed) {
    for (LinkDescriptor ld in killed) {
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
    int distance = Utils.getInClusterDistance(cnIndex, dst, d) 
    int currentCluster = currentNode / cs
    println "Route in cluster to $dst from $cnIndex, distance: $distance"
    if (distance == 1 && !checkLink(currentCluster * cs + dst, inputs.killed)) {
      println "$dst is down"
      return null
    }
    def partners = [(cnIndex + 1) % cs, (cnIndex + d) % cs, cnIndex ? cnIndex - 1 : cs - 1].sort() { a, b ->
      Utils.getInClusterDistance(a, dst, d) <=> Utils.getInClusterDistance(b, dst, d)
    }
    println "Parteners: $partners"
    int next = partners.find {
      int node = currentCluster * cs + it
      return checkLink(node, inputs.killed) && !visited.contains(node) 
    }
    if (next == null) { return null }
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
    def closeJumpPoints = transitionClusters.collect { Utils.getNearInfo(currentCluster, it, d).getSource() }
    closeJumpPoints = closeJumpPoints.sort() { a, b -> 
      Utils.getInClusterDistance(a, currentNodeIndex, d) <=> Utils.getInClusterDistance(b, currentNodeIndex, d) 
    }
    closeJumpPoints += closeJumpPoints.collect { (it + (cs >> 1)) % cs } // other jump points
    println "Jump points: $closeJumpPoints"
    String msg = ""
    
    def tryJumpPoints = { jumpPoints ->
      for (int njp in jumpPoints) {
        int jumpPoint = currentCluster * cs + njp
        int nextNode = Utils.getNearClusterConnection(jumpPoint, d)
        println "current: $currentNode"
        println "Try jump point node: $jumpPoint -> $nextNode"
        if (visited.contains(nextNode)) {
          msg += "$nextNode was visited."
          println "$nextNode was visited."
          continue
        }
        if (jumpPoint == currentNode) {
          if (!checkLink(nextNode, input.killed)) {
            msg += "Jump link to $nextNode is down."
            println "Jump link to $nextNode is down."
            continue
          }
          currentNode = nextNode
          msg += "Jumping from $jumpPoint to $currentNode."
          return [hop(jumpPoint, currentNode, msg)]
        } else {
          if (visited.contains(jumpPoint)) { continue }
          msg += "Look for $jumpPoint to jump."
          def res = routeInCluster(njp, msg, d, input)
          if (res != null) { return res }
        }
      }
      return null
    }
    
    def res = tryJumpPoints(closeJumpPoints)
    if (res != null) { return res }
    
    println "Cannot reach by near axises. Go far :)"
    def goFarJP = []
    cs.times() { if (!closeJumpPoints.contains(it)) { goFarJP += it } }
    println "Try them: $goFarJP \n---------"
    
    return tryJumpPoints(goFarJP)
  }
  
  @Override
  protected StepInfo next(final One2OneInputs input) {
    int destNode = input.destination
    if (currentNode == destNode) { return null }
    visited.add currentNode
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
    if (result.hopsInfo == null || result.hopsInfo.empty) {
      println "Destination is unreachable"
      return null
    }
    return result
  }

  @Override
  protected InputDataPanelFactory<One2OneInputs> createFactory() { return new One2OneInputPanelFactory() }
}