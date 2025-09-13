package com.finq.dtos.requests;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public class AnswerQuestionsRequest {

    @NotNull(message = "Answers are required")
    @Size(min = 5, max = 5, message = "Exactly 5 answers are required")
    private List<QuestionAnswerDto> answers;

    // Constructors
    public AnswerQuestionsRequest() {
    }

    public AnswerQuestionsRequest(List<QuestionAnswerDto> answers) {
        this.answers = answers;
    }

    // Getters and Setters
    public List<QuestionAnswerDto> getAnswers() {
        return answers;
    }

    public static class QuestionAnswerDto {
        @NotNull(message = "Question ID is required")
        private UUID questionId;

        @NotBlank(message = "Answer is required")
        @Size(min = 1, max = 100, message = "Answer must be between 1 and 100 characters")
        private String answer;
        public QuestionAnswerDto() {}

        public QuestionAnswerDto(UUID questionId, String answer) {
            this.questionId = questionId;
            this.answer = answer;
        }

        // Getters and Setters
        public UUID getQuestionId() {
            return questionId;
        }
        public void setQuestionId(UUID questionId) {
            this.questionId = questionId;
        }

        public String getAnswer() {
            return answer;
        }
        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}