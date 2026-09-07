package com.example.login.product;

import com.example.login.ApiResponse;
import com.example.login.LoginController;
import com.example.login.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private static final int MAX_NAME_LENGTH = 100;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductAdminController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body, HttpSession session) {
        Optional<String> denied = requireAdmin(session);
        if (denied.isPresent()) {
            return ApiResponse.error(403, denied.get());
        }
        String name = normalize(body.get("name"));
        if (name == null) {
            return ApiResponse.error(400, "产品名不能为空");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return ApiResponse.error(400, "产品名过长");
        }
        if (productRepository.existsName(name, null)) {
            return ApiResponse.error(400, "产品已存在");
        }

        Long id = productRepository.insert(name);

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
        if (!productRepository.existsId(id)) {
            return ApiResponse.error(404, "记录不存在");
        }
        String name = normalize(body.get("name"));
        if (name == null) {
            return ApiResponse.error(400, "产品名不能为空");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return ApiResponse.error(400, "产品名过长");
        }
        if (productRepository.existsName(name, id)) {
            return ApiResponse.error(400, "产品已存在");
        }

        productRepository.updateName(id, name);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "保存成功");
        return ApiResponse.success(data);
    }

    private Optional<String> requireAdmin(HttpSession session) {
        Object user = session.getAttribute(LoginController.SESSION_USER);
        if (user == null) {
            return Optional.of("未登录");
        }
        Optional<String> role = userRepository.findRoleByUsername(user.toString());
        if (role.isEmpty() || !"admin".equals(role.get())) {
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
