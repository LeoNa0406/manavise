package jp.co.example.manavise.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import jp.co.example.manavise.model.entity.Choice;

import java.util.List;

@Repository
public class ChoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChoiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Choice> choiceRowMapper = (rs, rowNum) -> {
        Choice c = new Choice();
        c.setChoiceId(rs.getInt("choice_id"));
        c.setQuestionId(rs.getInt("question_id"));
        c.setChoiceText(rs.getString("choice_text"));
        c.setCorrect(rs.getBoolean("is_correct"));
        c.setChoiceOrder(rs.getInt("choice_order"));
        return c;
    };

    /**
     * 問題IDに紐づく全選択肢を表示順で取得する（出題画面用）。
     */
    public List<Choice> findByQuestionId(Integer questionId) {
        String sql = """
                SELECT * FROM choices
                WHERE question_id = ?
                ORDER BY choice_order
                """;
        return jdbcTemplate.query(sql, choiceRowMapper, questionId);
    }

    /**
     * 問題IDに紐づく正解の選択肢を1件取得する（採点用）。
     * 4択・○× は is_correct=true が必ず1件存在することを前提とする。
     */
    public Choice findCorrectByQuestionId(Integer questionId) {
        String sql = """
                SELECT * FROM choices
                WHERE question_id = ? AND is_correct = true
                LIMIT 1
                """;
        return jdbcTemplate.queryForObject(sql, choiceRowMapper, questionId);
    }

    /**
     * 選択肢を1件登録する（問題登録・更新時に使用）。
     */
    public void insert(Choice choice) {
        String sql = """
                INSERT INTO choices (question_id, choice_text, is_correct, choice_order)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                choice.getQuestionId(),
                choice.getChoiceText(),
                choice.isCorrect(),
                choice.getChoiceOrder());
    }

    /**
     * 問題IDに紐づく全選択肢を削除する（問題更新時に一括削除→再登録で使用）。
     */
    public void deleteByQuestionId(Integer questionId) {
        String sql = "DELETE FROM choices WHERE question_id = ?";
        jdbcTemplate.update(sql, questionId);
    }
}