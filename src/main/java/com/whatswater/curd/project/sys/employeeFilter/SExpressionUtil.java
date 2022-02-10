package com.whatswater.curd.project.sys.employeeFilter;

import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.BusinessException;
import com.whatswater.curd.project.common.ErrorCodeEnum;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SExpressionUtil {
    public static final int LOGIC_TYPE_AND = 1;
    public static final String FUNCTION_NAME_AND = "and";
    public static final int LOGIC_TYPE_OR = 2;
    public static final String FUNCTION_NAME_OR = "or";

    public static SExpression parse(List<EmployeeFilterData> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            throw new BusinessException(ErrorCodeEnum.PARAM_NO_VALID.getErrCode(), "表达式data不能为空");
        }
        for (EmployeeFilterData data: dataList) {
            String[] functionParamList = data.getPath().split("\\.");
            if (functionParamList.length == 0) {
                throw new BusinessException(ErrorCodeEnum.PARAM_NO_VALID.getErrCode(), "path不能为空");
            }
            String last = functionParamList[functionParamList.length - 1];
            FunctionParamIndex lastIndex = parseIndex(last);
            if (!isNormalFunctionName(lastIndex.functionName)) {
                throw new BusinessException(ErrorCodeEnum.PARAM_NO_VALID.getErrCode(), "path最后一个节点必须是正常节点");
            }
        }
        SExpression sExpression = LogicNode.fromDataList(LOGIC_TYPE_AND, dataList, 0);
        return sExpression.flatten().merge();
    }

    public static Integer parseValueType(String path) {
        String[] operationList = path.split("\\.");
        if (operationList.length == 0) {
            return 0;
        }
        String operationIndex = operationList[operationList.length - 1];
        String functionName = parseIndex(operationIndex).functionName;
        return EmployeeFilterDataValueTypeEnum.getCodeByFunctionName(functionName);
    }


    public static FunctionParamIndex parseIndex(String functionParam) {
        Pattern pattern = Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*)\\[([1-9]+)]$");
        Matcher matcher = pattern.matcher(functionParam);

        if (!matcher.matches()) {
            throw new RuntimeException("解析functionParam失败，格式错误");
        }
        String functionName = matcher.group(1);
        String index = matcher.group(2);

        return new FunctionParamIndex(Integer.parseInt(index), functionName);
    }

    public static boolean isLogicFunctionName(String functionName) {
        return logicTypeOfFunctionName(functionName) > 0;
    }

    public static int logicTypeOfFunctionName(String functionName) {
        if (FUNCTION_NAME_AND.equals(functionName)) {
            return LOGIC_TYPE_AND;
        } else if (FUNCTION_NAME_OR.equals(functionName)) {
            return LOGIC_TYPE_OR;
        }
        return 0;
    }

    public static boolean isNormalFunctionName(String functionName) {
        return EmployeeFilterDataValueTypeEnum.getCodeByFunctionName(functionName) != null;
    }

    public interface SExpression {
        default SExpression merge() {
            return this;
        }
        default SExpression flatten() {
            return this;
        }

        void fillVariableNames(Set<String> variableNames);
    }

    public static class LogicNode implements SExpression {
        private int logicType;
        private List<SExpression> children;

        public LogicNode(int logicType) {
            this.logicType = logicType;
        }

        @Override
        public SExpression flatten() {
            if (CollectionUtil.isNotEmpty(children)) {
                if (children.size() == 1) {
                    return children.get(0);
                }
                return this;
            }
            return this;
        }

        @Override
        public void fillVariableNames(Set<String> variableNames) {
            if (CollectionUtil.isEmpty(children)) {
                return;
            }
            for (SExpression child: children) {
                child.fillVariableNames(variableNames);
            }
        }

        static LogicNode fromDataList(int logicType, List<EmployeeFilterData> dataList, int pathIndex) {
            LogicNode logicNode = new LogicNode(logicType);

            // 按照functionParam进行分组
            Map<String, List<EmployeeFilterData>> dataMap = dataList.stream().collect(Collectors.groupingBy(data -> {
                String[] functionParamList = data.getPath().split("\\.");
                return functionParamList[pathIndex];
            }));

            // 按照functionParamIndex对分组进行排序
            List<Map.Entry<String, List<EmployeeFilterData>>> entryList = dataMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> {
                    String functionParam = entry.getKey();
                    return parseIndex(functionParam).index;
                })).collect(Collectors.toList());
            // 递归调用转换为children
            List<SExpression> children = new ArrayList<>();
            logicNode.children = children;
            for (Map.Entry<String, List<EmployeeFilterData>> entry: entryList) {
                List<EmployeeFilterData> subDataList = entry.getValue();

                String nextNodeName = null;
                for (EmployeeFilterData subData: subDataList) {
                    String[] functionParamList = subData.getPath().split("\\.");
                    String functionParam = functionParamList[pathIndex + 1];
                    FunctionParamIndex functionParamIndex = parseIndex(functionParam);
                    if (nextNodeName == null) {
                        nextNodeName = functionParamIndex.functionName;
                    } else if (!nextNodeName.equals(functionParamIndex.functionName)) {
                        throw new RuntimeException("同一个路径的参数名称应该一致");
                    }
                }

                if (isNormalFunctionName(nextNodeName)) {
                    FunctionCall functionCall = new FunctionCall();
                    functionCall.setFunctionName(nextNodeName);

                    List<String> params = subDataList.stream()
                        .sorted(Comparator.comparing(data -> {
                            String[] functionParamList = data.getPath().split("\\.");
                            String functionParam = functionParamList[pathIndex + 1];
                            FunctionParamIndex functionParamIndex = parseIndex(functionParam);
                            return functionParamIndex.index;
                        }))
                        .map(EmployeeFilterData::getValue)
                        .collect(Collectors.toList());
                    functionCall.setParams(params);
                    children.add(functionCall);
                } else {
                    children.add(fromDataList(logicTypeOfFunctionName(nextNodeName), subDataList, pathIndex + 1));
                }
            }
            return logicNode;
        }

        public int getLogicType() {
            return logicType;
        }

        public void setLogicType(int logicType) {
            this.logicType = logicType;
        }

        public List<SExpression> getChildren() {
            return children;
        }

        public void setChildren(List<SExpression> children) {
            this.children = children;
        }
    }

    public static class FunctionCall implements SExpression {
        String functionName;
        List<String> params;

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }


        static Pattern pattern = Pattern.compile("^\\$\\{(.+)}$");
        @Override
        public void fillVariableNames(Set<String> variableNames) {
            if (CollectionUtil.isEmpty(variableNames)) {
                return;
            }
            for (String param: params) {
                Matcher matcher = pattern.matcher(param);
                if (!matcher.matches()) {
                    continue;
                }
                variableNames.add(matcher.group(1));
            }
        }
    }

    public static class FunctionParamIndex {
        int index;
        String functionName;

        public FunctionParamIndex(int index, String functionName) {
            this.index = index;
            this.functionName = functionName;
        }
    }
}
