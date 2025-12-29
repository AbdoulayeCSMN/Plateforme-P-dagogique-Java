package com.education.plateforme.service;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.User;
import com.education.plateforme.repository.CourseRepository;
import com.education.plateforme.repository.UserRepository;
import com.education.plateforme.util.MarkdownUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Course createCourse(Course course, User admin) {
        course.setCreatedBy(admin);
        course.setPublished(false);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setContent(courseDetails.getContent());
        
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        courseRepository.delete(course);
    }

    public Course publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        
        if (course.getContent() == null || course.getContent().trim().isEmpty()) {
            throw new RuntimeException("Le cours doit contenir du contenu avant d'être publié");
        }
        
        course.setPublished(true);
        return courseRepository.save(course);
    }

    public Course unpublishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        course.setPublished(false);
        return courseRepository.save(course);
    }

    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Seuls les étudiants peuvent être inscrits à un cours");
        }
        
        course.getEnrolledStudents().add(student);
        courseRepository.save(course);
    }
    public Course getCourseWithHtmlContent(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Conversion Markdown -> HTML uniquement pour la vue
        String htmlContent = MarkdownUtils.toHtml(course.getContent());
        course.setContent(htmlContent);

        return course;
    }


    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        
        course.getEnrolledStudents().remove(student);
        courseRepository.save(course);
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByPublishedTrue();
    }

    public List<Course> getCoursesByStudent(User student) {
        return courseRepository.findByEnrolledStudentsContaining(student);
    }

    public boolean isStudentEnrolled(Long courseId, User student) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getEnrolledStudents().contains(student);
    }
}