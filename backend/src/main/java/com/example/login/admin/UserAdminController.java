package com.example.login.admin;

import com.example.login.ApiResponse;
import com.example.login.LoginController;
import com.example.login.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    private static final Set<String> VALID_ROLES = Set.of(ROLE_ADMIN, ROLE_USER);

    private final UserRepository userRepository;

    public UserAdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Map<String, Object> list(HttpSession session) {
        Optional<String> denied = requireAdmin(session);
        if (denied.isPresent()) {
            return ApiResponse.error(403, denied.get());
        }
        List<Map<String, Object>> users = userRepository.listUsers();
        return ApiResponse.success(users);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body, HttpSession session) {
        Optional<String> denied = requireAdmin(session);
        if (denied.isPresent()) {
            return ApiResponse.error(403, denied.get());
        }

        String username = normalize(body.get("username"));
        if (username == null) {
            return ApiResponse.error(400, "用户名不能为空");
        }
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        String role = normalize(body.get("role"));
        if (role == null) {
            return ApiResponse.error(400, "角色不能为空");
        }
        if (!VALID_ROLES.contains(role)) {
            return ApiResponse.error(400, "角色取值不合法");
        }
        if (userRepository.existsUsername(username, null)) {
            return ApiResponse.error(400, "用户名已存在");
        }

        Long id = userRepository.createUser(username, password, role);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "添加成功");
        return ApiResponse.success(data);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      HttpSession session) {
        Optional<String> denied = requireAdmin(session);
        if (denied.isPresent()) {
            return ApiResponse.error(403, denied.get());
        }
        if (!userRepository.existsId(id)) {
            return ApiResponse.error(404, "记录不存在");
        }

        Map<String, Object> current = userRepository.listUsers().stream()
                .filter(u -> Long.valueOf(u.get("id").toString()).equals(id))
                .findFirst()
                .orElse(null);
        if (current == null) {
            return ApiResponse.error(404, "记录不存在");
        }

        String username = String.valueOf(current.get("username"));
        if (body.containsKey("username")) {
            String rawUsername = body.get("username");
            if (rawUsername == null || rawUsername.isBlank()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            username = rawUsername.trim();
            if (userRepository.existsUsername(username, id)) {
                return ApiResponse.error(400, "用户名已存在");
            }
        }

        String password = null;
        if (body.containsKey("password")) {
            String rawPassword = body.get("password");
            if (rawPassword == null || rawPassword.isBlank()) {
                return ApiResponse.error(400, "密码不能为空");
            }
            password = rawPassword;
        }

        String role = String.valueOf(current.get("role"));
        if (body.containsKey("role")) {
            String rawRole = body.get("role");
            if (rawRole == null || rawRole.isBlank()) {
                return ApiResponse.error(400, "角色不能为空");
            }
            role = rawRole.trim();
            if (!VALID_ROLES.contains(role)) {
                return ApiResponse.error(400, "角色取值不合法");
            }
        }

        userRepository.updateUser(id, username, password, role);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "保存成功");
        return ApiResponse.success(data);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id, HttpSession session) {
        Optional<String> denied = requireAdmin(session);
        if (denied.isPresent()) {
            return ApiResponse.error(403, denied.get());
        }
        if (!userRepository.existsId(id)) {
            return ApiResponse.error(404, "记录不存在");
        }

        Object user = session.getAttribute(LoginController.SESSION_USER);
        String targetUsername = String.valueOf(
                userRepository.listUsers().stream()
                        .filter(u -> Long.valueOf(u.get("id").toString()).equals(id))
                        .findFirst()
                        .map(u -> u.get("username"))
                        .orElse(""));
        if (user != null && targetUsername.equals(user.toString())) {
            return ApiResponse.error(400, "不能删除当前登录用户");
        }

        userRepository.deleteUser(id);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "删除成功");
        return ApiResponse.success(data);
    }

    private Optional<String> requireAdmin(HttpSession session) {
        Object user = session.getAttribute(LoginController.SESSION_USER);
        if (user == null) {
            return Optional.of("未登录");
        }
        Optional<String> role = userRepository.findRoleByUsername(user.toString());
        if (role.isEmpty() || !ROLE_ADMIN.equals(role.get())) {
            return Optional.of("无管理员权限");
        }
        return Optional.empty();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
