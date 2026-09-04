package com.example.login.bug;

import com.example.login.ApiResponse;
import com.example.login.LoginController;
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

    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String severity,
                                    @RequestParam(required = false) String assignee,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        BugQuery query = new BugQuery();
        query.setKeyword(keyword);
        query.setStatus(status);
        query.setSeverity(severity);
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
        Bug bug = bugService.create(title.trim(),
                normalizeNullable(body.get("description")),
                type == null || type.isBlank() ? null : type.trim(),
                null,
                severity == null || severity.isBlank() ? null : severity.trim(),
                creator,
                normalizeNullable(assignee));
        return ApiResponse.success(bug);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      HttpSession session) {
        String title = body.getOrDefault("title", "");
        if (title.trim().isEmpty()) {
            return ApiResponse.error(400, "标题不能为空");
        }
        String type = body.get("type");
        if (type != null && !type.isBlank() && !BugEnums.isValidType(type.trim())) {
            return ApiResponse.error(400, "Bug 类型取值不合法");
        }
        String status = body.get("status");
        if (status != null && !status.isBlank() && !BugEnums.isValidStatus(status.trim())) {
            return ApiResponse.error(400, "状态取值不合法");
        }
        String severity = body.get("severity");
        if (severity != null && !severity.isBlank() && !BugEnums.isValidSeverity(severity.trim())) {
            return ApiResponse.error(400, "严重程度取值不合法");
        }

        String operator = (String) session.getAttribute(LoginController.SESSION_USER);
        boolean updated = bugService.update(id,
                title.trim(),
                normalizeNullable(body.get("description")),
                type == null || type.isBlank() ? null : type.trim(),
                status == null || status.isBlank() ? null : status.trim(),
                severity == null || severity.isBlank() ? null : severity.trim(),
                operator,
                normalizeNullable(body.get("assignee")));

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
}
