package jp.co.example.manavise.model.entity;

import lombok.Data;

@Data
public class AnswerHistory {
    private Integer answerId;
    private Integer executeId;
    private Integer questionId;
    private String userAnswer;
    private boolean correct;
}
