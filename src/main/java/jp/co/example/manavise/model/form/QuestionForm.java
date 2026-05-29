package jp.co.example.manavise.model.form;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionForm {

    private Integer questionId;

    @NotNull(message = "問題番号を入力してください")
    private Integer questionNumber;

    @NotBlank(message = "問題内容を入力してください")
    private String questionContent;

    @NotNull(message = "答えを入力してください")
    private String answer;

    @NotNull(message = "カテゴリーを選択してください")
    private Integer categoryId;

    @NotNull
    private Integer questionType = 1;

    private List<ChoiceForm> choices = new ArrayList<>();

    private boolean deleted;
}
