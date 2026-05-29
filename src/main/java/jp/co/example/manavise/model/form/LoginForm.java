package jp.co.example.manavise.model.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {

    @NotBlank(message = "ログインIDは必須です")
    private String loginId;

    @NotBlank(message = "パスワードは必須です")
    private String loginPassword;
}
