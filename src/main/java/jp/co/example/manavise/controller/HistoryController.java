package jp.co.example.manavise.controller;

import jp.co.example.manavise.service.HistoryService;
import jp.co.example.manavise.model.dto.HistoryViewDto;
import jp.co.example.manavise.security.CustomUserDetails;

import java.util.List;

import jp.co.example.manavise.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/history")
public class HistoryController {

    private final UserService userService;
    private final HistoryService historyService;

    public HistoryController(HistoryService historyService, UserService userService) {
        this.historyService = historyService;
        this.userService = userService;
    }

    // GET /history/my
    // ログイン中のユーザー自身の履歴一覧を表示する（一般ユーザー用）。
    @GetMapping("/my")
    public String showMyHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Integer userId = userDetails.getUser().getUserId();
        List<HistoryViewDto> historyList = historyService.getHistoryByUser(userId);
        model.addAttribute("historyList", historyList);
        return "/history/my_history"; // TODO: "history/my_history" を返す
    }

    // GET /history/admin/students/{userId}
    // 特定生徒の履歴を表示する（管理者用）。
    // SecurityConfig で ADMIN ロールのみアクセス可に制限する。
    @GetMapping("/admin/students")
    public String showStudentHistory(@RequestParam("userId") Integer userId, Model model) {
        List<HistoryViewDto> historyList = historyService.getHistoryByUser(userId);
        model.addAttribute("historyList", historyList);
        model.addAttribute("targetUser", userService.findById(userId));
        return "/admin/student_history"; // TODO: "admin/student_history" を返す
    }
}
