package com.education.plateforme.repository;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByPublishedTrue();
    List<Course> findByCreatedBy(User admin);
    List<Course> findByEnrolledStudentsContaining(User student);
}