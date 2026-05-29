package jp.co.example.manavise.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerForm {

    @NotNull
    private Integer questionId;

    @NotBlank(message = "回答を入力してください")
    private String userAnswer;

    @NotNull
    private Integer categoryId;

}
