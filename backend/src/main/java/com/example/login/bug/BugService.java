package com.example.login.bug;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class BugService {

    private static final String DEFAULT_STATUS = "打开";
    private static final String DEFAULT_SEVERITY = "一般";

    private final BugRepository bugRepository;

    public BugService(BugRepository bugRepository) {
        this.bugRepository = bugRepository;
    }

    public Map<String, Object> search(BugQuery query) {
        long total = bugRepository.count(query);
        List<Bug> list = bugRepository.findPage(query);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("list", list);
        return data;
    }

    public Optional<Bug> getById(Long id) {
        return bugRepository.findById(id);
    }

    @Transactional
    public Bug create(String title, String description, String status, String severity,
                      String creator, String assignee) {
        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setDescription(description);
        bug.setStatus(status == null || status.isBlank() ? DEFAULT_STATUS : status);
        bug.setSeverity(severity == null || severity.isBlank() ? DEFAULT_SEVERITY : severity);
        bug.setCreator(creator);
        bug.setAssignee(assignee);
        LocalDateTime now = LocalDateTime.now();
        bug.setCreatedAt(now);
        bug.setUpdatedAt(now);

        Long id = bugRepository.insert(bug);
        bug.setId(id);

        List<BugHistory.Change> changes = new ArrayList<>();
        changes.add(new BugHistory.Change("title", null, bug.getTitle()));
        changes.add(new BugHistory.Change("status", null, bug.getStatus()));
        changes.add(new BugHistory.Change("severity", null, bug.getSeverity()));
        changes.add(new BugHistory.Change("assignee", null, bug.getAssignee()));
        writeHistory(id, creator, now, changes);
        return bug;
    }

    @Transactional
    public boolean update(Long id, String title, String description, String status, String severity,
                          String operator, String assignee) {
        Optional<Bug> existingOpt = bugRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return false;
        }
        Bug existing = existingOpt.get();

        List<BugHistory.Change> changes = new ArrayList<>();
        collectChange(changes, "title", existing.getTitle(), title);
        collectChange(changes, "description", existing.getDescription(), description);
        collectChange(changes, "status", existing.getStatus(), status);
        collectChange(changes, "severity", existing.getSeverity(), severity);
        collectChange(changes, "assignee", existing.getAssignee(), assignee);

        if (changes.isEmpty()) {
            return true;
        }

        existing.setTitle(title);
        existing.setDescription(description);
        existing.setStatus(status);
        existing.setSeverity(severity);
        existing.setAssignee(assignee);
        existing.setUpdatedAt(LocalDateTime.now());
        bugRepository.update(existing);

        writeHistory(id, operator, existing.getUpdatedAt(), changes);
        return true;
    }

    public List<BugHistory> history(Long id) {
        return bugRepository.findHistory(id);
    }

    private void collectChange(List<BugHistory.Change> changes, String field, String oldValue, String newValue) {
        String normalizedOld = normalize(oldValue);
        String normalizedNew = normalize(newValue);
        if (!Objects.equals(normalizedOld, normalizedNew)) {
            changes.add(new BugHistory.Change(field,
                    normalizedOld == null ? null : oldValue,
                    normalizedNew == null ? null : newValue));
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void writeHistory(Long bugId, String operator, LocalDateTime time, List<BugHistory.Change> changes) {
        BugHistory history = new BugHistory();
        history.setBugId(bugId);
        history.setOperator(operator);
        history.setOperatedAt(time);
        history.setChanges(changes);
        bugRepository.insertHistory(history);
    }
}
