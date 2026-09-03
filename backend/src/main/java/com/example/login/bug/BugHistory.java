package com.example.login.bug;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class BugHistory {

    private Long id;
    private Long bugId;
    private String operator;
    private LocalDateTime operatedAt;
    private List<Change> changes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBugId() {
        return bugId;
    }

    public void setBugId(Long bugId) {
        this.bugId = bugId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(LocalDateTime operatedAt) {
        this.operatedAt = operatedAt;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public static class Change {
        private String field;
        private Object oldValue;
        private Object newValue;

        public Change() {
        }

        public Change(String field, Object oldValue, Object newValue) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        @JsonProperty("old")
        public Object getOldValue() {
            return oldValue;
        }

        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        @JsonProperty("new")
        public Object getNewValue() {
            return newValue;
        }

        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }
    }
}
