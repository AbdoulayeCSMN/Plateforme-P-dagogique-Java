package com.education.plateforme.controller;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.User;
import com.education.plateforme.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private RAGService ragService;

    @Autowired
    private AIAgentService aiAgentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User student = userService.getUserByUsername(username).orElse(null);
        
        List<Course> enrolledCourses = courseService.getCoursesByStudent(student);
        List<Quiz> recentQuizzes = quizService.getStudentQuizHistory(student).stream()
                .limit(5)
                .toList();
        
        model.addAttribute("student", student);
        model.addAttribute("courses", enrolledCourses);
        model.addAttribute("recentQuizzes", recentQuizzes);
        model.addAttribute("totalCourses", enrolledCourses.size());
        
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String listCourses(Model model, Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
        List<Course> courses = courseService.getCoursesByStudent(student);
        
        model.addAttribute("courses", courses);
        return "student/courses";
    }

    @GetMapping("/courses/{id}")
    public String viewCourse(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
            Course course = courseService.getCourseWithHtmlContent(id);
            
            // Vérifier que l'étudiant est inscrit
            if (!courseService.isStudentEnrolled(id, student)) {
                throw new RuntimeException("Vous n'êtes pas inscrit à ce cours");
            }
            
            // Récupérer les informations basiques
            List<Quiz> courseQuizzes = quizService.getStudentQuizzesByCourse(course, student);
            String recommendedDifficulty = quizService.calculateRecommendedDifficulty(student, course);
            boolean canValidate = quizService.canValidateCourse(student, course);
            boolean isIndexed = ragService.isCourseIndexed(course);
            long chunkCount = ragService.getChunkCount(course);
            String recommendations = aiAgentService.provideRecommendations(student, course);
            
            // Calculer les statistiques
            int quizCount = courseQuizzes.size();
            Double avgScore = null;
            if (!courseQuizzes.isEmpty()) {
                avgScore = courseQuizzes.stream()
                        .filter(Quiz::isCompleted)
                        .mapToDouble(Quiz::getScore)
                        .average()
                        .orElse(0.0);
                avgScore = Math.round(avgScore * 10.0) / 10.0; // Arrondir à 1 décimale
            }
            
            // Ajouter au modèle
            model.addAttribute("course", course);
            model.addAttribute("quizCount", quizCount);
            model.addAttribute("avgScore", avgScore);
            model.addAttribute("recommendedDifficulty", recommendedDifficulty);
            model.addAttribute("canValidate", canValidate);
            model.addAttribute("isIndexed", isIndexed);
            model.addAttribute("chunkCount", chunkCount);
            model.addAttribute("recommendations", recommendations);
            
            return "student/course-view";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    @GetMapping("/quiz/history")
    public String quizHistory(Model model, Authentication authentication) {

        User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
        List<Quiz> quizzes = quizService.getStudentQuizHistory(student);

        // Moyenne
        double averageScore = quizzes.stream()
                .filter(Quiz::isCompleted)
                .mapToDouble(Quiz::getScore)
                .average()
                .orElse(0.0);

        // Taux de réussite (score >= 70)
        double successRate = quizzes.isEmpty() ? 0.0 :
                quizzes.stream()
                        .filter(q -> q.isCompleted() && q.getScore() >= 70)
                        .count() * 100.0 / quizzes.size();

        // Meilleur score
        double bestScore = quizzes.stream()
                .filter(Quiz::isCompleted)
                .mapToDouble(Quiz::getScore)
                .max()
                .orElse(0.0);

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("successRate", successRate);
        model.addAttribute("bestScore", bestScore);

        return "student/quiz-history";
    }


    @GetMapping("/quiz/{id}")
    public String viewQuizResult(@PathVariable Long id, 
                                Model model, 
                                Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName()).orElseThrow();
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        
        // Vérifier que le quiz appartient à l'étudiant
        if (!quiz.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Accès non autorisé");
        }
        
        model.addAttribute("quiz", quiz);
        return "student/quiz-result";
    }
    
}