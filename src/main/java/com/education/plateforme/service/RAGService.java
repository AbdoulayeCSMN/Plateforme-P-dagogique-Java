package com.education.plateforme.service;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.DocumentChunk;
import com.education.plateforme.repository.DocumentChunkRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RAGService {

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private EmbeddingModel embeddingModel;

    // Cache des vector stores par cours
    private final Map<Long, VectorStore> vectorStoreCache = new HashMap<>();

    /**
     * Indexe le contenu d'un cours pour le RAG
     */
    public void indexCourse(Course course) {
        try {
            // Supprimer les anciens chunks
            documentChunkRepository.deleteByCourse(course);
            vectorStoreCache.remove(course.getId());

            // Découper le contenu en chunks
            List<String> chunks = splitIntoChunks(course.getContent(), 500);

            // Créer et sauvegarder les chunks
            List<DocumentChunk> documentChunks = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                DocumentChunk chunk = new DocumentChunk();
                chunk.setCourse(course);
                chunk.setContent(chunks.get(i));
                chunk.setChunkIndex(i);
                chunk.setIndexed(true);
                
                documentChunks.add(chunk);
            }

            documentChunkRepository.saveAll(documentChunks);
            
            // Initialiser le vector store pour ce cours
            initializeVectorStore(course);
            
            System.out.println("✅ Cours indexé avec succès : " + chunks.size() + " chunks créés");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'indexation du cours: " + e.getMessage(), e);
        }
    }

    /**
     * Initialise le vector store pour un cours
     */
    private void initializeVectorStore(Course course) {
        try {
            // Créer un SimpleVectorStore avec le modèle d'embedding
            SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
            
            List<DocumentChunk> chunks = documentChunkRepository.findByCourseAndIndexedTrue(course);
            
            if (chunks.isEmpty()) {
                System.out.println("⚠️ Aucun chunk trouvé pour le cours " + course.getId());
                return;
            }
            
            // Créer les documents pour le vector store
            List<Document> documents = chunks.stream()
                    .map(chunk -> {
                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("courseId", course.getId());
                        metadata.put("chunkIndex", chunk.getChunkIndex());
                        metadata.put("chunkId", chunk.getId());

                        // ✅ API correcte Spring AI
                        return new Document(chunk.getContent(), metadata);
                    })
                    .collect(Collectors.toList());

            
            System.out.println("📝 Ajout de " + documents.size() + " documents au vector store...");
            
            // Ajouter tous les documents au vector store
            vectorStore.add(documents);
            
            // Mettre en cache
            vectorStoreCache.put(course.getId(), vectorStore);
            
            System.out.println("✅ Vector store initialisé pour le cours " + course.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation du vector store: " + e.getMessage(), e);
        }
    }

    /**
     * Recherche les passages pertinents dans un cours
     */
    public List<String> searchRelevantContent(Course course, String query, int topK) {
        VectorStore vectorStore = vectorStoreCache.get(course.getId());
        
        if (vectorStore == null) {
            System.out.println("🔄 Vector store non trouvé en cache, initialisation...");
            initializeVectorStore(course);
            vectorStore = vectorStoreCache.get(course.getId());
        }

        try {
            // Créer une requête de recherche avec le builder
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(topK)
                    .build();

            // Effectuer la recherche
            List<Document> results = vectorStore.similaritySearch(searchRequest);
            
            // Extraire le contenu des documents
            return results.stream()
                    .map(doc -> doc.getText())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la recherche dans le vector store: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Récupère tout le contexte d'un cours pour la génération de quiz
     */
    public String getCourseContext(Course course, int maxChunks) {
        List<DocumentChunk> chunks = documentChunkRepository.findByCourseAndIndexedTrue(course);
        
        if (chunks.isEmpty()) {
            System.out.println("⚠️ Aucun chunk indexé trouvé, indexation du cours...");
            // Si pas encore indexé, indexer maintenant
            indexCourse(course);
            chunks = documentChunkRepository.findByCourseAndIndexedTrue(course);
        }

        String context = chunks.stream()
                .limit(maxChunks)
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n\n"));
        
        System.out.println("📚 Contexte récupéré : " + chunks.size() + " chunks (limité à " + maxChunks + ")");
        
        return context;
    }

    /**
     * Découpe un texte en chunks de taille donnée
     */
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        // Nettoyer le texte
        text = text.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        
        // Découper par phrases
        String[] sentences = text.split("(?<=[.!?])\\s+");
        
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        if (chunks.isEmpty() && !text.trim().isEmpty()) {
            chunks.add(text.trim());
        }
        
        System.out.println("✂️ Texte découpé en " + chunks.size() + " chunks");
        
        return chunks;
    }

    /**
     * Vérifie si un cours est indexé
     */
    public boolean isCourseIndexed(Course course) {
        long count = documentChunkRepository.countByCourse(course);
        boolean indexed = count > 0;
        System.out.println("🔍 Cours " + course.getId() + " indexé : " + indexed + " (" + count + " chunks)");
        return indexed;
    }

    /**
     * Obtient le nombre de chunks pour un cours
     */
    public long getChunkCount(Course course) {
        return documentChunkRepository.countByCourse(course);
    }
}