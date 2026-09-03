package com.example.login.bug;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BugRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public BugRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<Bug> findById(Long id) {
        List<Bug> rows = jdbcTemplate.query(
                "SELECT * FROM bug WHERE id = ?", (rs, rowNum) -> {
                    Bug b = new Bug();
                    b.setId(rs.getLong("id"));
                    b.setTitle(rs.getString("title"));
                    b.setDescription(rs.getString("description"));
                    b.setStatus(rs.getString("status"));
                    b.setSeverity(rs.getString("severity"));
                    b.setCreator(rs.getString("creator"));
                    b.setAssignee(rs.getString("assignee"));
                    b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    b.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return b;
                }, id);
        return rows.stream().findFirst();
    }

    public long count(BugQuery query) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM bug WHERE 1=1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, query);
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return count == null ? 0 : count;
    }

    public List<Bug> findPage(BugQuery query) {
        StringBuilder sql = new StringBuilder("SELECT * FROM bug WHERE 1=1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, query);
        sql.append(" ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?");
        args.add(query.getSize());
        args.add((long) (query.getPage() - 1) * query.getSize());

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Bug b = new Bug();
            b.setId(rs.getLong("id"));
            b.setTitle(rs.getString("title"));
            b.setDescription(rs.getString("description"));
            b.setStatus(rs.getString("status"));
            b.setSeverity(rs.getString("severity"));
            b.setCreator(rs.getString("creator"));
            b.setAssignee(rs.getString("assignee"));
            b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            b.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return b;
        }, args.toArray());
    }

    private void appendFilters(StringBuilder sql, List<Object> args, BugQuery query) {
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
            String like = "%" + query.getKeyword().trim() + "%";
            args.add(like);
            args.add(like);
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            sql.append(" AND status = ?");
            args.add(query.getStatus().trim());
        }
        if (query.getSeverity() != null && !query.getSeverity().isBlank()) {
            sql.append(" AND severity = ?");
            args.add(query.getSeverity().trim());
        }
        if (query.getAssignee() != null && !query.getAssignee().isBlank()) {
            sql.append(" AND assignee = ?");
            args.add(query.getAssignee().trim());
        }
    }

    public Long insert(Bug bug) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO bug (title, description, status, severity, creator, assignee, created_at, updated_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bug.getTitle());
            ps.setString(2, bug.getDescription());
            ps.setString(3, bug.getStatus());
            ps.setString(4, bug.getSeverity());
            ps.setString(5, bug.getCreator());
            ps.setString(6, bug.getAssignee());
            ps.setTimestamp(7, Timestamp.valueOf(bug.getCreatedAt()));
            ps.setTimestamp(8, Timestamp.valueOf(bug.getUpdatedAt()));
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void update(Bug bug) {
        jdbcTemplate.update(
                "UPDATE bug SET title = ?, description = ?, status = ?, severity = ?, assignee = ?, updated_at = ? WHERE id = ?",
                bug.getTitle(), bug.getDescription(), bug.getStatus(), bug.getSeverity(),
                bug.getAssignee(), Timestamp.valueOf(bug.getUpdatedAt()), bug.getId());
    }

    public void insertHistory(BugHistory history) {
        try {
            String changesJson = objectMapper.writeValueAsString(history.getChanges());
            jdbcTemplate.update(
                    "INSERT INTO bug_history (bug_id, operator, operated_at, changes) VALUES (?, ?, ?, ?)",
                    history.getBugId(), history.getOperator(),
                    Timestamp.valueOf(history.getOperatedAt()), changesJson);
        } catch (Exception e) {
            throw new IllegalStateException("序列化变更记录失败", e);
        }
    }

    public List<BugHistory> findHistory(Long bugId) {
        return jdbcTemplate.query(
                "SELECT * FROM bug_history WHERE bug_id = ? ORDER BY operated_at ASC, id ASC",
                (rs, rowNum) -> {
                    BugHistory h = new BugHistory();
                    h.setId(rs.getLong("id"));
                    h.setBugId(rs.getLong("bug_id"));
                    h.setOperator(rs.getString("operator"));
                    h.setOperatedAt(rs.getTimestamp("operated_at").toLocalDateTime());
                    try {
                        h.setChanges(objectMapper.readValue(
                                rs.getString("changes"), new TypeReference<List<BugHistory.Change>>() {
                                }));
                    } catch (Exception e) {
                        throw new IllegalStateException("解析变更记录失败", e);
                    }
                    return h;
                }, bugId);
    }
}
