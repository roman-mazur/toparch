package org.mazur.toparch.router.one2all;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.model.Message;
import org.mazur.toparch.model.Node;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.HopResolver;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.Router;

/**
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class O2ARouter extends Router<O2AInputs> {

  /** Nodes. */
  private List<GenericNode> nodes = null;
  
  /** Senders and busy nodes. */
  private HashSet<GenericNode> senders, busyNodes = new HashSet<GenericNode>(),
                               markedNodes = new HashSet<GenericNode>();
  
  private int step = 0;
  
  @Override
  protected InputDataPanelFactory<O2AInputs> createFactory() { return new O2AInputsFactory(); }

  @Override
  public String getName() { return "ќдин до вс≥х"; }

  private void addSender(final GenericNode node, final List<LinkDescriptor> killed) {
    node.refreshMessages(killed);
    senders.add(node);
  }
  
  private GenericNode send(final Message m) {
    GenericNode source = nodes.get(m.getSource());
    source.sendCount++;
    GenericNode dest = nodes.get(m.getDestination());
    if (busyNodes.contains(dest)) { return null; }
    busyNodes.add(dest);
    dest.deniedPartners.add(source.getNumber());
    return dest;
  }
  
  public String[][] formMDistrib() {
    String[][] result = new String[nodes.size()][];
    int index = 0;
    for (Node node : nodes) {
      result[index++] = node.getMessages();
    }
    return result;
  }

  @Override
  public HashSet<GenericNode> getMarkedNodes() { return markedNodes; }
  
  @Override
  protected StepInfo next(final O2AInputs input) {
    busyNodes.clear();
    if (senders == null) {
      senders = new HashSet<GenericNode>();
      addSender(nodes.get(input.getSource()), input.getKilled());
    }
    markedNodes.addAll(senders);
    StepInfo result = new StepInfo();
    result.setStep(step++);
    List<HopInfo> hops = new LinkedList<HopInfo>();
    List<GenericNode> newSenders = new LinkedList<GenericNode>();
    for (GenericNode s : senders) {
      Message m = s.nextMessage();
      if (m == null) { continue; }
      GenericNode sender = send(m);
      if (sender == null) { continue; }
      sender.deniedPartners.add(input.getSource());
      newSenders.add(sender);
      HopInfo hop = new HopInfo();
      hop.setSource(m.getSource());
      hop.setDestination(m.getDestination());
      hop.setDescription(getName());
      hops.add(hop);
    }
    result.setHopsInfo(hops);
    
    // clean senders
    senders.addAll(newSenders);
    List<GenericNode> toRemove = new LinkedList<GenericNode>();
    for (GenericNode n : senders) { 
      if (!n.isSender()) { 
        toRemove.add(n); 
      } else {
        n.refreshMessages(input.getKilled());
      }
    }
    senders.removeAll(toRemove);
    
    if (result.getHopsInfo().isEmpty()) { return null; }
    return result;
  }

  @Override
  public void reinit() {
    markedNodes.clear();
    int d = State.INSTANCE.getDimension();
    int n = Utils.getNodesCount(d);
    nodes = new ArrayList<GenericNode>(n);
    for (int i = 0; i < n; i++) {
      GenericNode node = new GenericNode();
      node.setNumber(i);
      nodes.add(node);
    }
    step = 0;
    senders = null;
  }

  private static class GenericNode extends Node {
    private static HopResolver[] resolvers = null;
    static {
      HopResolver[] r = STANDARD_RESOLVERS;
      resolvers = new HopResolver[] { r[3], r[0], r[2], r[1] };
    }
    private int sendCount = 0;
    private Collection<Integer> deniedPartners = new HashSet<Integer>();
    public Message nextMessage() { return getBuffers().isEmpty() ? null : getBuffers().first(); }
    public boolean isSender() { return sendCount < resolvers.length; }
    public void refreshMessages(final List<LinkDescriptor> killed) {
      getBuffers().clear();
      int dest = -1;
      do {
        while (isSender() && deniedPartners.contains(dest = resolvers[sendCount].getNext(getNumber()))) { sendCount++; }
        if (isKilled(getNumber(), dest, killed)) { deniedPartners.add(dest); dest = -1; }
      } while (isSender() && dest == -1);
      if (dest >= 0 && isSender()) {
        addMessage(new Message(getNumber(), dest, new BitSet(2)));
      }
    }
  }
}
