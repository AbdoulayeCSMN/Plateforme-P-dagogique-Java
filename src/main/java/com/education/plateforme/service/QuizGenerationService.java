package com.education.plateforme.service;

import com.education.plateforme.dto.QuizDTO;
import com.education.plateforme.dto.QuizQuestionDTO;
import com.education.plateforme.model.Course;
import com.education.plateforme.model.Quiz;
import com.education.plateforme.model.QuizQuestion;
import com.education.plateforme.model.User;
import com.education.plateforme.repository.QuizRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class QuizGenerationService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private RAGService ragService;

    @Autowired
    private QuizRepository quizRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Génère un quiz basé sur le contenu du cours avec RAG et Mistral AI
     */
    public QuizDTO generateQuiz(Course course, User student, String difficulty, int numberOfQuestions, int timeLimitMinutes) {
        try {
            // 1. Récupérer le contexte pertinent via RAG
            String courseContext = ragService.getCourseContext(course, 10);

            // 2. Construire le prompt pour Mistral AI
            String fullPrompt = buildFullPrompt(courseContext, course.getTitle(), difficulty, numberOfQuestions);

            // 3. Appeler Mistral AI
            Prompt prompt = new Prompt(fullPrompt);
            String response = chatModel.call(prompt)
                    .getResult()
                    .getOutput()
                    .getText();

            // 4. Parser la réponse JSON
            List<QuizQuestionDTO> questions = parseQuizResponse(response);

            // 5. Créer et sauvegarder le quiz
            Quiz quiz = createQuizEntity(course, student, difficulty, timeLimitMinutes, questions);

            // 6. Retourner le DTO
            return new QuizDTO(quiz.getId(), difficulty, timeLimitMinutes, questions);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du quiz: " + e.getMessage(), e);
        }
    }

    private String buildFullPrompt(String courseContext, String courseTitle, String difficulty, int numberOfQuestions) {
        String difficultyInstruction = switch (difficulty) {
            case "EASY" -> "Questions simples et directes sur les concepts de base.";
            case "MEDIUM" -> "Questions nécessitant une compréhension et application des concepts.";
            case "HARD" -> "Questions complexes nécessitant analyse et synthèse approfondie.";
            default -> "Questions de difficulté moyenne.";
        };

        return String.format("""
            Tu es un expert pédagogique spécialisé dans la création de quiz QCM de haute qualité.
            
            Ton rôle est de générer des questions QCM basées UNIQUEMENT sur le contenu fourni.
            
            Niveau de difficulté : %s
            
            RÈGLES STRICTES :
            1. Génère UNIQUEMENT des questions basées sur le contenu fourni
            2. Chaque question doit avoir exactement 4 options
            3. Une seule option doit être correcte
            4. Les mauvaises réponses doivent être plausibles mais clairement incorrectes
            5. Fournis une explication claire pour chaque réponse
            6. Utilise un langage clair et professionnel
            7. Évite les formulations ambiguës
            
            FORMAT DE RÉPONSE (JSON strict) :
            [
              {
                "question": "Question ici ?",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctOptionIndex": 0,
                "explanation": "Explication détaillée de pourquoi cette réponse est correcte"
              }
            ]
            
            IMPORTANT : Réponds UNIQUEMENT avec le tableau JSON, sans texte supplémentaire avant ou après.
            Ne mets pas de balises markdown comme ```json ou ```.
            
            Contenu du cours "%s" :
            
            %s
            
            Génère %d questions QCM basées sur ce contenu.
            Assure-toi que les questions couvrent différents aspects du cours.
            Retourne uniquement le JSON sans aucun texte supplémentaire.
            """, difficultyInstruction, courseTitle, courseContext, numberOfQuestions);
    }

    private List<QuizQuestionDTO> parseQuizResponse(String response) {
        try {
            // Nettoyer la réponse (enlever les balises markdown si présentes)
            String cleanedResponse = response.trim();
            
            // Enlever les balises markdown si présentes
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            } else if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            
            cleanedResponse = cleanedResponse.trim();

            // Parser le JSON
            List<QuizQuestionDTO> questions = objectMapper.readValue(cleanedResponse, new TypeReference<List<QuizQuestionDTO>>() {});
            
            // Valider que nous avons au moins une question
            if (questions.isEmpty()) {
                throw new RuntimeException("Aucune question n'a été générée");
            }
            
            return questions;
        } catch (Exception e) {
            System.err.println("Erreur de parsing. Réponse reçue: " + response);
            throw new RuntimeException("Erreur lors du parsing de la réponse de Mistral AI: " + e.getMessage(), e);
        }
    }

    private Quiz createQuizEntity(Course course, User student, String difficulty, int timeLimitMinutes, List<QuizQuestionDTO> questionDTOs) {
        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setStudent(student);
        quiz.setDifficulty(difficulty);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        quiz.setTotalQuestions(questionDTOs.size());
        quiz.setCompleted(false);

        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < questionDTOs.size(); i++) {
            QuizQuestionDTO dto = questionDTOs.get(i);
            QuizQuestion question = new QuizQuestion();
            question.setQuiz(quiz);  // IMPORTANT: lier la question au quiz
            question.setQuestionText(dto.getQuestion());
            question.setOptions(dto.getOptions());
            question.setCorrectOptionIndex(dto.getCorrectOptionIndex());
            question.setExplanation(dto.getExplanation());
            questions.add(question);
        }

        quiz.setQuestions(questions);
        
        System.out.println("💾 Sauvegarde du quiz avec " + questions.size() + " questions");
        
        Quiz savedQuiz = quizRepository.save(quiz);
        
        System.out.println("✅ Quiz sauvegardé - ID: " + savedQuiz.getId() + " avec " + savedQuiz.getQuestions().size() + " questions");
        
        return savedQuiz;
    }
}