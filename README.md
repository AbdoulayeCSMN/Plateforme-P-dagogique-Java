# 🎓 Plateforme Pédagogique Intelligente avec IA

## 📋 Informations du Projet

**Projet Mini-Projet Spring Boot**  
**Année Universitaire:** 2024-2025  
**Technologies:** Spring Boot 3.5.9, Spring AI 1.1.2, Mistral AI, MySQL, Thymeleaf, Tailwind CSS

---

## 🎯 Objectif du Projet

Développer une plateforme pédagogique sécurisée et intelligente permettant aux étudiants de se former via des contenus pédagogiques et de s'auto-évaluer grâce à des quiz générés automatiquement par l'Intelligence Artificielle.

### Problématique Résolue

Comment permettre aux étudiants d'évaluer efficacement leurs connaissances de manière personnalisée et automatisée, tout en garantissant que les quiz sont strictement basés sur le contenu du cours ?

---

## 🚀 Fonctionnalités Principales

### Pour l'Administrateur
- ✅ Gestion complète des cours (création, modification, publication)
- ✅ Gestion des comptes étudiants
- ✅ Inscription des étudiants aux cours
- ✅ Indexation des cours pour le système RAG (Retrieval-Augmented Generation)
- ✅ Tableau de bord avec statistiques

### Pour l'Étudiant
- ✅ Consultation des cours inscrits
- ✅ Lecture du contenu pédagogique
- ✅ Génération automatique de quiz par IA
- ✅ Passage de quiz interactifs avec timer
- ✅ Consultation de l'historique et des résultats
- ✅ Système de progression et validation de cours

---

## 🤖 Intelligence Artificielle Intégrée

### 1. **LLM (Large Language Model) - Mistral AI**
- Génération automatique de questions QCM pertinentes
- Création d'explications détaillées pour chaque réponse
- Adaptation du niveau de difficulté

### 2. **RAG (Retrieval-Augmented Generation)**
- Découpage intelligent du contenu en segments (chunks)
- Indexation vectorielle pour recherche sémantique
- Garantie que les questions sont basées uniquement sur le cours
- Évite les hallucinations du LLM

### 3. **Agent IA**
- Analyse de l'historique de l'étudiant
- Ajustement dynamique de la difficulté (Facile, Moyen, Difficile)
- Calcul du temps limite optimal
- Évaluation de la progression et validation du cours
- Recommandations personnalisées

---

## 🏗️ Architecture Technique

### Backend
- **Framework:** Spring Boot 3.5.9
- **Sécurité:** Spring Security (authentification et autorisation par rôles)
- **Persistance:** Spring Data JPA avec MySQL
- **IA:** Spring AI 1.1.2 avec Mistral AI
- **Template Engine:** Thymeleaf

### Frontend
- **Framework CSS:** Tailwind CSS
- **Interactivité:** Alpine.js
- **Design:** Interface moderne avec mode sombre/clair
- **Responsive:** Compatible mobile et desktop

### Base de Données
- **SGBD:** MySQL 8
- **Entités principales:** User, Course, Quiz, QuizQuestion, DocumentChunk

---

## 📊 Flux de Fonctionnement

### 1. Création de Contenu (Admin)
```
Admin → Crée un cours → Publie le cours → Indexe le contenu (RAG)
```

### 2. Génération de Quiz (Étudiant + IA)
```
Étudiant demande un quiz
    ↓
Agent IA analyse l'historique
    ↓
RAG récupère le contenu pertinent
    ↓
LLM (Mistral AI) génère les questions
    ↓
Agent IA ajuste la difficulté et le temps
    ↓
Quiz présenté à l'étudiant
```

### 3. Évaluation et Progression
```
Étudiant soumet les réponses
    ↓
Système calcule le score
    ↓
Agent IA évalue la progression
    ↓
Recommandations et validation du cours
```

---

## 🔒 Sécurité

- Authentification obligatoire (Spring Security)
- Séparation stricte des rôles (ADMIN / STUDENT)
- Protection CSRF
- Encodage BCrypt des mots de passe
- Autorisation basée sur les rôles pour chaque endpoint

---

## 💾 Structure de la Base de Données

### Tables Principales
- **users:** Comptes (admin et étudiants)
- **courses:** Contenus pédagogiques
- **course_enrollments:** Inscriptions étudiants-cours
- **document_chunks:** Segments indexés pour le RAG
- **quizzes:** Quiz générés
- **quiz_questions:** Questions avec options et explications

---

## 🎨 Captures d'Écran

### Dashboard Administrateur
- Vue d'ensemble des statistiques
- Gestion des cours et étudiants
- Interface moderne et intuitive

### Espace Étudiant
- Cours inscrits avec progression
- Génération de quiz IA
- Quiz interactif avec timer
- Résultats détaillés avec explications

---

## 🚀 Installation et Lancement

### Prérequis
- Java 17+
- MySQL 8+
- Maven 3.8+
- Clé API Mistral AI

### Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/plateforme_pedagogique
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.ai.mistralai.api-key=votre_cle_mistral_ai
```

### Lancement
```bash
npm install
mvn clean install
mvn spring-boot:run
```

### Accès
```
http://localhost:8085
Admin: admin / admin123
Étudiant: student / student123
```

---

## 📈 Points Forts du Projet

1. **Innovation:** Intégration complète de l'IA avec RAG et Agent intelligent
2. **Sécurité:** Architecture sécurisée avec Spring Security
3. **UX:** Interface moderne et intuitive avec Tailwind CSS
4. **Personnalisation:** Adaptation automatique aux performances de l'étudiant
5. **Évolutivité:** Architecture modulaire et extensible
6. **Qualité:** Code structuré suivant les bonnes pratiques Spring

---

## 🔮 Perspectives d'Évolution

- Support de documents PDF, images et vidéos
- Système de certification automatique
- Tableaux de bord analytiques avancés
- IA conversationnelle (chatbot pédagogique)
- Système de recommandation de cours
- Gamification avec badges et niveaux

---

## 👥 Équipe de Développement

CHAIBOU ABDOULAYE

---

## 📝 Licence

Projet académique - Mini-Projet Spring Boot

---

**Date de réalisation:** Décembre 2024 - Janvier 2025
```
