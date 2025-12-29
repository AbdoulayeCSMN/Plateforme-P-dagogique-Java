package com.education.plateforme.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class QuizQuestionDTO {
    
    @JsonProperty("question")
    private String question;
    
    @JsonProperty("options")
    private List<String> options;
    
    @JsonProperty("correctOptionIndex")
    private int correctOptionIndex;
    
    @JsonProperty("explanation")
    private String explanation;

    // Constructeur par défaut OBLIGATOIRE pour Jackson
    public QuizQuestionDTO() {
    }

    // Constructeur avec paramètres
    public QuizQuestionDTO(String question, List<String> options, int correctOptionIndex, String explanation) {
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
    }

    // Getters et Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "QuizQuestionDTO{" +
                "question='" + question + '\'' +
                ", options=" + options +
                ", correctOptionIndex=" + correctOptionIndex +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}