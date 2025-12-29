package com.education.plateforme.repository;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {
    List<DocumentChunk> findByCourse(Course course);
    List<DocumentChunk> findByCourseAndIndexedTrue(Course course);
    void deleteByCourse(Course course);
    long countByCourse(Course course);
}