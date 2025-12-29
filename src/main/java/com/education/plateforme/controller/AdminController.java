package com.education.plateforme.controller;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.User;
import com.education.plateforme.service.CourseService;
import com.education.plateforme.service.RAGService;
import com.education.plateforme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private RAGService ragService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User admin = userService.getUserByUsername(username).orElse(null);
        
        List<Course> courses = courseService.getAllCourses();
        List<User> students = userService.getStudents();
        
        long publishedCount = courses.stream().filter(Course::isPublished).count();
        long draftCount = courses.size() - publishedCount;

        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("draftCount", draftCount);        
        model.addAttribute("admin", admin);
        model.addAttribute("courses", courses);
        model.addAttribute("students", students);
        model.addAttribute("totalCourses", courses.size());
        model.addAttribute("totalStudents", students.size());
        
        return "admin/dashboard";
    }

    // ========== GESTION DES COURS ==========

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses";
    }

    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "admin/course-form";
    }

    @PostMapping("/courses/new")
    public String createCourse(@ModelAttribute Course course, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User admin = userService.getUserByUsername(authentication.getName()).orElseThrow();
            courseService.createCourse(course, admin);
            redirectAttributes.addFlashAttribute("success", "Cours créé avec succès");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/courses/new";
        }
    }

    @GetMapping("/courses/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        model.addAttribute("course", course);
        return "admin/course-form";
    }
    
    @GetMapping("/courses/index/{id}")
    public String indexCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id)
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
            ragService.indexCourse(course);
            redirectAttributes.addFlashAttribute("success", "Cours indexé avec succès pour le système RAG");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'indexation : " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/edit/{id}")
    public String updateCourse(@PathVariable Long id, 
                              @ModelAttribute Course course,
                              RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("success", "Cours modifié avec succès");
            return "redirect:/admin/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/courses/edit/" + id;
        }
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Cours supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/publish/{id}")
    public String publishCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.publishCourse(id);
            redirectAttributes.addFlashAttribute("success", "Cours publié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/unpublish/{id}")
    public String unpublishCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.unpublishCourse(id);
            redirectAttributes.addFlashAttribute("success", "Cours dépublié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // ========== GESTION DES INSCRIPTIONS ==========

    @GetMapping("/courses/enrollments/{id}")
    public String manageEnrollments(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        List<User> allStudents = userService.getStudents();
        
        model.addAttribute("course", course);
        model.addAttribute("allStudents", allStudents);
        model.addAttribute("enrolledStudents", course.getEnrolledStudents());
        
        return "admin/course-enrollments";
    }

    @PostMapping("/courses/enroll/{courseId}/{studentId}")
    public String enrollStudent(@PathVariable Long courseId, 
                                @PathVariable Long studentId,
                                RedirectAttributes redirectAttributes) {
        try {
            courseService.enrollStudent(courseId, studentId);
            redirectAttributes.addFlashAttribute("success", "Étudiant inscrit avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/courses/enrollments/" + courseId;
    }

    @GetMapping("/courses/unenroll/{courseId}/{studentId}")
    public String unenrollStudent(@PathVariable Long courseId, 
                                  @PathVariable Long studentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            courseService.unenrollStudent(courseId, studentId);
            redirectAttributes.addFlashAttribute("success", "Étudiant désinscrit avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/courses/enrollments/" + courseId;
    }

    // ========== GESTION DES ÉTUDIANTS ==========

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", userService.getStudents());
        return "admin/students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/student-form";
    }

    @PostMapping("/students/new")
    public String createStudent(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            user.setRole(User.Role.STUDENT);
            user.setEnabled(true);
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Étudiant créé avec succès");
            return "redirect:/admin/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/students/new";
        }
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        User student = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        model.addAttribute("user", student);
        return "admin/student-form";
    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable Long id, 
                               @ModelAttribute User user,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", "Étudiant modifié avec succès");
            return "redirect:/admin/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/admin/students/edit/" + id;
        }
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Étudiant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }
}