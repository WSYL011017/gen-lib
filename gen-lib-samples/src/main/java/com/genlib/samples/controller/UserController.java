package com.genlib.samples.controller;

import com.genlib.core.model.Result;
import com.genlib.core.model.PageResult;
import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.core.exception.BusinessException;
import com.genlib.samples.entity.User;
import com.genlib.samples.service.UserService;
import com.genlib.utils.text.StringUtils;
import com.genlib.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户控制器示例
 * 演示如何使用Gen-Lib的统一响应格式、异常处理等功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户列表
     */
    @GetMapping
    public Result<List<User>> getUsers() {
        List<User> users = userService.findAll();
        return Result.success("查询成功", users);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<PageResult<User>> getUsersPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        
        PageResult<User> result = userService.findPage(pageNum, pageSize, keyword);
        return Result.success("分页查询成功", result);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        // 参数验证
        BusinessException.assertNotNull(id, "用户ID不能为空");
        BusinessException.assertTrue(id > 0, "用户ID必须大于0");
        
        User user = userService.findById(id);
        if (user == null) {
            return Result.error(ResultCodeEnum.DATA_NOT_FOUND, user,"用户不存在");
        }
        
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        // 参数验证
        validateUser(user);
        
        // 检查用户名是否已存在
        if (userService.existsByUsername(user.getUsername())) {
            return Result.error(ResultCodeEnum.DATA_ALREADY_EXISTS,user, "用户名已存在");
        }
        
        // 设置默认值
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);
        
        // 设置审计信息（创建）
        user.onCreate(1L, "admin");
        
        User created = userService.create(user);
        return Result.success("用户创建成功", created);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // 参数验证
        BusinessException.assertNotNull(id, "用户ID不能为空");
        validateUser(user);
        
        // 检查用户是否存在
        User existing = userService.findById(id);
        BusinessException.assertNotNull(existing, "用户不存在");
        
        // 更新字段
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setMobile(user.getMobile());
        existing.setRealName(user.getRealName());
        existing.setAge(user.getAge());
        
        // 设置审计信息（更新）
        existing.onUpdate(1L, "admin");
        
        User updated = userService.update(existing);
        return Result.success("用户更新成功", updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // 参数验证
        BusinessException.assertNotNull(id, "用户ID不能为空");
        
        // 检查用户是否存在
        User user = userService.findById(id);
        BusinessException.assertNotNull(user, "用户不存在");
        
        userService.deleteById(id);
        return Result.success("用户删除成功");
    }

    /**
     * 充值
     */
    @PostMapping("/{id}/recharge")
    public Result<User> recharge(@PathVariable Long id, @RequestParam BigDecimal amount) {
        // 参数验证
        BusinessException.assertNotNull(id, "用户ID不能为空");
        BusinessException.assertNotNull(amount, "充值金额不能为空");
        BusinessException.assertTrue(amount.compareTo(BigDecimal.ZERO) > 0, "充值金额必须大于0");
        BusinessException.assertTrue(amount.compareTo(new BigDecimal("10000")) <= 0, "单次充值金额不能超过10000");
        
        User user = userService.recharge(id, amount);
        return Result.success("充值成功", user);
    }

    /**
     * 工具类功能演示
     */
    @GetMapping("/demo/utils")
    public Result<Object> utilsDemo() {
        // 演示工具类使用
        String result = StringUtils.toCamelCase("user_name", "_"); // "userName"
        String masked = StringUtils.maskMobile("13812345678"); // "138****5678" 
        String formatted = DateUtils.formatDateTime(LocalDateTime.now());
        
        return Result.success("工具类演示", new Object() {
            public final String camelCase = result;
            public final String maskedMobile = masked;
            public final String currentTime = formatted;
        });
    }

    /**
     * 异常处理演示
     */
    @GetMapping("/demo/exception")
    public Result<Void> exceptionDemo(@RequestParam(required = false) String type) {
        if ("business".equals(type)) {
            // 业务异常
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, "这是一个业务异常示例");
        } else if ("param".equals(type)) {
            // 参数异常
            BusinessException.assertNotNull(null, "参数不能为空");
        } else if ("runtime".equals(type)) {
            // 运行时异常
            throw new RuntimeException("这是一个运行时异常示例");
        }
        
        return Result.success("正常处理");
    }

    /**
     * 验证用户参数
     */
    private void validateUser(User user) {
        BusinessException.assertNotNull(user, "用户信息不能为空");
        BusinessException.assertNotEmpty(user.getUsername(), "用户名不能为空");
        BusinessException.assertNotEmpty(user.getEmail(), "邮箱不能为空");
        
        // 用户名长度验证
        BusinessException.assertTrue(
            user.getUsername().length() >= 3 && user.getUsername().length() <= 20,
            "用户名长度必须在3-20个字符之间"
        );
        
        // 邮箱格式验证（简单验证）
        BusinessException.assertTrue(
            user.getEmail().contains("@") && user.getEmail().contains("."),
            "邮箱格式不正确"
        );
        
        // 年龄验证
        if (user.getAge() != null) {
            BusinessException.assertTrue(
                user.getAge() >= 0 && user.getAge() <= 150,
                "年龄必须在0-150之间"
            );
        }
    }
}