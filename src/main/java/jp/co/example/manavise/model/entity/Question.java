package jp.co.example.manavise.model.entity;

import lombok.Data;

@Data
public class Question {
    private Integer questionId;
    private Integer questionNumber;
    private String questionContent;
    private String answer;
    private Integer questionType;
    private Integer categoryId;
    private boolean deleted;
    public Object getQuestionType;
}
