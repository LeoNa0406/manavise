package jp.co.example.manavise.model.form;

import lombok.Data;

@Data
public class ChoiceForm {
    private Integer choiceId;
    private Integer questionId;
    private String choiceText;
    private boolean correct;
    private Integer choiceOrder;
}
