package jp.co.example.manavise.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class StudentForm {

    private Integer userId;

    @NotBlank(message = "ユーザー名は必須です")
    @Size(max = 20, message = "ユーザー名は20文字以内で入力してください")
    private String userName;

    @NotBlank(message = "ログインIDは必須です")
    @Size(max = 20, message = "ログインIDは20文字以内で入力してください")
    private String loginId;

    @NotBlank(message = "パスワードは必須です")
    @Size(max = 20, message = "パスワードは20文字以内で入力してください")
    private String loginPassword;

    @NotNull(message = "ロールIDは必須です")
    private Integer roleId;

}
