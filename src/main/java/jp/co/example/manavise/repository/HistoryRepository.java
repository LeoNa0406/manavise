package jp.co.example.manavise.repository;

import jp.co.example.manavise.model.entity.AnswerHistory;
import jp.co.example.manavise.model.entity.QuizHistory;
import jp.co.example.manavise.model.dto.HistoryViewDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class HistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public HistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 履歴表示用の RowMapper */
    private final RowMapper<HistoryViewDto> historyViewDtoRowMapper = (rs, rowNum) -> {
        HistoryViewDto dto = new HistoryViewDto();
        dto.setExecuteId(rs.getInt("execute_id"));
        dto.setExecutedAt(rs.getTimestamp("executed_at").toLocalDateTime());
        dto.setUserName(rs.getString("user_name"));
        dto.setQuestionNumber(rs.getInt("question_number"));
        dto.setQuestionContent(rs.getString("question_content"));
        dto.setUserAnswer(rs.getString("user_answer"));
        dto.setCorrectAnswer(rs.getString("correct_answer"));
        dto.setCorrect(rs.getBoolean("correct"));
        dto.setCategoryName(rs.getString("category_name"));
        return dto;
    };

    /** 問題実施履歴を登録し、採番された executeId を返す */
    public Integer insertQuizHistory(QuizHistory quizHistory) {
        String sql = "INSERT INTO quiz_histories (execute_user) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "execute_id" });
            ps.setInt(1, quizHistory.getExecuteUserId());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** 回答履歴を1件登録する */
    public void insertAnswerHistory(AnswerHistory answerHistory) {
        String sql = "INSERT INTO answer_histories (execute_id, question_id, user_answer, correct) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                answerHistory.getExecuteId(),
                answerHistory.getQuestionId(),
                answerHistory.getUserAnswer(),
                answerHistory.isCorrect());
    }

    /**
     * ユーザーID で自分の履歴一覧を取得する（一般ユーザー用）。
     * quiz_histories・answer_histories・questions・categories を結合した表示用DTOで返す。
     */
    public List<HistoryViewDto> findHistoryByUserId(Integer userId) {
        String sql = """
                SELECT
                    qh.execute_id,
                    qh.executed_at,
                    u.user_name,
                    q.question_number,
                    q.question_content,
                    ah.user_answer,
                    q.answer AS correct_answer,
                    ah.correct,
                    c.category_name
                FROM quiz_histories qh
                JOIN answer_histories ah ON qh.execute_id = ah.execute_id
                JOIN questions q ON ah.question_id = q.question_id
                JOIN categories c ON q.category_id = c.category_id
                JOIN users u ON qh.execute_user = u.user_id
                WHERE qh.execute_user = ?
                ORDER BY qh.executed_at DESC, qh.execute_id DESC
                """;

        return jdbcTemplate.query(sql, historyViewDtoRowMapper, userId);

    }
}
