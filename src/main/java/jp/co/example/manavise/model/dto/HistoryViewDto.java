package jp.co.example.manavise.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoryViewDto {
    private Integer executeId;
    private LocalDateTime executedAt;
    private String userName;
    private Integer questionNumber;
    private String questionContent;
    private String userAnswer;
    private String correctAnswer;
    private boolean correct;
    private String categoryName;
}
