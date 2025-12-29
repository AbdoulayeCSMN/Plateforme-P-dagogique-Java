package com.education.plateforme.controller;

import com.education.plateforme.dto.QuizDTO;
import com.education.plateforme.dto.QuizGenerationRequest;
import com.education.plateforme.dto.QuizSubmissionDTO;
import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.User;
import com.education.plateforme.service.AIAgentService;
import com.education.plateforme.service.CourseService;
import com.education.plateforme.service.QuizService;
import com.education.plateforme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/student/quiz")
public class QuizController {

    @Autowired
    private AIAgentService aiAgentService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    /**
     * Génère un nouveau quiz avec l'agent IA
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<?> generateQuiz(@RequestBody QuizGenerationRequest request, 
                                         Authentication authentication) {
        try {
            User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
            Course course = courseService.getCourseById(request.getCourseId()).orElseThrow();

            // Vérifier que l'étudiant est inscrit
            if (!courseService.isStudentEnrolled(course.getId(), student)) {
                return ResponseEntity.status(403).body(Map.of("error", "Vous n'êtes pas inscrit à ce cours"));
            }

            System.out.println("🚀 Génération du quiz pour le cours: " + course.getTitle());

            // Générer le quiz avec l'agent IA
            QuizDTO quizDTO = aiAgentService.generateAdaptiveQuiz(course, student);

            System.out.println("✅ Quiz généré avec succès - ID: " + quizDTO.getQuizId());

            return ResponseEntity.ok(quizDTO);
        } catch (Exception e) {
            System.err.println("❌ ERREUR génération quiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Affiche le quiz interactif
     */
    @GetMapping("/take/{quizId}")
    public String takeQuiz(@PathVariable Long quizId, Model model, Authentication authentication) {
        try {
            User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

            // Vérifier que le quiz appartient à l'étudiant
            if (!quiz.getStudent().getId().equals(student.getId())) {
                throw new RuntimeException("Accès non autorisé");
            }

            // Vérifier que le quiz n'est pas déjà complété
            if (quiz.isCompleted()) {
                return "redirect:/student/quiz/" + quizId;
            }

            System.out.println("📝 Affichage du quiz ID: " + quizId);
            System.out.println("   - Nombre de questions: " + quiz.getQuestions().size());
            System.out.println("   - Difficulté: " + quiz.getDifficulty());
            System.out.println("   - Temps limite: " + quiz.getTimeLimitMinutes() + " min");

            model.addAttribute("quiz", quiz);
            model.addAttribute("course", quiz.getCourse());
            model.addAttribute("student", student);

            return "student/quiz-take";
        } catch (Exception e) {
            System.err.println("❌ Erreur affichage quiz: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/student/courses";
        }
    }

    /**
     * Soumet les réponses du quiz
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionDTO submission, 
                                       Authentication authentication) {
        try {
            User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
            Quiz quiz = quizService.getQuizById(submission.getQuizId()).orElseThrow();

            // Vérifier que le quiz appartient à l'étudiant
            if (!quiz.getStudent().getId().equals(student.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Accès non autorisé"));
            }

            // Vérifier que le quiz n'est pas déjà complété
            if (quiz.isCompleted()) {
                return ResponseEntity.status(400).body(Map.of("error", "Ce quiz a déjà été soumis"));
            }

            // Convertir les réponses
            Map<Long, Integer> answers = new HashMap<>();
            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                Long questionId = quiz.getQuestions().get(i).getId();
                Integer answer = submission.getAnswers().get(i);
                if (answer != null) {
                    answers.put(questionId, answer);
                }
            }

            // Définir le temps passé
            quiz.setTimeSpentMinutes(submission.getTimeSpentMinutes());

            // Soumettre le quiz
            Quiz completedQuiz = quizService.submitQuiz(quiz.getId(), answers);

            // Retourner les résultats
            Map<String, Object> result = new HashMap<>();
            result.put("quizId", completedQuiz.getId());
            result.put("score", completedQuiz.getScore());
            result.put("correctAnswers", completedQuiz.getCorrectAnswers());
            result.put("totalQuestions", completedQuiz.getTotalQuestions());
            result.put("passed", completedQuiz.getScore() >= 70);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}