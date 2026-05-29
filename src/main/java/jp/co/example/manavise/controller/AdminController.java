package jp.co.example.manavise.controller;

import jp.co.example.manavise.model.entity.Question;
import jp.co.example.manavise.model.entity.User;
import jp.co.example.manavise.model.form.ChoiceForm;
import jp.co.example.manavise.model.form.QuestionForm;
import jp.co.example.manavise.model.form.StudentForm;
import jp.co.example.manavise.service.QuestionService;
import jp.co.example.manavise.service.UserService;
import jp.co.example.manavise.model.entity.Category;
import jp.co.example.manavise.model.entity.Choice;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QuestionService questionService;
    private final UserService userService;

    public AdminController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    // GET /admin/questions
    // 管理者用：全問題一覧（編集・削除リンク付き）を表示する。
    @GetMapping("/questions")
    public String showQuestionList(@RequestParam(required = false) Integer categoryId, Model model) {
        List<Question> questions;
        if (categoryId != null) {
            questions = questionService.findQuestionsByCategory(categoryId);
        } else {
            questions = questionService.findAllQuestions();
        }
        List<Category> categories = questionService.findAllCategories();
        model.addAttribute("questions", questions);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        return "admin/question_list";
    }

    // GET /admin/questions/new
    // 問題新規登録フォームを表示する。
    @GetMapping("/questions/new")
    public String showRegisterForm(Model model) {
        model.addAttribute("categories", questionService.findAllCategories());
        model.addAttribute("questionForm", new QuestionForm());
        return "admin/register";
    }

    /**
     * POST /admin/questions/new
     * 問題を新規登録する。バリデーションエラー時はフォームに戻る。
     */
    @PostMapping("/questions/new")
    public String register(@Valid @ModelAttribute("questionForm") QuestionForm form,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", questionService.findAllCategories());
            return "admin/register";
        }
        questionService.registerQuestion(form);
        return "redirect:/admin/questions";
    }

    // GET /admin/questions/{id}/edit
    // 問題編集フォームを表示する。既存データをフォームに詰めて渡す。
    @GetMapping("/questions/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("categories", questionService.findAllCategories());
        Question question = questionService.findById(id);
        List<Choice> choices = questionService.findChoicesByQuestionId(id);
        model.addAttribute("choices", choices);
        QuestionForm form = new QuestionForm();
        form.setQuestionId(question.getQuestionId());
        form.setQuestionNumber(question.getQuestionNumber());
        form.setQuestionContent(question.getQuestionContent());
        form.setAnswer(question.getAnswer());
        form.setCategoryId(question.getCategoryId());
        form.setQuestionType(question.getQuestionType());
        for (Choice choice : choices) {
            ChoiceForm choiceForm = new ChoiceForm();
            choiceForm.setChoiceId(choice.getChoiceId());
            choiceForm.setChoiceText(choice.getChoiceText());
            choiceForm.setCorrect(choice.isCorrect());
            choiceForm.setChoiceOrder(choice.getChoiceOrder());
            form.getChoices().add(choiceForm);
        }
        model.addAttribute("questionForm", form);
        return "admin/edit";

    }

    // POST /admin/questions/{id}/edit
    // 問題を更新する。バリデーションエラー時はフォームに戻る。
    @PostMapping("/questions/{id}/edit")
    public String update(@PathVariable Integer id,
            @Valid @ModelAttribute QuestionForm form,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "admin/edit";
        }
        questionService.updateQuestion(form);
        return "redirect:/admin/questions"; // TODO: 成功時は /admin/questions へリダイレクト
    }

    // POST /admin/questions/{id}/delete
    // 問題を論理削除する。
    @PostMapping("/questions/{id}/delete")
    public String delete(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return "redirect:/admin/questions"; // TODO: /admin/questions へリダイレクト
    }

    // GET /admin/students
    // 生徒（一般ユーザー）一覧を表示する。
    @GetMapping("/students")
    public String showStudentList(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/student_list"; // TODO: "admin/student_list" を返す
    }

    // GET /admin/students/new
    // 生徒（一般ユーザー）新規登録フォームを表示する。
    @GetMapping("/students/new")
    public String showStudentRegisterForm(Model model) {
        StudentForm studentForm = new StudentForm();
        studentForm.setRoleId(2); // 一般ユーザーのロールID
        model.addAttribute("studentForm", studentForm);
        return "admin/student_register";
    }

    // POST /admin/students
    // 生徒（一般ユーザー）を新規登録する。
    @PostMapping("/students/new")
    public String registerStudent(@Valid @ModelAttribute StudentForm form,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "admin/student_register";
        }
        userService.registerUser(form);
        return "redirect:/admin/students";
    }
}
