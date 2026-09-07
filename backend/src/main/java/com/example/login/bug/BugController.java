package com.example.login.bug;

import com.example.login.ApiResponse;
import com.example.login.LoginController;
import com.example.login.product.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    private final BugService bugService;
    private final ProductRepository productRepository;

    public BugController(BugService bugService, ProductRepository productRepository) {
        this.bugService = bugService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String severity,
                                    @RequestParam(required = false) Long productId,
                                    @RequestParam(required = false) String assignee,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        BugQuery query = new BugQuery();
        query.setKeyword(keyword);
        query.setType(type);
        query.setStatus(status);
        query.setSeverity(severity);
        query.setProductId(productId);
        query.setAssignee(assignee);
        query.setPage(Math.max(page, 1));
        query.setSize(Math.min(Math.max(size, 1), 100));
        return ApiResponse.success(bugService.search(query));
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Optional<Bug> bug = bugService.getById(id);
        if (bug.isEmpty()) {
            return ApiResponse.error(404, "记录不存在");
        }
        return ApiResponse.success(bug.get());
    }

    @GetMapping("/{id}/history")
    public Map<String, Object> history(@PathVariable Long id) {
        Optional<Bug> bug = bugService.getById(id);
        if (bug.isEmpty()) {
            return ApiResponse.error(404, "记录不存在");
        }
        List<BugHistory> history = bugService.history(id);
        return ApiResponse.success(history);
    }

    @PostMapping("/{id}/progress")
    public Map<String, Object> progress(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpSession session) {
        String content = body == null ? null : body.get("content");
        if (content == null || content.isBlank()) {
            return ApiResponse.error(400, "进展内容不能为空");
        }
        String operator = (String) session.getAttribute(LoginController.SESSION_USER);
        boolean added = bugService.addProgress(id, content.trim(), operator);
        if (!added) {
            return ApiResponse.error(404, "记录不存在");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "进展更新成功");
        return ApiResponse.success(data);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body, HttpSession session) {
        String title = body.getOrDefault("title", "");
        if (title.trim().isEmpty()) {
            return ApiResponse.error(400, "标题不能为空");
        }
        String type = body.get("type");
        if (type != null && !type.isBlank() && !BugEnums.isValidType(type.trim())) {
            return ApiResponse.error(400, "Bug 类型取值不合法");
        }
        String severity = body.get("severity");
        if (severity != null && !severity.isBlank() && !BugEnums.isValidSeverity(severity.trim())) {
            return ApiResponse.error(400, "严重程度取值不合法");
        }
        String creator = (String) session.getAttribute(LoginController.SESSION_USER);
        String assignee = body.get("assignee");
        Long productId = parseProductId(body.get("productId"));
        if (productId == null) {
            return ApiResponse.error(400, "请选择产品");
        }
        if (!productRepository.existsId(productId)) {
            return ApiResponse.error(400, "产品不存在");
        }
        Bug bug = bugService.create(title.trim(),
                normalizeNullable(body.get("description")),
                type == null || type.isBlank() ? null : type.trim(),
                null,
                severity == null || severity.isBlank() ? null : severity.trim(),
                productId,
                creator,
                normalizeNullable(assignee));
        return ApiResponse.success(bug);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      HttpSession session) {
        Optional<Bug> existingOpt = bugService.getById(id);
        if (existingOpt.isEmpty()) {
            return ApiResponse.error(404, "记录不存在");
        }
        Bug current = existingOpt.get();

        // 部分更新语义：只更新请求体中出现的字段，未提供的字段保留原值。
        // 这样流转状态/关闭 Bug 等操作（仅携带 status）不会误清空详细描述、指派处理人等属性。
        String title = current.getTitle();
        if (body.containsKey("title")) {
            title = body.get("title");
            if (title == null || title.isBlank()) {
                return ApiResponse.error(400, "标题不能为空");
            }
            title = title.trim();
        }

        String type = current.getType();
        if (body.containsKey("type")) {
            String rawType = body.get("type");
            if (rawType != null && !rawType.isBlank()) {
                type = rawType.trim();
                if (!BugEnums.isValidType(type)) {
                    return ApiResponse.error(400, "Bug 类型取值不合法");
                }
            }
        }

        String status = current.getStatus();
        if (body.containsKey("status")) {
            String rawStatus = body.get("status");
            if (rawStatus != null && !rawStatus.isBlank()) {
                status = rawStatus.trim();
                if (!BugEnums.isValidStatus(status)) {
                    return ApiResponse.error(400, "状态取值不合法");
                }
            }
        }

        String severity = current.getSeverity();
        if (body.containsKey("severity")) {
            String rawSeverity = body.get("severity");
            if (rawSeverity != null && !rawSeverity.isBlank()) {
                severity = rawSeverity.trim();
                if (!BugEnums.isValidSeverity(severity)) {
                    return ApiResponse.error(400, "严重程度取值不合法");
                }
            }
        }

        String description = current.getDescription();
        if (body.containsKey("description")) {
            description = normalizeNullable(body.get("description"));
        }

        String assignee = current.getAssignee();
        if (body.containsKey("assignee")) {
            assignee = normalizeNullable(body.get("assignee"));
        }

        Long productId = current.getProductId();
        if (body.containsKey("productId")) {
            Long parsed = parseProductId(body.get("productId"));
            if (parsed == null) {
                return ApiResponse.error(400, "请选择产品");
            }
            if (!productRepository.existsId(parsed)) {
                return ApiResponse.error(400, "产品不存在");
            }
            productId = parsed;
        }

        String operator = (String) session.getAttribute(LoginController.SESSION_USER);
        boolean updated = bugService.update(id, title, description, type, status, severity,
                productId, operator, assignee);

        if (!updated) {
            return ApiResponse.error(404, "记录不存在");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("message", "保存成功");
        return ApiResponse.success(data);
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Long parseProductId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
