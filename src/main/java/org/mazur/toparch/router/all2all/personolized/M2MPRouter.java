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
        public HopInfo getHop(final Node node) {
          System.out.println("Process node: " + node.getNumber() + " (circle-1 routing)");
          int d = State.INSTANCE.getDimension();
          int cs = d << 1;
          int[] message = node.findMaxNotMyMessage(-1);
          if (message == null) { return null; }
          StringBuilder description = new StringBuilder();
          int currentCluster = node.getNumber() / cs;
          int nodeIndex = node.getNumber() % cs;
          nodeIndex--;
          if (nodeIndex < 0) { nodeIndex = cs + nodeIndex; }
          int destination = currentCluster * cs + nodeIndex; 
          send(message, node.getNumber(), destination);
          description.append("M[").append(message[0]).append(",").append(message[1]).append("] ");
          HopInfo info = new HopInfo();
          info.setDescription(description.toString());
          info.setSource(node.getNumber());
          info.setDestination(destination);
          return info;
        }
      },

      // =================== opposite routing ===================
     new HopResolver() {
      @Override
      public HopInfo getHop(final Node node) {
        System.out.println("Process node: " + node.getNumber() + " (opposite routing)");
        int d = State.INSTANCE.getDimension();
        int[] m = node.findMaxNotMyMessage(d);
        if (m == null) { return null; }
        int cs = d << 1;
        int jp = (node.getNumber() % cs + d) % cs;
        int currentCluster = node.getNumber() / cs;
        int next = currentCluster * cs + jp;
        send(m, node.getNumber(), next);
        HopInfo info = new HopInfo();
        info.setSource(node.getNumber());
        info.setDestination(next);
        StringBuilder message = new StringBuilder();
        message.append("M[").append(m[0]).append(",").append(m[1]).append("] ");
        info.setDescription(message.toString());
        return info;
      }
    },
    
    // =================== circle routing ===================
    new HopResolver() {
      @Override
      public HopInfo getHop(final Node node) {
        System.out.println("Process node: " + node.getNumber() + " (opposite routing)");
        int d = State.INSTANCE.getDimension();
        int cs = d << 1;
        int[] message = node.findMaxNotMyMessage(1);
        if (message == null) { return null; }
        StringBuilder description = new StringBuilder();
        int currentCluster = node.getNumber() / cs;
        int nodeIndex = node.getNumber() % cs;
        int destination = currentCluster * cs + ((nodeIndex + 1) % cs); 
        send(message, node.getNumber(), destination);
        description.append("M[").append(message[0]).append(",").append(message[1]).append("] ");
        HopInfo info = new HopInfo();
        info.setDescription(description.toString());
        info.setSource(node.getNumber());
        info.setDestination(destination);
        return info;
      }
    },
    
    // =================== clusters routing ===================
    new HopResolver() {
      @Override
      public HopInfo getHop(final Node node) {
        System.out.println("Process node: " + node.getNumber() + " (clusters routing)");
        int d = State.INSTANCE.getDimension();
        int[] message = node.findFirstForJump();
        if (message == null) { return null; }
        StringBuilder description = new StringBuilder();
        int destination = Utils.getNearClusterConnection(node.getNumber(), d); 
        send(message, node.getNumber(), destination);
        description.append("M[").append(message[0]).append(",").append(message[1]).append("] ");
        HopInfo info = new HopInfo();
        info.setDescription(description.toString());
        info.setSource(node.getNumber());
        info.setDestination(destination);
        return info;
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
    sourceNode.removeMessage(m[0], m[1]);
    destinationNode.addMessage(m[0], m[1]);
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
      for (int j = 0; j < n; j++) { node.addMessage(i, j); }
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
  
  private List<HopInfo> runResolver(final HopResolver resolver) {
    List<HopInfo> hops = new LinkedList<HopInfo>();
    for (Node node : nodes) {
      HopInfo hi = resolver.getHop(node);
      if (hi != null) { hops.add(hi); }
    }
    return hops;
  }
  
  @Override
  protected StepInfo next(final M2MRouterInputs input) {
    copyNodes();
    StepInfo result = new StepInfo();
    result.setStep(step);
    HopResolver resolver = internalResolvers[step++ % internalResolvers.length];
    List<HopInfo> hops = runResolver(resolver);
    if (hops.isEmpty()) {
      nodes = nextStepNodes; 
      return null;
    }
    result.setHopsInfo(hops);
    nodes = nextStepNodes;
    result.setMessagesDistribution(formMDistrib());
    return result;
  }

  private interface HopResolver {
    HopInfo getHop(final Node node);
  }
}
