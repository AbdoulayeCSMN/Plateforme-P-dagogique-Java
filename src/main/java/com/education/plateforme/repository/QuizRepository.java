package com.education.plateforme.repository;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByStudentOrderByAttemptDateDesc(User student);
    List<Quiz> findByCourseAndStudentOrderByAttemptDateDesc(Course course, User student);
    List<Quiz> findByStudentAndCompletedTrueOrderByAttemptDateDesc(User student);
}