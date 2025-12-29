package com.education.plateforme.service;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.QuizQuestion;
import com.education.plateforme.model.User;
import com.education.plateforme.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseService courseService;

    /**
     * Crée un nouveau quiz pour un étudiant (utilisé par l'IA)
     */
    public Quiz createQuiz(Course course, User student, String difficulty, int timeLimitMinutes) {
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setStudent(student);
        quiz.setDifficulty(difficulty);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        quiz.setCompleted(false);
        
        return quizRepository.save(quiz);
    }

    /**
     * Soumet les réponses d'un quiz
     */
    public Quiz submitQuiz(Long quizId, Map<Long, Integer> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        
        if (quiz.isCompleted()) {
            throw new RuntimeException("Ce quiz a déjà été soumis");
        }

        // Enregistrer les réponses de l'étudiant
        int correctCount = 0;
        for (QuizQuestion question : quiz.getQuestions()) {
            Integer studentAnswer = answers.get(question.getId());
            question.setStudentAnswerIndex(studentAnswer);
            
            if (question.isCorrect()) {
                correctCount++;
            }
        }

        // Calculer le score
        quiz.setCorrectAnswers(correctCount);
        quiz.setTotalQuestions(quiz.getQuestions().size());
        quiz.setScore((double) correctCount / quiz.getTotalQuestions() * 100);
        quiz.setCompleted(true);
        quiz.setSubmittedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    /**
     * Récupère l'historique des quiz d'un étudiant
     */
    public List<Quiz> getStudentQuizHistory(User student) {
        return quizRepository.findByStudentAndCompletedTrueOrderByAttemptDateDesc(student);
    }

    /**
     * Récupère les quiz d'un étudiant pour un cours spécifique
     */
    public List<Quiz> getStudentQuizzesByCourse(Course course, User student) {
        return quizRepository.findByCourseAndStudentOrderByAttemptDateDesc(course, student);
    }

    /**
     * Récupère un quiz par son ID
     */
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    /**
     * Calcule la difficulté recommandée pour le prochain quiz (pour l'IA)
     */
    public String calculateRecommendedDifficulty(User student, Course course) {
        List<Quiz> previousQuizzes = getStudentQuizzesByCourse(course, student);
        
        if (previousQuizzes.isEmpty()) {
            return "EASY";
        }

        // Calculer la moyenne des scores des 3 derniers quiz
        double avgScore = previousQuizzes.stream()
                .limit(3)
                .filter(Quiz::isCompleted)
                .mapToDouble(Quiz::getScore)
                .average()
                .orElse(0.0);

        if (avgScore >= 80) {
            return "HARD";
        } else if (avgScore >= 60) {
            return "MEDIUM";
        } else {
            return "EASY";
        }
    }

    /**
     * Vérifie si un étudiant peut valider le cours (pour l'IA agentique)
     */
    public boolean canValidateCourse(User student, Course course) {
        List<Quiz> completedQuizzes = getStudentQuizzesByCourse(course, student).stream()
                .filter(Quiz::isCompleted)
                .toList();

        if (completedQuizzes.size() < 3) {
            return false; // Au moins 3 quiz complétés requis
        }

        // Les 3 derniers quiz doivent avoir un score >= 70%
        return completedQuizzes.stream()
                .limit(3)
                .allMatch(quiz -> quiz.getScore() >= 70.0);
    }

	public CourseService getCourseService() {
		return courseService;
	}

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
}