package jp.co.example.manavise.repository;

import jp.co.example.manavise.model.form.StudentForm;
import jp.co.example.manavise.model.entity.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * users テーブルへのデータアクセスクラス。
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** ユーザー情報をマッピングする RowMapper */
    private final RowMapper<User> userRowMapper = (rs, i) -> {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("user_name"));
        user.setLoginId(rs.getString("login_id"));
        user.setLoginPassword(rs.getString("login_password"));
        user.setRoleId(rs.getInt("role_id"));
        return user;
    };

    /** ログインID でユーザーを検索する（認証用） */
    public Optional<User> findByLoginId(String loginId) {
        String sql = "SELECT * FROM users WHERE login_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, loginId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /** ユーザーID でユーザーを取得する */
    public Optional<User> findById(Integer userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /** 全ユーザーを取得する（管理者：生徒一覧用） */
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    // ユーザーを新規登録する
    public void insert(StudentForm studentForm) {
        String sql = "INSERT INTO users (user_name, login_id, login_password, role_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                studentForm.getUserName(),
                studentForm.getLoginId(),
                studentForm.getLoginPassword(),
                studentForm.getRoleId());
    }

}
