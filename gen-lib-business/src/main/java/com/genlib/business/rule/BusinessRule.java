package com.genlib.business.rule;

/**
 * 业务规则接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class BusinessRule {

    private final String name;
    private final String description;
    private final RuleCondition condition;
    private final RuleAction action;
    private final int priority;
    private boolean enabled;

    public BusinessRule(String name, String description, RuleCondition condition, RuleAction action) {
        this(name, description, condition, action, 0, true);
    }

    public BusinessRule(String name, String description, RuleCondition condition, 
                       RuleAction action, int priority, boolean enabled) {
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.action = action;
        this.priority = priority;
        this.enabled = enabled;
    }

    // getters and setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RuleCondition getCondition() { return condition; }
    public RuleAction getAction() { return action; }
    public int getPriority() { return priority; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "BusinessRule{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", enabled=" + enabled +
                '}';
    }
}

/**
 * 规则条件接口
 */
@FunctionalInterface
interface RuleCondition {
    boolean evaluate(RuleContext context);
}

/**
 * 规则动作接口
 */
@FunctionalInterface
interface RuleAction {
    void execute(RuleContext context) throws Exception;
}