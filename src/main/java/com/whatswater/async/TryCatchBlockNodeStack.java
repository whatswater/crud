package com.whatswater.async;


import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TRY CATCH STACK
 public void test() {
     // code not in try catch block，while <code>currentTryCatchBlockNode</code> is empty
     try {
         // code in out try catch block，while <code>currentTryCatchBlockNode</code> has one element Out-Try-Catch-Block
         try {
             // code in inner try catch block，while <code>currentTryCatchBlockNode</code> has two element In-Try-Catch-Block
         } catch (RuntimeException e1) {
             // code in handler block，while <code>currentTryCatchBlockNode</code> has one element Out-Try-Catch-Block
         }
         // code in out try catch block，while <code>currentTryCatchBlockNode</code> has one element Out-Try-Catch-Block
    } catch(Exception e2) {
         // code not in try catch block，while <code>currentTryCatchBlockNode</code> is empty
    }
 }

 <code>offsetMap</code> - a map of (start-label-offset or end-label-offset) to try catch node
 <code>currentTryCatchBlockNode</code> current try catch stack

 <code>consumerLabelNode</code> in labelNode，change <code>currentTryCatchBlockNode</code>
 */
public class TryCatchBlockNodeStack {
    final Map<Integer, TryCatchBlockNode> offsetMap;
    LinkedList<TryCatchBlockNode> currentTryCatchBlockNode = new LinkedList<>();

    public TryCatchBlockNodeStack(List<TryCatchBlockNode> tryCatchBlockNodes) {
        offsetMap = getTryCatchBlockNodeMap(tryCatchBlockNodes);
    }

    public void consumerLabelNode(LabelNode labelNode) {
        TryCatchBlockNode tryCatchBlockNode = offsetMap.get(labelNode.getLabel().getOffset());
        if (tryCatchBlockNode == null) {
            return;
        }

        final boolean isStart = tryCatchBlockNode.start.getLabel().getOffset() == labelNode.getLabel().getOffset();
        if (isStart) {
            currentTryCatchBlockNode.add(tryCatchBlockNode);
            return;
        }
        final boolean isEnd = tryCatchBlockNode.end.getLabel().getOffset() == labelNode.getLabel().getOffset();
        if (!isEnd) {
            return;
        }
        if (currentTryCatchBlockNode.isEmpty() || currentTryCatchBlockNode.getLast() != tryCatchBlockNode) {
            throw new RuntimeException("TRY CATCH BLOCK EXCEPTION!");
        }
        currentTryCatchBlockNode.removeLast();
    }

    /**
     * 获取当前字节码的try catch块中
     * @return 当前字节码所在的try catch块
     */
    public TryCatchBlockNode getCurrentTryCatchBlockNode() {
        if (currentTryCatchBlockNode.isEmpty()) {
            return null;
        }
        return currentTryCatchBlockNode.getLast();
    }

    public boolean inTryCatch() {
        return !currentTryCatchBlockNode.isEmpty();
    }

    public static Map<Integer, TryCatchBlockNode> getTryCatchBlockNodeMap(List<TryCatchBlockNode> tryCatchBlockNodes) {
        Map<Integer, TryCatchBlockNode> map = new TreeMap<>();
        if (tryCatchBlockNodes != null) {
            for (TryCatchBlockNode tryCatchBlockNode: tryCatchBlockNodes) {
                int o1 = tryCatchBlockNode.start.getLabel().getOffset();
                int o2 = tryCatchBlockNode.end.getLabel().getOffset();

                map.put(o1, tryCatchBlockNode);
                map.put(o2, tryCatchBlockNode);
            }
        }
        return map;
    }
}
