package com.education.plateforme.dto;

import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmissionDTO {
    private Long quizId;
    private Map<Integer, Integer> answers; // questionIndex -> selectedOptionIndex
    private int timeSpentMinutes;
	public Long getQuizId() {
		return quizId;
	}
	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}
	public Map<Integer, Integer> getAnswers() {
		return answers;
	}
	public void setAnswers(Map<Integer, Integer> answers) {
		this.answers = answers;
	}
	public int getTimeSpentMinutes() {
		return timeSpentMinutes;
	}
	public void setTimeSpentMinutes(int timeSpentMinutes) {
		this.timeSpentMinutes = timeSpentMinutes;
	}
}