package org.mazur.toparch.router.multicast;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mazur.toparch.State;
import org.mazur.toparch.Utils;
import org.mazur.toparch.model.Message;
import org.mazur.toparch.model.Node;
import org.mazur.toparch.play.HopInfo;
import org.mazur.toparch.play.StepInfo;
import org.mazur.toparch.router.InputDataPanelFactory;
import org.mazur.toparch.router.LinkDescriptor;
import org.mazur.toparch.router.Router;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class MulticastRouter extends Router<MulticastInputs> {

  /** Senders. */
  private HashSet<GenericNode> senders;
  
  /** Step. */
  private int step = 0, d;
  
  /** Nodes. */
  private List<GenericNode> nodes;
  
  private List<LinkDescriptor> killed;
  
  /** Busy nodes. */
  private BitSet busyNodes;
  
  /** Marked nodes. */
  private Set<GenericNode> markedNodes;
  
  @Override
  protected InputDataPanelFactory<MulticastInputs> createFactory() {
    return new MulticastInputsFactory();
  }
  @Override
  public String getName() { return "Мультикаст"; }

  private void addSender(final int number, final List<Integer> destinations) {
    GenericNode node = nodes.get(number);
    GenericMessage msg = new GenericMessage(number, destinations, new BitSet(Utils.getNodesCount(d)));
    node.addMessage(msg);
    senders.add(node);
  }
  
  @Override
  public Set<? extends Node> getMarkedNodes() { return markedNodes; }
  
  @Override
  protected StepInfo next(final MulticastInputs input) {
    step++;
    busyNodes.clear();
    killed = input.getKilled();
    if (senders == null) {
      senders = new HashSet<GenericNode>();
      addSender(input.getSource(), input.getDestinations());
    }
    
    StepInfo result = new StepInfo();
    result.setStep(step);
    List<HopInfo> hops = new LinkedList<HopInfo>();
    for (GenericNode s : new ArrayList<GenericNode>(senders)) {
      if (s.getMessages().length == 0) { senders.remove(s); } 
      GenericMessage m = s.nextMessage();
      if (m == null) { continue; }
      HopInfo hop = s.send(m); 
      if (hop == null) { continue; }
      hops.add(hop);
    }
    if (!hops.isEmpty()) { result.setHopsInfo(hops); }
    
    markedNodes.addAll(senders);
    if (result.getHopsInfo() == null) { return null; }
    return result;
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
  public void reinit() {
    d = State.INSTANCE.getDimension();
    step = 0;
    senders = null;
    int n = Utils.getNodesCount(d);
    busyNodes = new BitSet(n);
    nodes = new ArrayList<GenericNode>(n);
    for (int i = 0; i < n; i++) {
      GenericNode node = new GenericNode();
      node.setNumber(i);
      nodes.add(node);
    }
    markedNodes = new HashSet<GenericNode>(n);
  }

  /**
   * @version: $Id$
   * @author Roman Mazur (mailto: mazur.roman@gmail.com)
   */
  private class GenericNode extends Node {
    @Override
    public void addMessage(final Message m) {
      GenericMessage msg = (GenericMessage)m;
      Map<Integer, List<Integer>> mGroups = new HashMap<Integer, List<Integer>>();
      for (int dest : msg.destinations) {
        int next = Utils.getNextNode(getNumber(), dest, d, createRouteFilter(getNumber(), msg, killed));
        List<Integer> group = mGroups.get(next);
        if (group == null) { group = new ArrayList<Integer>(); mGroups.put(next, group); }
        group.add(dest);
      }
      for (Entry<Integer, List<Integer>> e : mGroups.entrySet()) {
        boolean exist = false;
        for (Message existedM : getBuffers()) {
          if (((GenericMessage)existedM).getPartner(getNumber()) == e.getKey()) {
            ((GenericMessage)existedM).destinations.addAll(e.getValue());
            exist = true;
            break;
          }
        }
        if (exist) { continue; }
        BitSet bs = new BitSet();
        bs.or(m.getVisitedNodes());
        GenericMessage nm = new GenericMessage(m.getSource(), e.getValue(), bs);
        nm.setDestination(e.getValue().get(0));
        nm.partner = e.getKey();
        super.addMessage(nm); 
      }
    }
    public GenericMessage nextMessage() {
      for (Message m : getBuffers()) {
        int next = Utils.getNextNode(getNumber(), m.getDestination(), d, createRouteFilter(getNumber(), m, killed));
        if (next == -1) { continue; }
        if (!busyNodes.get(next)) { return (GenericMessage)m; }
      }
      return null; 
    }
    public HopInfo send(final GenericMessage msg) {
      int partner = msg.getPartner(getNumber());
      if (partner < 0) { return null; }
      removeMessage(msg);
      msg.getVisitedNodes().set(getNumber());
      GenericNode dest = nodes.get(partner);
      dest.addMessage(msg);
      senders.add(dest);
      busyNodes.set(partner);
      HopInfo hop = new HopInfo();
      hop.setSource(getNumber());
      hop.setDestination(partner);
      hop.setDescription(msg.destinations.toString());
      return hop;
    }
    @Override
    protected String describeMessage(Message m, int zerosCount) {
      return m.getSource() + "," + ((GenericMessage)m).destinations;
    }
  }
  
  /**
   * @version: $Id$
   * @author Roman Mazur (mailto: mazur.roman@gmail.com)
   */
  private class GenericMessage extends Message {
    /** Destinations. */
    private List<Integer> destinations;
    private Integer partner;
    public GenericMessage(int source, List<Integer> destination, BitSet visitedNodes) {
      super(source, -1, visitedNodes);
      destinations = destination;
    }

    @Override
    public Message copy() {
      GenericMessage result = new GenericMessage(getSource(), destinations, getVisitedNodes());
      result.setDestination(getDestination());
      return result;
    }
    public int getPartner(final int number) {
      return partner != null ? partner : Utils.getNextNode(number, getDestination(), d, Node.createRouteFilter(number, this, killed));
    }
  }
  
}
