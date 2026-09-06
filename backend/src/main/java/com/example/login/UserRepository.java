package com.example.login;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean authenticate(String username, String password) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE username = ? AND password = ?",
                Integer.class, username, password);
        return count != null && count > 0;
    }

    public Optional<String> findRoleByUsername(String username) {
        List<String> roles = jdbcTemplate.query(
                "SELECT role FROM user WHERE username = ?",
                (rs, rowNum) -> rs.getString("role"), username);
        return roles.stream().findFirst();
    }

    public boolean existsUsername(String username, Long exceptId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM user WHERE username = ?");
        Object[] args = exceptId == null
                ? new Object[]{username}
                : new Object[]{username, exceptId};
        if (exceptId != null) {
            sql.append(" AND id <> ?");
        }
        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, args);
        return count != null && count > 0;
    }

    public boolean existsId(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public List<Map<String, Object>> listUsers() {
        return jdbcTemplate.query(
                "SELECT id, username, role FROM user ORDER BY id ASC",
                (rs, rowNum) -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("username", rs.getString("username"));
                    row.put("role", rs.getString("role"));
                    return row;
                });
    }

    public Long createUser(String username, String password, String role) {
        jdbcTemplate.update(
                "INSERT INTO user (username, password, role) VALUES (?, ?, ?)",
                username, password, role);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM user WHERE username = ?", Long.class, username);
    }

    public void updateUser(Long id, String username, String password, String role) {
        if (password == null) {
            jdbcTemplate.update(
                    "UPDATE user SET username = ?, role = ? WHERE id = ?", username, role, id);
        } else {
            jdbcTemplate.update(
                    "UPDATE user SET username = ?, password = ?, role = ? WHERE id = ?",
                    username, password, role, id);
        }
    }

    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM user WHERE id = ?", id);
    }
}
