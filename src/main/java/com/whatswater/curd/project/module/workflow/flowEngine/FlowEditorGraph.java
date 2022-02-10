package com.whatswater.curd.project.module.workflow.flowEngine;



import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowEditorGraph {
    private FlowNode startNode;
    private int nodeCount;
    private int edgeCount;

    public FlowNode getStartNode() {
        return startNode;
    }

    public void setStartNode(FlowNode startNode) {
        this.startNode = startNode;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    public List<FlowNode> findAllNode() {
        List<FlowNode> tmp = new ArrayList<>();
        if (this.startNode != null) {
            tmp.add(this.startNode);
        }

        List<FlowNode> ret = new ArrayList<>();
        do {
            ret.addAll(tmp);
            List<FlowNode> children = new ArrayList<>();
            for (FlowNode node: tmp) {
                if (CollectionUtil.isEmpty(node.getFlowEditorEdge())) {
                    continue;
                }
                children.addAll(node.getFlowEditorEdge().stream().map(FlowEdge::getTargetNode).collect(Collectors.toList()));
            }
            tmp = children;
        } while (!tmp.isEmpty());
        return ret;
    }

    public List<FlowEdge> findAllEdge() {
        List<FlowNode> tmp = new ArrayList<>();
        if (this.startNode != null) {
            tmp.add(this.startNode);
        }

        List<FlowEdge> ret = new ArrayList<>();
        do {
            List<FlowNode> children = new ArrayList<>();
            for (FlowNode node: tmp) {
                if (CollectionUtil.isEmpty(node.getFlowEditorEdge())) {
                    continue;
                }
                ret.addAll(node.getFlowEditorEdge());
                children.addAll(node.getFlowEditorEdge().stream().map(FlowEdge::getTargetNode).collect(Collectors.toList()));
            }
            tmp = children;
        } while (!tmp.isEmpty());
        return ret;
    }

    public static FlowEditorGraph fromJson(String content) {
        FlowContent flowContent = CrudUtils.readValue(content, FlowContent.class);
        Map<String, List<FlowEditorEdge>> srcTargetList = flowContent.getEdgeList().stream().collect(Collectors.groupingBy(FlowEditorEdge::getSourceId));
        Map<String, FlowNode> idNodeMap = flowContent.getNodeList().stream().collect(Collectors.toMap(FlowNode::getId, Function.identity(), (prev, next) -> prev));

        FlowNode startNode = flowContent.getStartNode();

        FlowEditorGraph flowEditorGraph = new FlowEditorGraph();
        flowEditorGraph.setEdgeCount(flowContent.getEdgeList().size());
        flowEditorGraph.setNodeCount(flowContent.getNodeList().size());
        flowEditorGraph.setStartNode(startNode);

        List<FlowNode> flowNodeList = new ArrayList<>();
        flowNodeList.add(startNode);

        while (!CollectionUtil.isEmpty(flowNodeList)) {
            List<FlowNode> nextNodeList = new ArrayList<>();
            for (FlowNode flowNode: flowNodeList) {
                List<FlowEditorEdge> flowEditorEdgeList = srcTargetList.get(flowNode.getId());
                if (CollectionUtil.isNotEmpty(flowEditorEdgeList)) {
                    List<FlowEdge> nextList = flowEditorEdgeList.stream().map(flowEditorEdge -> {
                        FlowEdge flowEdge = new FlowEdge();
                        flowEdge.setRouteName(flowEditorEdge.getRouteName());
                        flowEdge.setSourceNode(flowNode);

                        FlowNode nextNode = idNodeMap.get(flowEditorEdge.getTargetId());
                        nextNodeList.add(nextNode);
                        flowEdge.setTargetNode(nextNode);

                        return flowEdge;
                    }).collect(Collectors.toList());
                    flowNode.setFlowEditorEdge(nextList);
                }
            }
            flowNodeList = nextNodeList;
        }

        return flowEditorGraph;
    }

    public static class FlowNode {
        private String id;
        private String shape;
        private String code;
        private String title;
        private String assigneeConfigType;
        private String assigneeConfig;
        private List<FlowLinkEventWithTrigger> flowLinkEventList;
        private List<FlowEdge> flowEdge;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getShape() {
            return shape;
        }

        public void setShape(String shape) {
            this.shape = shape;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAssigneeConfigType() {
            return assigneeConfigType;
        }

        public void setAssigneeConfigType(String assigneeConfigType) {
            this.assigneeConfigType = assigneeConfigType;
        }

        public String getAssigneeConfig() {
            return assigneeConfig;
        }

        public void setAssigneeConfig(String assigneeConfig) {
            this.assigneeConfig = assigneeConfig;
        }

        public List<FlowLinkEventWithTrigger> getFlowLinkEventList() {
            return flowLinkEventList;
        }

        public void setFlowLinkEventList(List<FlowLinkEventWithTrigger> flowLinkEventList) {
            this.flowLinkEventList = flowLinkEventList;
        }

        public List<FlowEdge> getFlowEditorEdge() {
            return flowEdge;
        }

        public void setFlowEditorEdge(List<FlowEdge> flowEdge) {
            this.flowEdge = flowEdge;
        }
    }

    public static class FlowEdge {
        private String routeName;
        private FlowNode sourceNode;
        private FlowNode targetNode;

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public FlowNode getSourceNode() {
            return sourceNode;
        }

        public void setSourceNode(FlowNode sourceNode) {
            this.sourceNode = sourceNode;
        }

        public FlowNode getTargetNode() {
            return targetNode;
        }

        public void setTargetNode(FlowNode targetNode) {
            this.targetNode = targetNode;
        }
    }

    public static class FlowEditorEdge {
        private String routeName;
        private String sourceId;
        private String targetId;

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class FlowContent {
        List<FlowEditorEdge> edgeList;
        List<FlowNode> nodeList;
        FlowNode startNode;

        public List<FlowEditorEdge> getEdgeList() {
            return edgeList;
        }

        public void setEdgeList(List<FlowEditorEdge> edgeList) {
            this.edgeList = edgeList;
        }

        public List<FlowNode> getNodeList() {
            return nodeList;
        }

        public void setNodeList(List<FlowNode> nodeList) {
            this.nodeList = nodeList;
        }

        public FlowNode getStartNode() {
            return startNode;
        }

        public void setStartNode(FlowNode startNode) {
            this.startNode = startNode;
        }
    }
}
