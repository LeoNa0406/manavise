package jp.co.example.manavise.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizHistory {
    private Integer executeId;
    private Integer executeUserId;
    private LocalDateTime executedAt;
}
