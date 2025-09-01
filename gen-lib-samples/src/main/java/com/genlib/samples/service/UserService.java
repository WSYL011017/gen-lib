package com.genlib.samples.service;

import com.genlib.core.model.PageResult;
import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.samples.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 用户服务示例
 * 演示如何使用Gen-Lib的异常处理、分页等功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@Service
public class UserService {

    // 模拟数据存储（实际项目中应该使用数据库）
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // 初始化一些测试数据
        initTestData();
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return users.get(id);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> username.equals(user.getUsername()));
    }

    /**
     * 创建用户
     */
    public User create(User user) {
        // 生成ID
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        
        // 保存用户
        users.put(id, user);
        
        return user;
    }

    /**
     * 更新用户
     */
    public User update(User user) {
        BusinessException.assertNotNull(user.getId(), "用户ID不能为空");
        
        User existing = users.get(user.getId());
        BusinessException.assertNotNull(existing, "用户不存在");
        
        // 更新用户信息
        users.put(user.getId(), user);
        
        return user;
    }

    /**
     * 删除用户
     */
    public void deleteById(Long id) {
        User user = users.remove(id);
        BusinessException.assertNotNull(user, "用户不存在");
    }

    /**
     * 分页查询用户
     */
    public PageResult<User> findPage(int pageNum, int pageSize, String keyword) {
        List<User> allUsers = new ArrayList<>(users.values());
        
        // 关键字搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            allUsers = allUsers.stream()
                    .filter(user -> 
                        (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerKeyword)) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) ||
                        (user.getRealName() != null && user.getRealName().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }
        
        // 计算分页
        long total = allUsers.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allUsers.size());
        
        List<User> pageData;
        if (startIndex >= allUsers.size()) {
            pageData = new ArrayList<>();
        } else {
            pageData = allUsers.subList(startIndex, endIndex);
        }
        
        return PageResult.of(pageNum, pageSize, total, pageData);
    }

    /**
     * 用户充值
     */
    public User recharge(Long id, BigDecimal amount) {
        User user = users.get(id);
        BusinessException.assertNotNull(user, "用户不存在");
        BusinessException.assertTrue(user.isActive(), "用户状态异常，无法充值");
        
        // 充值
        user.addBalance(amount);
        
        // 设置审计信息
        user.onUpdate(1L, "system");
        
        return user;
    }

    /**
     * 用户消费
     */
    public User consume(Long id, BigDecimal amount) {
        User user = users.get(id);
        BusinessException.assertNotNull(user, "用户不存在");
        BusinessException.assertTrue(user.isActive(), "用户状态异常，无法消费");
        
        // 检查余额
        boolean success = user.deductBalance(amount);
        if (!success) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "余额不足");
        }
        
        // 设置审计信息
        user.onUpdate(1L, "system");
        
        return user;
    }

    /**
     * 初始化测试数据
     */
    private void initTestData() {
        // 用户1
        User user1 = new User("admin", "admin@example.com");
        user1.setId(idGenerator.getAndIncrement());
        user1.setRealName("管理员");
        user1.setMobile("13800138000");
        user1.setAge(30);
        user1.setBalance(new BigDecimal("1000.00"));
        user1.onCreate(1L, "system");
        users.put(user1.getId(), user1);

        // 用户2
        User user2 = new User("zhangsan", "zhangsan@example.com");
        user2.setId(idGenerator.getAndIncrement());
        user2.setRealName("张三");
        user2.setMobile("13812345678");
        user2.setAge(25);
        user2.setBalance(new BigDecimal("500.00"));
        user2.onCreate(1L, "system");
        users.put(user2.getId(), user2);

        // 用户3
        User user3 = new User("lisi", "lisi@example.com");
        user3.setId(idGenerator.getAndIncrement());
        user3.setRealName("李四");
        user3.setMobile("13987654321");
        user3.setAge(28);
        user3.setBalance(new BigDecimal("200.00"));
        user3.onCreate(1L, "system");
        users.put(user3.getId(), user3);

        // 用户4
        User user4 = new User("wangwu", "wangwu@example.com");
        user4.setId(idGenerator.getAndIncrement());
        user4.setRealName("王五");
        user4.setMobile("13666666666");
        user4.setAge(32);
        user4.setBalance(new BigDecimal("0.00"));
        user4.onCreate(1L, "system");
        users.put(user4.getId(), user4);
    }
}