package jp.co.example.manavise.repository;

import jp.co.example.manavise.model.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** カテゴリー情報をマッピングする RowMapper */
    private final RowMapper<Category> categoryRowMapper = (rs, i) -> {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        return category;
    };

    /** 全カテゴリーを取得する */
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories ORDER BY category_id";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    /** カテゴリーID でカテゴリーを取得する */
    public Optional<Category> findById(Integer categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, categoryId);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }
}
