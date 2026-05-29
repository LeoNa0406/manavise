package jp.co.example.manavise.repository;

import jp.co.example.manavise.model.entity.Question;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * questions テーブルへのデータアクセスクラス。
 * 論理削除（deleted = true）のレコードは通常クエリから除外する。
 */
@Repository
public class QuestionRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 問題情報をマッピングする RowMapper */
    private final RowMapper<Question> questionRowMapper = (rs, i) -> {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionNumber(rs.getInt("question_number"));
        question.setQuestionContent(rs.getString("question_content"));
        question.setAnswer(rs.getString("answer"));
        question.setCategoryId(rs.getInt("category_id"));
        question.setQuestionType(rs.getInt("question_type"));
        question.setDeleted(rs.getBoolean("deleted"));
        return question;
    };

    /** 全問題を取得する */
    public List<Question> findAllQuestions() {
        String sql = "SELECT * FROM questions ORDER BY question_number";
        return jdbcTemplate.query(sql, questionRowMapper);
    }

    /** 論理削除されていない全問題を取得する */
    public List<Question> findAllActive() {
        String sql = "SELECT * FROM questions WHERE deleted = false ORDER BY question_number";
        return jdbcTemplate.query(sql, questionRowMapper);
    }

    /** カテゴリーIDで絞り込み、論理削除されていない問題を取得する */
    public List<Question> findActiveByCategoryId(Integer categoryId) {
        String sql = "SELECT * FROM questions WHERE category_id = ? AND deleted = false ORDER BY question_number";
        return jdbcTemplate.query(sql, questionRowMapper, categoryId);
    }

    /** 問題ID で問題を取得する（論理削除済みも含む：管理者用） */
    public Optional<Question> findById(Integer questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, questionRowMapper, questionId);
        return questions.isEmpty() ? Optional.empty() : Optional.of(questions.get(0));
    }

    /** 問題を新規登録する */
    public Integer insert(Question question) {
        String sql = "INSERT INTO questions (question_number, question_content, answer, category_id, question_type) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "question_id" });
            ps.setInt(1, question.getQuestionNumber());
            ps.setString(2, question.getQuestionContent());
            ps.setString(3, question.getAnswer());
            ps.setInt(4, question.getCategoryId());
            ps.setInt(5, question.getQuestionType());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** 問題を更新する */
    public void update(Question question) {
        String sql = """
                UPDATE questions
                SET question_number = ?,
                question_content = ?,
                answer = ?,
                category_id = ?,
                question_type = ?
                WHERE question_id = ?
                        """;
        jdbcTemplate.update(sql,
                question.getQuestionNumber(),
                question.getQuestionContent(),
                question.getAnswer(),
                question.getCategoryId(),
                question.getQuestionType(),
                question.getQuestionId());
    }

    /** 問題を論理削除する（deleted = true にする） */
    public void logicalDelete(Integer questionId) {
        String sql = "UPDATE questions SET deleted = true WHERE question_id = ?";
        jdbcTemplate.update(sql, questionId);
    }
}
