package com.genlib.business.statemachine;

import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态机组件
 * 提供状态转换的管理和验证
 * 
 * @param <S> 状态类型
 * @param <E> 事件类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class StateMachine<S, E> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    private final String name;
    private final Map<S, StateNode<S, E>> stateNodes;
    private final Map<String, StateTransition<S, E>> transitions;
    private S initialState;

    public StateMachine(String name) {
        this.name = name;
        this.stateNodes = new ConcurrentHashMap<>();
        this.transitions = new ConcurrentHashMap<>();
    }

    /**
     * 设置初始状态
     */
    public StateMachine<S, E> setInitialState(S state) {
        this.initialState = state;
        return this;
    }

    /**
     * 添加状态
     */
    public StateMachine<S, E> addState(S state) {
        stateNodes.put(state, new StateNode<>(state));
        return this;
    }

    /**
     * 添加状态转换
     */
    public StateMachine<S, E> addTransition(S from, E event, S to) {
        return addTransition(from, event, to, null, null);
    }

    /**
     * 添加状态转换（带条件）
     */
    public StateMachine<S, E> addTransition(S from, E event, S to, 
                                          TransitionCondition<S, E> condition) {
        return addTransition(from, event, to, condition, null);
    }

    /**
     * 添加状态转换（带条件和动作）
     */
    public StateMachine<S, E> addTransition(S from, E event, S to, 
                                          TransitionCondition<S, E> condition,
                                          TransitionAction<S, E> action) {
        String transitionKey = buildTransitionKey(from, event);
        StateTransition<S, E> transition = new StateTransition<>(from, event, to, condition, action);
        transitions.put(transitionKey, transition);
        
        // 添加到源状态节点
        StateNode<S, E> fromNode = stateNodes.get(from);
        if (fromNode == null) {
            fromNode = new StateNode<>(from);
            stateNodes.put(from, fromNode);
        }
        fromNode.addTransition(event, transition);
        
        // 确保目标状态存在
        if (!stateNodes.containsKey(to)) {
            stateNodes.put(to, new StateNode<>(to));
        }
        
        return this;
    }

    /**
     * 执行状态转换
     */
    public StateTransitionResult<S> fireEvent(S currentState, E event, Object context) {
        logger.debug("状态机[{}] 尝试状态转换: {} --[{}]--> ?", name, currentState, event);
        
        // 验证当前状态
        if (!stateNodes.containsKey(currentState)) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, 
                "无效的当前状态: " + currentState);
        }
        
        // 查找转换
        String transitionKey = buildTransitionKey(currentState, event);
        StateTransition<S, E> transition = transitions.get(transitionKey);
        
        if (transition == null) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, 
                String.format("状态[%s]不能处理事件[%s]", currentState, event));
        }
        
        // 检查转换条件
        if (transition.getCondition() != null) {
            boolean conditionMet = transition.getCondition().evaluate(currentState, event, context);
            if (!conditionMet) {
                throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, 
                    "状态转换条件不满足");
            }
        }
        
        S targetState = transition.getTo();
        logger.info("状态机[{}] 状态转换: {} --[{}]--> {}", name, currentState, event, targetState);
        
        // 执行转换动作
        if (transition.getAction() != null) {
            try {
                transition.getAction().execute(currentState, event, targetState, context);
            } catch (Exception e) {
                logger.error("状态转换动作执行失败", e);
                throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, 
                    "状态转换动作执行失败: " + e.getMessage());
            }
        }
        
        return new StateTransitionResult<>(currentState, targetState, event, true);
    }

    /**
     * 检查是否可以执行状态转换
     */
    public boolean canFireEvent(S currentState, E event, Object context) {
        try {
            String transitionKey = buildTransitionKey(currentState, event);
            StateTransition<S, E> transition = transitions.get(transitionKey);
            
            if (transition == null) {
                return false;
            }
            
            if (transition.getCondition() != null) {
                return transition.getCondition().evaluate(currentState, event, context);
            }
            
            return true;
        } catch (Exception e) {
            logger.warn("检查状态转换失败", e);
            return false;
        }
    }

    /**
     * 获取当前状态可以处理的事件
     */
    public Set<E> getAvailableEvents(S currentState) {
        StateNode<S, E> stateNode = stateNodes.get(currentState);
        return stateNode != null ? stateNode.getAvailableEvents() : Set.of();
    }

    /**
     * 获取初始状态
     */
    public S getInitialState() {
        return initialState;
    }

    /**
     * 获取状态机名称
     */
    public String getName() {
        return name;
    }

    /**
     * 构建转换键
     */
    private String buildTransitionKey(S from, E event) {
        return from + "_" + event;
    }

    /**
     * 状态节点
     */
    private static class StateNode<S, E> {
        private final S state;
        private final Map<E, StateTransition<S, E>> transitions;

        public StateNode(S state) {
            this.state = state;
            this.transitions = new HashMap<>();
        }

        public void addTransition(E event, StateTransition<S, E> transition) {
            transitions.put(event, transition);
        }

        public Set<E> getAvailableEvents() {
            return transitions.keySet();
        }
    }

    /**
     * 状态转换
     */
    public static class StateTransition<S, E> {
        private final S from;
        private final E event;
        private final S to;
        private final TransitionCondition<S, E> condition;
        private final TransitionAction<S, E> action;

        public StateTransition(S from, E event, S to, 
                             TransitionCondition<S, E> condition,
                             TransitionAction<S, E> action) {
            this.from = from;
            this.event = event;
            this.to = to;
            this.condition = condition;
            this.action = action;
        }

        // getters
        public S getFrom() { return from; }
        public E getEvent() { return event; }
        public S getTo() { return to; }
        public TransitionCondition<S, E> getCondition() { return condition; }
        public TransitionAction<S, E> getAction() { return action; }
    }

    /**
     * 状态转换结果
     */
    public static class StateTransitionResult<S> {
        private final S fromState;
        private final S toState;
        private final Object event;
        private final boolean success;

        public StateTransitionResult(S fromState, S toState, Object event, boolean success) {
            this.fromState = fromState;
            this.toState = toState;
            this.event = event;
            this.success = success;
        }

        // getters
        public S getFromState() { return fromState; }
        public S getToState() { return toState; }
        public Object getEvent() { return event; }
        public boolean isSuccess() { return success; }
    }

    /**
     * 转换条件接口
     */
    @FunctionalInterface
    public interface TransitionCondition<S, E> {
        boolean evaluate(S currentState, E event, Object context);
    }

    /**
     * 转换动作接口
     */
    @FunctionalInterface
    public interface TransitionAction<S, E> {
        void execute(S fromState, E event, S toState, Object context) throws Exception;
    }
}