package org.mazur.toparch.router.all2all.personolized;

import static org.mazur.toparch.Utils.getNodesCount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.Router;
import org.mazur.toparch.router.all2all.A2AInputsFactory;
import org.mazur.toparch.router.all2all.M2MRouterInputs;
import org.mazur.toparch.router.all2all.Node;

/**
 * All to all personalized router.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class M2MPRouter extends Router<M2MRouterInputs> {

  private List<Node> nodes, nextStepNodes;
  
  private int step = 0;
  
  private HopResolver[] internalResolvers = new HopResolver[] {
      // =================== circle routing ===================
      new HopResolver() {
        @Override
        public int getNext(final int current) {
          int cs = State.INSTANCE.getDimension() << 1;
          int cluster = current / cs;
          int ci = current % cs;
          ci--;
          if (ci < 0) { ci += cs; }
          return cluster * cs + ci;
        }
      },

     // =================== opposite routing ===================
     new HopResolver() {
        @Override
        public int getNext(final int current) {
          int d = State.INSTANCE.getDimension();
          int cs = d << 1;
          int cluster = current / cs;
          int ci = current % cs;
          ci += d; ci %= cs;
          return cluster * cs + ci;
        }
    },
    
    // =================== circle routing ===================
    new HopResolver() {
      @Override
      public int getNext(final int current) {
        int cs = State.INSTANCE.getDimension() << 1;
        int cluster = current / cs;
        int ci = (current % cs + 1) % cs;
        return cluster * cs + ci;
      }
    },
    
    // =================== clusters routing ===================
    new HopResolver() {
      @Override
      public int getNext(final int current) {
        return Utils.getNearClusterConnection(current, State.INSTANCE.getDimension());
      }
    }
  };
  
  @Override
  protected InputDataPanelFactory<M2MRouterInputs> createFactory() { return new A2AInputsFactory(); }

  @Override
  public String getName() { return "all-to-all-personolized routing"; }

  private void send(int[] m, final int from, final int to) {
    Node sourceNode = nodes.get(from);
    sourceNode.removeMessage(m[0], m[1]);
    
    sourceNode = nextStepNodes.get(from);
    Node destinationNode = nextStepNodes.get(to);
    int v = sourceNode.removeMessage(m[0], m[1]);
    destinationNode.addMessage(m[0], m[1], v);
  }
  
  public String[][] formMDistrib() {
    String[][] result = new String[nodes.size()][];
    int index = 0;
    for (Node node : nodes) {
      result[index++] = node.getMessages();
    }
    return result;
  }

  private void copyNodes() {
    nextStepNodes = new ArrayList<Node>(nodes.size());
    for (Node node : nodes) {
      Node copy = new Node();
      copy.setNumber(node.getNumber());
      copy.copyMessages(node);
      nextStepNodes.add(copy);
    }
  }
  
  @Override
  public void reinit() {
    int d = State.INSTANCE.getDimension();
    int n = getNodesCount(d);
    nodes = new ArrayList<Node>(n);
    for (int i = 0; i < n; i++) {
      Node node = new Node();
      node.setNumber(i);
      for (int j = 0; j < n; j++) { node.addMessage(i, j, 0); }
      nodes.add(node);
    }
    step = 0;
  }
  
  protected boolean isKilled(final int src, final int dst, final List<LinkDescriptor> killed) {
    for (LinkDescriptor ld : killed) {
      if ((ld.getSource() == src && ld.getDestination() == dst)
          || (ld.getSource() == dst && ld.getDestination() == src)) {
        return true;
      }
    }
    return false;
  }
  
  private HopInfo resolve(final Node node, final HopResolver resolver, final List<LinkDescriptor> killed) {
    System.out.println("Process node: " + node.getNumber() + " (circle-1 routing)");
    int nextNode = resolver.getNext(node.getNumber());
    if (isKilled(node.getNumber(), nextNode, killed)) {
      node.markMessages(nextNode);
      return null;
    }
    int[] message = node.selectMessage(nextNode);
    if (message == null) { return null; }
    StringBuilder description = new StringBuilder();
    send(message, node.getNumber(), nextNode);
    description.append("M[").append(message[0]).append(",").append(message[1]).append("] ");
    HopInfo info = new HopInfo();
    info.setDescription(description.toString());
    info.setSource(node.getNumber());
    info.setDestination(nextNode);
    return info;
  }
  
  private List<HopInfo> runResolver(final HopResolver resolver, final List<LinkDescriptor> killed) {
    List<HopInfo> hops = new LinkedList<HopInfo>();
    for (Node node : nodes) {
      HopInfo hi = resolve(node, resolver, killed);
      if (hi != null) { hops.add(hi); }
    }
    return hops;
  }
  
  @Override
  protected StepInfo next(final M2MRouterInputs input) {
    copyNodes();
    StepInfo result = new StepInfo();
    int internalStep = 0;
    while (internalStep < internalResolvers.length) {
      HopResolver resolver = internalResolvers[step++ % internalResolvers.length];
      List<HopInfo> hops = runResolver(resolver, input.getKilled());
      if (hops.isEmpty()) {
        nodes = nextStepNodes; 
        internalStep++;
        continue;
      }
      result.setHopsInfo(hops);
      break;
    }
    nodes = nextStepNodes;
    if (result.getHopsInfo() == null) { return null; }
    result.setStep(step);
    result.setMessagesDistribution(formMDistrib());
    return result;
  }

  private interface HopResolver {
    int getNext(final int current);
  }
}
