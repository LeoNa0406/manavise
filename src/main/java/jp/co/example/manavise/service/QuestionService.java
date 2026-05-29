package jp.co.example.manavise.service;

import jp.co.example.manavise.model.form.QuestionForm;
import jp.co.example.manavise.model.form.ChoiceForm;
import jp.co.example.manavise.model.entity.Category;
import jp.co.example.manavise.model.entity.Choice;
import jp.co.example.manavise.model.entity.Question;
import jp.co.example.manavise.repository.CategoryRepository;
import jp.co.example.manavise.repository.ChoiceRepository;
import jp.co.example.manavise.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;

    public QuestionService(QuestionRepository questionRepository,
            CategoryRepository categoryRepository, ChoiceRepository choiceRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.choiceRepository = choiceRepository;
    }

    /** 全カテゴリー一覧を取得する */
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    /** カテゴリーIDで1件取得する */
    public Category findCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません"));
    }

    /** 全問題一覧を取得する */
    public List<Question> findAllQuestions() {
        return questionRepository.findAllQuestions();
    }

    /** 論理削除されていない全問題を取得する */
    public List<Question> findAllActiveQuestions() {
        return questionRepository.findAllActive();
    }

    /** カテゴリーIDで絞り込んだ問題一覧を取得する */
    public List<Question> findQuestionsByCategory(Integer categoryId) {
        return questionRepository.findActiveByCategoryId(categoryId);
    }

    /** 問題IDで1件取得する（編集フォーム表示用） */
    public Question findById(Integer questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("問題が見つかりません"));
    }

    public List<Choice> findChoicesByQuestionId(Integer questionId) {
        return choiceRepository.findByQuestionId(questionId);
    }

    /** 問題を新規登録する */
    public void registerQuestion(QuestionForm form) {
        Question question = new Question();
        question.setQuestionNumber(form.getQuestionNumber());
        question.setQuestionContent(form.getQuestionContent());
        question.setQuestionType(form.getQuestionType());

        question.setAnswer(form.getQuestionType() == 1 ? form.getAnswer()
                : form.getChoices().stream()
                        .filter(ChoiceForm::isCorrect)
                        .map(ChoiceForm::getChoiceText)
                        .findFirst().orElse(""));
        question.setCategoryId(form.getCategoryId());
        Integer questionId = questionRepository.insert(question);

        if (form.getQuestionType() != 1) {
            insertChoices(questionId, form);
        }
    }

    private void insertChoices(Integer questionId, QuestionForm form) {
        List<ChoiceForm> choiceForms = form.getChoices();
        if (choiceForms == null)
            return;

        int order = 1;

        for (ChoiceForm cf : choiceForms) {
            Choice choice = new Choice();
            choice.setQuestionId(questionId);
            choice.setChoiceText(cf.getChoiceText());
            choice.setCorrect(cf.isCorrect());
            choice.setChoiceOrder(order++);

            choiceRepository.insert(choice);
        }
    }

    /** 問題を更新する */
    public void updateQuestion(QuestionForm form) {
        Question question = new Question();
        question.setQuestionId(form.getQuestionId());
        question.setQuestionNumber(form.getQuestionNumber());
        question.setQuestionContent(form.getQuestionContent());

        question.setAnswer(form.getQuestionType() == 1 ? form.getAnswer()
                : form.getChoices().stream()
                        .filter(ChoiceForm::isCorrect)
                        .map(ChoiceForm::getChoiceText)
                        .findFirst().orElse(""));

        question.setQuestionType(form.getQuestionType());
        question.setCategoryId(form.getCategoryId());
        questionRepository.update(question);

        choiceRepository.deleteByQuestionId(form.getQuestionId());
        if (form.getQuestionType() != 1) {
            insertChoices(form.getQuestionId(), form);
        }
    }

    /** 問題を論理削除する */
    public void deleteQuestion(Integer questionId) {
        questionRepository.logicalDelete(questionId);
    }
}