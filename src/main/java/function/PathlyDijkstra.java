package function;


import com.orientechnologies.common.collection.OMultiValue;
import com.orientechnologies.common.types.OModifiableBoolean;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.serialization.serializer.OStringSerializerHelper;
import com.orientechnologies.orient.core.sql.OSQLHelper;
import com.orientechnologies.orient.graph.sql.OGraphCommandExecutorSQLFactory;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Tkaewkunha on 1/22/16.
 */

public class PathlyDijkstra extends FindPath {
    public static final String NAME = "dijkstra";

    private String paramWeightFieldName;

    public PathlyDijkstra() {
        super(NAME, 3, 4);
    }

    public LinkedList<OrientVertex> executePathlyDij(Object iThis, OIdentifiable iCurrentRecord, Object iCurrentResult,
                                                     final Object[] iParams, OCommandContext iContext,
                                                     String first, String[] second) {

        final OModifiableBoolean shutdownFlag = new OModifiableBoolean();
        ODatabaseDocumentInternal curDb = ODatabaseRecordThreadLocal.INSTANCE.get();
        final OrientBaseGraph graph = OGraphCommandExecutorSQLFactory.getGraph(false, shutdownFlag);
        spurNode = first;
        ignoredNode = second;
        try {

            final ORecord record = iCurrentRecord != null ? iCurrentRecord.getRecord() : null;
            Object source = iParams[0];
            if (OMultiValue.isMultiValue(source)) {
                if (OMultiValue.getSize(source) > 1)
                    throw new IllegalArgumentException("Only one sourceVertex is allowed");
                source = OMultiValue.getFirstValue(source);
            }
            paramSourceVertex = graph.getVertex(OSQLHelper.getValue(source, record, iContext));

            Object dest = iParams[1];
            if (OMultiValue.isMultiValue(dest)) {
                if (OMultiValue.getSize(dest) > 1)
                    throw new IllegalArgumentException("Only one destinationVertex is allowed");
                dest = OMultiValue.getFirstValue(dest);
            }
            paramDestinationVertex = graph.getVertex(OSQLHelper.getValue(dest, record, iContext));


            paramWeightFieldName = OStringSerializerHelper.getStringContent(iParams[2]);
            if (iParams.length > 3) {
                paramDirection = Direction.valueOf(iParams[3].toString().toUpperCase());
            }
            return super.execute(iContext);
        } finally {
            if (shutdownFlag.getValue())
                graph.shutdown(false);
            ODatabaseRecordThreadLocal.INSTANCE.set(curDb);
        }
    }

    public Object execute(Object o, OIdentifiable oIdentifiable, Object o1, Object[] objects, OCommandContext oCommandContext) {
        return null;
    }

    public String getSyntax() {
        return "dijkstra(<sourceVertex>, <destinationVertex>, <weightEdgeFieldName>, [<direction>])";
    }

    protected float getDistance(final OrientVertex node, final OrientVertex target) {
        final Iterator<Edge> edges = node.getEdges(target, paramDirection).iterator();
        if (edges.hasNext()) {
            final Edge e = edges.next();
            if (e != null) {
                final Object fieldValue = e.getProperty(paramWeightFieldName);
                if (fieldValue != null)
                    if (fieldValue instanceof Float)
                        return (Float) fieldValue;
                    else if (fieldValue instanceof Number)
                        return ((Number) fieldValue).floatValue();
            }
        }
        return MIN;
    }

    @Override
    protected boolean isVariableEdgeWeight() {
        return true;
    }
}
