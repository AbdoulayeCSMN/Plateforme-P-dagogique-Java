package com.education.plateforme.dto;

import lombok.Data;

@Data
public class QuizGenerationRequest {
    private Long courseId;
    private String difficulty; // EASY, MEDIUM, HARD
    private int numberOfQuestions = 10;
    private int timeLimitMinutes = 15;
	public Long getCourseId() {
		return courseId;
	}
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}
	public void setNumberOfQuestions(int numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}
	public int getTimeLimitMinutes() {
		return timeLimitMinutes;
	}
	public void setTimeLimitMinutes(int timeLimitMinutes) {
		this.timeLimitMinutes = timeLimitMinutes;
	}
}