package com.example.login.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, created_at FROM product ORDER BY id ASC",
                (rs, rowNum) -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("name", rs.getString("name"));
                    row.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime());
                    return row;
                });
    }

    public Optional<String> findName(Long id) {
        List<String> names = jdbcTemplate.query(
                "SELECT name FROM product WHERE id = ?", (rs, rowNum) -> rs.getString("name"), id);
        return names.stream().findFirst();
    }

    public boolean existsId(Long id) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product WHERE id = ?", Long.class, id);
        return count != null && count > 0;
    }

    public boolean existsName(String name, Long excludeId) {
        Long count;
        if (excludeId == null) {
            count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM product WHERE name = ?", Long.class, name);
        } else {
            count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM product WHERE name = ? AND id <> ?", Long.class, name, excludeId);
        }
        return count != null && count > 0;
    }

    public Long insert(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO product (name, created_at) VALUES (?, NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updateName(Long id, String name) {
        jdbcTemplate.update("UPDATE product SET name = ? WHERE id = ?", name, id);
    }
}
