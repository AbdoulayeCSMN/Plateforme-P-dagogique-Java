package com.education.plateforme.service;

import com.education.plateforme.dto.QuizDTO;
import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AIAgentService {

    @Autowired
    private QuizGenerationService quizGenerationService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private RAGService ragService;

    /**
     * L'agent IA décide des paramètres du quiz et le génère
     */
    public QuizDTO generateAdaptiveQuiz(Course course, User student) {
        // 1. Vérifier que le cours est indexé
        if (!ragService.isCourseIndexed(course)) {
            ragService.indexCourse(course);
        }

        // 2. Analyser l'historique de l'étudiant
        String recommendedDifficulty = quizService.calculateRecommendedDifficulty(student, course);
        
        // 3. Déterminer le nombre de questions basé sur la difficulté
        int numberOfQuestions = determineQuestionCount(recommendedDifficulty);
        
        // 4. Déterminer la limite de temps
        int timeLimitMinutes = determineTimeLimit(recommendedDifficulty, numberOfQuestions);

        // 5. Générer le quiz avec les paramètres optimaux
        return quizGenerationService.generateQuiz(
                course, 
                student, 
                recommendedDifficulty, 
                numberOfQuestions, 
                timeLimitMinutes
        );
    }

    /**
     * Évalue si l'étudiant peut valider le cours
     */
    public boolean evaluateCourseCompletion(User student, Course course) {
        return quizService.canValidateCourse(student, course);
    }

    /**
     * Fournit des recommandations à l'étudiant
     */
    public String provideRecommendations(User student, Course course) {
        List<Quiz> quizzes = quizService.getStudentQuizzesByCourse(course, student);
        
        if (quizzes.isEmpty()) {
            return "Commencez par passer votre premier quiz pour évaluer votre niveau.";
        }

        double avgScore = quizzes.stream()
                .filter(Quiz::isCompleted)
                .mapToDouble(Quiz::getScore)
                .average()
                .orElse(0.0);

        if (avgScore >= 80) {
            return "Excellent travail ! Vous maîtrisez bien ce cours. Continuez avec des quiz difficiles.";
        } else if (avgScore >= 60) {
            return "Bon progrès ! Revoyez les concepts où vous avez des difficultés et réessayez.";
        } else {
            return "Prenez le temps de relire attentivement le cours avant de refaire un quiz.";
        }
    }

    private int determineQuestionCount(String difficulty) {
        return switch (difficulty) {
            case "EASY" -> 5;
            case "MEDIUM" -> 7;
            case "HARD" -> 10;
            default -> 5;
        };
    }

    private int determineTimeLimit(String difficulty, int numberOfQuestions) {
        int baseTimePerQuestion = switch (difficulty) {
            case "EASY" -> 2;
            case "MEDIUM" -> 3;
            case "HARD" -> 4;
            default -> 3;
        };
        return numberOfQuestions * baseTimePerQuestion;
    }
}