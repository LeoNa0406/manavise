package jp.co.example.manavise.service;

import jp.co.example.manavise.model.form.AnswerForm;
import jp.co.example.manavise.model.dto.HistoryViewDto;
import jp.co.example.manavise.model.entity.QuizHistory;
import jp.co.example.manavise.repository.ChoiceRepository;
import jp.co.example.manavise.repository.HistoryRepository;
import jp.co.example.manavise.repository.QuestionRepository;
import jp.co.example.manavise.model.entity.AnswerHistory;
import jp.co.example.manavise.model.entity.Question;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    public HistoryService(HistoryRepository historyRepository, QuestionRepository questionRepository,
            ChoiceRepository choiceRepository) {
        this.historyRepository = historyRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
    }

    // 回答を採点して answer_histories に保存する。
    // 正解かどうかを判定し、正解フラグをセットして登録する。
    @Transactional
    public boolean recordAnswer(Integer userId, AnswerForm form) {
        QuizHistory quizHistory = new QuizHistory();
        quizHistory.setExecuteUserId(userId);
        Integer executeId = historyRepository.insertQuizHistory(quizHistory);

        Question question = questionRepository.findById(form.getQuestionId())
                .orElseThrow(() -> new RuntimeException("問題が見つかりません"));

        String correctAnswer = switch (question.getQuestionType()) {
            case 1 -> question.getAnswer();
            case 2, 3 -> choiceRepository.findCorrectByQuestionId(form.getQuestionId()).getChoiceText();
            default -> throw new RuntimeException("未対応の問題形式です");
        };

        boolean correct = form.getUserAnswer().equals(correctAnswer);

        AnswerHistory answerHistory = new AnswerHistory();
        answerHistory.setExecuteId(executeId);
        answerHistory.setQuestionId(form.getQuestionId());
        answerHistory.setUserAnswer(form.getUserAnswer());
        answerHistory.setCorrect(correct);
        historyRepository.insertAnswerHistory(answerHistory);

        return correct;
    }

    // 自分の履歴一覧を取得する（一般ユーザー用）。
    public List<HistoryViewDto> getHistoryByUser(Integer userId) {
        return historyRepository.findHistoryByUserId(userId);
    }

    // 全ユーザーの履歴一覧を取得する（管理者用）。
    public List<HistoryViewDto> getAllHistory() {
        return List.of(); // TODO
    }

}
