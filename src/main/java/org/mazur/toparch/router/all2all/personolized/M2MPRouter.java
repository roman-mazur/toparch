package org.mazur.toparch.router.all2all.personolized;

import static org.mazur.toparch.Utils.getNodesCount;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.model.Message;
import org.mazur.toparch.model.Node;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.Router;
import org.mazur.toparch.router.all2all.A2AInputsFactory;
import org.mazur.toparch.router.all2all.A2ARouterInputs;

/**
 * All to all personalized router.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class M2MPRouter extends Router<A2ARouterInputs> {

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
  protected InputDataPanelFactory<A2ARouterInputs> createFactory() { return new A2AInputsFactory(); }

  @Override
  public String getName() { return "Усі до всіх з персональним призначенням"; }

  private void send(Message m, final int from, final int to) {
    Node sourceNode = nodes.get(from);
    sourceNode.removeMessage(m);
    
    sourceNode = nextStepNodes.get(from);
    Node destinationNode = nextStepNodes.get(to);
    m = sourceNode.removeMessage(m);
    m.getVisitedNodes().set(from);
    destinationNode.addMessage(m);
  }
  
  public String[][] formMDistrib() {
    String[][] result = new String[nextStepNodes.size()][];
    int index = 0;
    for (Node node : nextStepNodes) {
      result[index++] = node.getMessages();
    }
    return result;
  }

  private void copyNodes() {
    if (nextStepNodes != null) { nodes = nextStepNodes; }
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
      for (int j = 0; j < n; j++) { node.addMessage(new Message(i, j, new BitSet(n))); }
      nodes.add(node);
    }
    step = 0;
  }
  
  private HopInfo resolve(final Node node, final HopResolver resolver, final List<LinkDescriptor> killed) {
    int nextNode = resolver.getNext(node.getNumber());
    LinkedList<Message> removed = new LinkedList<Message>();
    Message message = node.selectMessage(nextNode, killed, removed);
    for (Message toRemove : removed) {
      node.removeMessage(toRemove);
      nextStepNodes.get(node.getNumber()).removeMessage(toRemove);
    }
    if (message == null) { return null; }
    StringBuilder description = new StringBuilder();
    send(message, node.getNumber(), nextNode);
    description.append(message);
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
  protected StepInfo next(final A2ARouterInputs input) {
    System.out.println("Step " + step);
    copyNodes();
    StepInfo result = new StepInfo();
    int internalStep = 0;
    while (internalStep < internalResolvers.length) {
      HopResolver resolver = internalResolvers[step++ % internalResolvers.length];
      List<HopInfo> hops = runResolver(resolver, input.getKilled());
      if (hops.isEmpty()) {
        internalStep++;
        continue;
      }
      result.setHopsInfo(hops);
      break;
    }
    if (result.getHopsInfo() == null) { return null; }
    result.setStep(step);
    result.setMessagesDistribution(formMDistrib());
    return result;
  }

  private interface HopResolver {
    int getNext(final int current);
  }
}
