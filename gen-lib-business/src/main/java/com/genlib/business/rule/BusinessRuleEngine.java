package com.genlib.business.rule;

import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 业务规则引擎
 * 提供灵活的业务规则管理和执行
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class BusinessRuleEngine {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleEngine.class);

    private final String name;
    private final Map<String, BusinessRule> rules;
    private final Map<String, RuleGroup> ruleGroups;
    private final List<RuleExecutionListener> listeners;

    public BusinessRuleEngine(String name) {
        this.name = name;
        this.rules = new ConcurrentHashMap<>();
        this.ruleGroups = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * 添加规则
     */
    public BusinessRuleEngine addRule(BusinessRule rule) {
        rules.put(rule.getName(), rule);
        logger.debug("规则引擎[{}] 添加规则: {}", name, rule.getName());
        return this;
    }

    /**
     * 添加规则组
     */
    public BusinessRuleEngine addRuleGroup(RuleGroup ruleGroup) {
        ruleGroups.put(ruleGroup.getName(), ruleGroup);
        logger.debug("规则引擎[{}] 添加规则组: {}", name, ruleGroup.getName());
        return this;
    }

    /**
     * 添加规则执行监听器
     */
    public BusinessRuleEngine addListener(RuleExecutionListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * 执行单个规则
     */
    public RuleExecutionResult executeRule(String ruleName, RuleContext context) {
        BusinessRule rule = rules.get(ruleName);
        if (rule == null) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "规则不存在: " + ruleName);
        }

        return executeRule(rule, context);
    }

    /**
     * 执行规则组
     */
    public RuleGroupExecutionResult executeRuleGroup(String groupName, RuleContext context) {
        RuleGroup ruleGroup = ruleGroups.get(groupName);
        if (ruleGroup == null) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "规则组不存在: " + groupName);
        }

        return executeRuleGroup(ruleGroup, context);
    }

    /**
     * 执行所有规则
     */
    public List<RuleExecutionResult> executeAllRules(RuleContext context) {
        List<RuleExecutionResult> results = new ArrayList<>();
        
        for (BusinessRule rule : rules.values()) {
            if (rule.isEnabled()) {
                try {
                    RuleExecutionResult result = executeRule(rule, context);
                    results.add(result);
                } catch (Exception e) {
                    logger.error("规则执行失败: " + rule.getName(), e);
                    results.add(new RuleExecutionResult(rule.getName(), false, e.getMessage()));
                }
            }
        }
        
        return results;
    }

    /**
     * 验证规则
     */
    public void validateRules(RuleContext context) {
        List<RuleExecutionResult> results = executeAllRules(context);
        
        List<String> failedRules = results.stream()
                .filter(result -> !result.isSuccess())
                .map(RuleExecutionResult::getRuleName)
                .collect(Collectors.toList());
        
        if (!failedRules.isEmpty()) {
            String message = "业务规则验证失败: " + String.join(", ", failedRules);
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, message);
        }
    }

    /**
     * 执行规则（内部方法）
     */
    private RuleExecutionResult executeRule(BusinessRule rule, RuleContext context) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 触发监听器 - 规则开始执行
            listeners.forEach(listener -> listener.onRuleStart(rule, context));
            
            // 检查规则条件
            boolean conditionMet = rule.getCondition().evaluate(context);
            
            if (conditionMet) {
                // 执行规则动作
                rule.getAction().execute(context);
                
                long duration = System.currentTimeMillis() - startTime;
                logger.debug("规则[{}] 执行成功，耗时: {}ms", rule.getName(), duration);
                
                RuleExecutionResult result = new RuleExecutionResult(rule.getName(), true, null, duration);
                
                // 触发监听器 - 规则执行成功
                listeners.forEach(listener -> listener.onRuleSuccess(rule, context, result));
                
                return result;
            } else {
                long duration = System.currentTimeMillis() - startTime;
                logger.debug("规则[{}] 条件不满足，跳过执行", rule.getName());
                
                RuleExecutionResult result = new RuleExecutionResult(rule.getName(), true, "条件不满足", duration);
                
                // 触发监听器 - 规则跳过
                listeners.forEach(listener -> listener.onRuleSkipped(rule, context, result));
                
                return result;
            }
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("规则[{}] 执行失败", rule.getName(), e);
            
            RuleExecutionResult result = new RuleExecutionResult(rule.getName(), false, e.getMessage(), duration);
            
            // 触发监听器 - 规则执行失败
            listeners.forEach(listener -> listener.onRuleFailure(rule, context, result, e));
            
            return result;
        }
    }

    /**
     * 执行规则组（内部方法）
     */
    private RuleGroupExecutionResult executeRuleGroup(RuleGroup ruleGroup, RuleContext context) {
        long startTime = System.currentTimeMillis();
        List<RuleExecutionResult> results = new ArrayList<>();
        
        try {
            for (String ruleName : ruleGroup.getRuleNames()) {
                BusinessRule rule = rules.get(ruleName);
                if (rule != null && rule.isEnabled()) {
                    RuleExecutionResult result = executeRule(rule, context);
                    results.add(result);
                    
                    // 如果是失败停止模式且规则执行失败，则停止执行
                    if (ruleGroup.getExecutionMode() == RuleGroup.ExecutionMode.FAIL_FAST 
                        && !result.isSuccess()) {
                        break;
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            boolean success = results.stream().allMatch(RuleExecutionResult::isSuccess);
            
            return new RuleGroupExecutionResult(ruleGroup.getName(), success, results, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("规则组[{}] 执行失败", ruleGroup.getName(), e);
            
            return new RuleGroupExecutionResult(ruleGroup.getName(), false, results, duration);
        }
    }

    /**
     * 获取规则引擎名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取所有规则
     */
    public Collection<BusinessRule> getRules() {
        return rules.values();
    }

    /**
     * 获取规则
     */
    public BusinessRule getRule(String ruleName) {
        return rules.get(ruleName);
    }

    /**
     * 移除规则
     */
    public BusinessRuleEngine removeRule(String ruleName) {
        rules.remove(ruleName);
        logger.debug("规则引擎[{}] 移除规则: {}", name, ruleName);
        return this;
    }

    /**
     * 清空所有规则
     */
    public BusinessRuleEngine clearRules() {
        rules.clear();
        ruleGroups.clear();
        logger.debug("规则引擎[{}] 清空所有规则", name);
        return this;
    }
}