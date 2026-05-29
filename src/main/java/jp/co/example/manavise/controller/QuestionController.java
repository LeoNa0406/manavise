package jp.co.example.manavise.controller;

import jp.co.example.manavise.model.entity.Category;
import jp.co.example.manavise.model.entity.Choice;
import jp.co.example.manavise.model.entity.Question;
import jp.co.example.manavise.model.form.AnswerForm;
import jp.co.example.manavise.service.HistoryService;
import jp.co.example.manavise.service.QuestionService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jp.co.example.manavise.security.CustomUserDetails;

@Controller
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final HistoryService historyService;

    public QuestionController(QuestionService questionService, HistoryService historyService) {
        this.questionService = questionService;
        this.historyService = historyService;
    }

    // GET /question/categories
    // カテゴリー一覧を表示する。
    @GetMapping("/categories")
    public String showCategories(Model model) {
        model.addAttribute("categories", questionService.findAllCategories());
        return "question/categories";
    }

    // GET /question/list?categoryId={id}
    // カテゴリーで絞り込んだ問題一覧を表示する。
    @GetMapping("/list")
    public String showQuestionList(@RequestParam(required = false) Integer categoryId, Model model) {
        model.addAttribute("category", questionService.findCategoryById(categoryId));
        model.addAttribute("questions", questionService.findQuestionsByCategory(categoryId));
        return "question/list";
    }

    // POST /question/start
    // 問題実施を開始する。quiz_histories に記録し、1問目を表示する。
    @GetMapping("/start")
    public String startQuiz(
            @RequestParam Integer questionId,
            @RequestParam Integer categoryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Question question = questionService.findById(questionId);
        Category category = questionService.findCategoryById(categoryId);
        List<Choice> choices = questionService.findChoicesByQuestionId(questionId);
        model.addAttribute("question", question);
        model.addAttribute("category", category);
        model.addAttribute("choices", choices);
        AnswerForm answerForm = new AnswerForm();
        answerForm.setQuestionId(questionId);
        answerForm.setCategoryId(categoryId);
        model.addAttribute("answerForm", answerForm);
        return "question/execute";
    }

    // POST /question/answer
    // 回答を受け取り、採点して結果画面へ遷移する。
    @PostMapping("/answer")
    public String submitAnswer(@Valid @ModelAttribute AnswerForm form,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (result.hasErrors()) {
            System.out.println("送信された問題ID: " + form.getQuestionId());
            Question question = questionService.findById(form.getQuestionId());
            Category category = questionService.findCategoryById(form.getCategoryId());
            List<Choice> choices = questionService.findChoicesByQuestionId(form.getQuestionId());
            model.addAttribute("question", question);
            model.addAttribute("category", category);
            model.addAttribute("choices", choices);
            return "question/execute";
        }

        Integer userId = userDetails.getUser().getUserId();
        boolean correct = historyService.recordAnswer(userId, form);

        Question question = questionService.findById(form.getQuestionId());
        Category category = questionService.findCategoryById(form.getCategoryId());

        model.addAttribute("question", question);
        model.addAttribute("userAnswer", form.getUserAnswer());
        model.addAttribute("correct", correct);
        model.addAttribute("category", category);

        return "question/result";
    }
}
