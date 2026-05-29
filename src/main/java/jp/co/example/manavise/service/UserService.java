package jp.co.example.manavise.service;

import jp.co.example.manavise.model.entity.User;
import jp.co.example.manavise.repository.UserRepository;
import jp.co.example.manavise.security.CustomUserDetails;
import jp.co.example.manavise.model.form.StudentForm;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Spring Security から呼ばれる認証用メソッド。
     * loginId でユーザーを検索し、CustomUserDetails を返す。
     */
    @Override
    public CustomUserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));

        String roleName = (user.getRoleId() == 1) ? "ROLE_ADMIN" : "ROLE_USER";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));
        return new CustomUserDetails(user, authorities);
    }

    /** 全ユーザー（生徒）一覧を取得する（管理者用） */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /** ユーザーID でユーザーを取得する */
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
    }

    /** ユーザーを新規登録する */
    public void registerUser(StudentForm studentForm) {
        userRepository.insert(studentForm);
    }

}
