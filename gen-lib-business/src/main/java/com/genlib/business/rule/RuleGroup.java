package com.genlib.business.rule;

import java.util.List;

/**
 * 规则组
 * 用于组织和管理相关的业务规则
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class RuleGroup {

    private final String name;
    private final String description;
    private final List<String> ruleNames;
    private final ExecutionMode executionMode;
    private final int priority;

    public RuleGroup(String name, String description, List<String> ruleNames) {
        this(name, description, ruleNames, ExecutionMode.CONTINUE_ON_FAILURE, 0);
    }

    public RuleGroup(String name, String description, List<String> ruleNames, 
                    ExecutionMode executionMode, int priority) {
        this.name = name;
        this.description = description;
        this.ruleNames = ruleNames;
        this.executionMode = executionMode;
        this.priority = priority;
    }

    // getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getRuleNames() { return ruleNames; }
    public ExecutionMode getExecutionMode() { return executionMode; }
    public int getPriority() { return priority; }

    /**
     * 执行模式枚举
     */
    public enum ExecutionMode {
        /** 失败时继续执行 */
        CONTINUE_ON_FAILURE,
        /** 失败时快速停止 */
        FAIL_FAST
    }

    @Override
    public String toString() {
        return "RuleGroup{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ruleNames=" + ruleNames +
                ", executionMode=" + executionMode +
                ", priority=" + priority +
                '}';
    }
}

/**
 * 规则执行结果
 */
class RuleExecutionResult {
    private final String ruleName;
    private final boolean success;
    private final String message;
    private final long executionTime;

    public RuleExecutionResult(String ruleName, boolean success, String message) {
        this(ruleName, success, message, 0);
    }

    public RuleExecutionResult(String ruleName, boolean success, String message, long executionTime) {
        this.ruleName = ruleName;
        this.success = success;
        this.message = message;
        this.executionTime = executionTime;
    }

    // getters
    public String getRuleName() { return ruleName; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public long getExecutionTime() { return executionTime; }

    @Override
    public String toString() {
        return "RuleExecutionResult{" +
                "ruleName='" + ruleName + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", executionTime=" + executionTime +
                '}';
    }
}

/**
 * 规则组执行结果
 */
class RuleGroupExecutionResult {
    private final String groupName;
    private final boolean success;
    private final List<RuleExecutionResult> ruleResults;
    private final long executionTime;

    public RuleGroupExecutionResult(String groupName, boolean success, 
                                   List<RuleExecutionResult> ruleResults, long executionTime) {
        this.groupName = groupName;
        this.success = success;
        this.ruleResults = ruleResults;
        this.executionTime = executionTime;
    }

    // getters
    public String getGroupName() { return groupName; }
    public boolean isSuccess() { return success; }
    public List<RuleExecutionResult> getRuleResults() { return ruleResults; }
    public long getExecutionTime() { return executionTime; }

    @Override
    public String toString() {
        return "RuleGroupExecutionResult{" +
                "groupName='" + groupName + '\'' +
                ", success=" + success +
                ", ruleResults=" + ruleResults +
                ", executionTime=" + executionTime +
                '}';
    }
}

/**
 * 规则执行监听器
 */
interface RuleExecutionListener {
    default void onRuleStart(BusinessRule rule, RuleContext context) {}
    default void onRuleSuccess(BusinessRule rule, RuleContext context, RuleExecutionResult result) {}
    default void onRuleFailure(BusinessRule rule, RuleContext context, RuleExecutionResult result, Exception exception) {}
    default void onRuleSkipped(BusinessRule rule, RuleContext context, RuleExecutionResult result) {}
}