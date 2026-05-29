package jp.co.example.manavise.model.entity;

import lombok.Data;

@Data
public class User {
    private Integer userId;
    private String userName;
    private String loginId;
    private String loginPassword;
    private Integer roleId;
}
