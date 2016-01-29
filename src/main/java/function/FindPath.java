package function;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.command.OCommandExecutorAbstract;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.sql.functions.math.OSQLFunctionMathAbstract;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import model.DijkstraResult;

import java.util.*;

/**
 * Created by Tkaewkunha on 1/23/16.
 */
public abstract class FindPath extends OSQLFunctionMathAbstract {
    //First Node
    protected String spurNode;
    protected String[] ignoredNode;

    protected OrientBaseGraph db;
    protected Set<OrientVertex> unSettledNodes;
    protected Map<ORID, OrientVertex> predecessors;
    protected Map<ORID, Float> distance;

    protected OrientVertex paramSourceVertex;
    protected OrientVertex paramDestinationVertex;
    protected Direction paramDirection = Direction.OUT;
    protected OCommandContext context;

    protected static final float MIN = 0f;

    public FindPath(final String iName, final int iMinParams, final int iMaxParams) {
        super(iName, iMinParams, iMaxParams);
    }

    protected DijkstraResult execute(final OCommandContext iContext) {
        context = iContext;
        unSettledNodes = new HashSet<OrientVertex>();
        distance = new HashMap<ORID, Float>();
        predecessors = new HashMap<ORID, OrientVertex>();
        distance.put(paramSourceVertex.getIdentity(), MIN);
        unSettledNodes.add(paramSourceVertex);

        int maxDistances = 0;
        int maxSettled = 0;
        int maxUnSettled = 0;
        int maxPredecessors = 0;

        while (continueTraversing()) {
            final OrientVertex node = getMinimum(unSettledNodes);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
            if (distance.size() > maxDistances)
                maxDistances = distance.size();
            if (unSettledNodes.size() > maxUnSettled)
                maxUnSettled = unSettledNodes.size();
            if (predecessors.size() > maxPredecessors)
                maxPredecessors = predecessors.size();

            if (!isVariableEdgeWeight() && distance.containsKey(paramDestinationVertex.getIdentity()))
                // FOUND
                break;

            if (!OCommandExecutorAbstract.checkInterruption(context))
                break;
        }

        context.setVariable("maxDistances", maxDistances);
        context.setVariable("maxSettled", maxSettled);
        context.setVariable("maxUnSettled", maxUnSettled);
        context.setVariable("maxPredecessors", maxPredecessors);
//        System.out.println("{{{{{{{{}}}}}}} distance.size = " + distance.size());
        if(distance.size() <= 1){
            distance = null;
            return null;
        }else {
            LinkedList<OrientVertex> result = getPath();
            DijkstraResult dijResult = new DijkstraResult(result, distance.get(result.get(result.size() - 1).getIdentity()));
            float[] rootPathCost = new float[result.size() - 1];
            for (int i = 0; i < result.size() - 1; i++) {
                rootPathCost[i] = distance.get(result.get(i).getIdentity());
            }
            dijResult.setRootPathCost(rootPathCost);
            distance = null;
            return dijResult;
        }
    }

    protected boolean isVariableEdgeWeight() {
        return false;
    }

    /*
     * This method returns the path from the source to the selected target and NULL if no path exists
     */
    public LinkedList<OrientVertex> getPath() {
        final LinkedList<OrientVertex> path = new LinkedList<OrientVertex>();
        OrientVertex step = paramDestinationVertex;
        // Check if a path exists
        if (predecessors.get(step.getIdentity()) == null)
            return null;

        path.add(step);
        while (predecessors.get(step.getIdentity()) != null) {
            step = predecessors.get(step.getIdentity());
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    public boolean aggregateResults() {
        return false;
    }

    @Override
    public Object getResult() {
        return getPath();
    }

    protected void findMinimalDistances(final OrientVertex node) {
        for (OrientVertex neighbor : getNeighbors(node)) {
//            System.out.println("- - get afterget Neighbors" + neighbor.getIdentity().toString());
            final float d = sumDistances(getShortestDistance(node), getDistance(node, neighbor));

            if (getShortestDistance(neighbor) > d) {
                distance.put(neighbor.getIdentity(), d);
                predecessors.put(neighbor.getIdentity(), node);
                unSettledNodes.add(neighbor);
            }
        }

    }

    protected Set<OrientVertex> getNeighbors(final Vertex node) {
        context.incrementVariable("getNeighbors");
        final Set<OrientVertex> neighbors = new HashSet<OrientVertex>();
        OrientVertex currentV = (OrientVertex) node;
        Iterable<Vertex> neighborList = node.getVertices(paramDirection);
        // System.out.println("Current V : "  + currentV.getIdentity().toString());
        if (node != null) {
            for (Vertex v : neighborList) {
                final OrientVertex ov = (OrientVertex) v;
                if (ov != null && isNotSettled(ov)) {
//                    System.out.println("- getNeighbors ov rid = " + ov.getIdentity().toString());
                    if (currentV.getIdentity().toString().equals(spurNode)) {
//                      System.out.println("In beware confition = " + currentV.getIdentity().toString());
                        if (!checkIngnore(ov.getIdentity().toString()))
                            neighbors.add(ov);
                    } else {
                        neighbors.add(ov);
                    }

                }

            }

        }
        return neighbors;
    }
    public boolean checkIngnore(String neigbor) {
        for (String ignore : ignoredNode) {
            if (neigbor.equals(ignore)) {
                return true;
            }
        }
        return false;
    }

    protected OrientVertex getMinimum(final Set<OrientVertex> vertexes) {

        OrientVertex minimum = null;
        Float minimumDistance = null;
        for (OrientVertex vertex : vertexes) {
            if (minimum == null || getShortestDistance(vertex) < minimumDistance) {
                minimum = vertex;
                minimumDistance = getShortestDistance(minimum);
            }
        }
        return minimum;
    }

    protected boolean isNotSettled(final OrientVertex vertex) {
        return unSettledNodes.contains(vertex) || !distance.containsKey(vertex.getIdentity());
    }

    protected boolean continueTraversing() {
        return unSettledNodes.size() > 0;
    }

    protected float getShortestDistance(final OrientVertex destination) {
        if (destination == null)
            return Float.MAX_VALUE;

        final Float d = distance.get(destination.getIdentity());
        return d == null ? Float.MAX_VALUE : d;
    }

    protected float sumDistances(final float iDistance1, final float iDistance2) {
        return iDistance1 + iDistance2;
    }

    protected abstract float getDistance(final OrientVertex node, final OrientVertex target);
}
