package com.education.plateforme.dto;

import java.util.List;

public class QuizDTO {
    
    private Long quizId;
    private String difficulty;
    private int timeLimitMinutes;
    private List<QuizQuestionDTO> questions;

    // Constructeur par défaut
    public QuizDTO() {
    }

    // Constructeur avec paramètres
    public QuizDTO(Long quizId, String difficulty, int timeLimitMinutes, List<QuizQuestionDTO> questions) {
        this.quizId = quizId;
        this.difficulty = difficulty;
        this.timeLimitMinutes = timeLimitMinutes;
        this.questions = questions;
    }

    // Getters et Setters
    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuizDTO{" +
                "quizId=" + quizId +
                ", difficulty='" + difficulty + '\'' +
                ", timeLimitMinutes=" + timeLimitMinutes +
                ", questions=" + questions +
                '}';
    }
}