package jp.co.example.manavise.model.entity;

import lombok.Data;

@Data
public class Choice {
    private Integer choiceId;
    private Integer questionId;
    private String choiceText;
    private boolean correct;
    private Integer choiceOrder;
}
