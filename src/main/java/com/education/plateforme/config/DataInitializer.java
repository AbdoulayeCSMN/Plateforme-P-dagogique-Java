package com.education.plateforme.config;

import com.education.plateforme.model.Course;
import com.education.plateforme.model.User;
import com.education.plateforme.repository.CourseRepository;
import com.education.plateforme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des données existent déjà
        if (userRepository.count() > 0) {
            System.out.println("✅ Les données existent déjà dans la base de données");
            return;
        }

        System.out.println("🚀 Initialisation des données de test...");

        // Créer un administrateur
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@plateforme.com");
        admin.setFullName("Administrateur Principal");
        admin.setRole(User.Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);
        System.out.println("👑 Admin créé : admin / admin123");

        // Créer des étudiants
        User student1 = createStudent("student", "student123", "Jean Dupont", "jean.dupont@example.com");
        User student2 = createStudent("marie.martin", "password123", "Marie Martin", "marie.martin@example.com");
        User student3 = createStudent("pierre.dubois", "password123", "Pierre Dubois", "pierre.dubois@example.com");
        User student4 = createStudent("sophie.bernard", "password123", "Sophie Bernard", "sophie.bernard@example.com");
        
        System.out.println("4 étudiants créés");

        // Créer des cours
        Course course1 = createCourse(
            admin,
            "Introduction à Spring Boot",
            "Apprenez les fondamentaux de Spring Boot et créez votre première application web.",
            getCourseContent1()
        );

        Course course2 = createCourse(
            admin,
            "Sécurité avec Spring Security",
            "Maîtrisez Spring Security pour sécuriser vos applications web.",
            getCourseContent2()
        );

        Course course3 = createCourse(
            admin,
            "Bases de données avec JPA",
            "Découvrez JPA et Hibernate pour gérer vos données efficacement.",
            getCourseContent3()
        );

        @SuppressWarnings("unused")
		Course course4 = createCourse(
            admin,
            "Intelligence Artificielle et Machine Learning",
            "Introduction aux concepts fondamentaux de l'IA et du ML.",
            getCourseContent4()
        );

        System.out.println("📚 4 cours créés");

        // Publier certains cours
        course1.setPublished(true);
        course2.setPublished(true);
        course3.setPublished(true);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);

        // Inscrire des étudiants aux cours
        course1.getEnrolledStudents().add(student1);
        course1.getEnrolledStudents().add(student2);
        course1.getEnrolledStudents().add(student3);
        
        course2.getEnrolledStudents().add(student1);
        course2.getEnrolledStudents().add(student2);
        
        course3.getEnrolledStudents().add(student1);
        course3.getEnrolledStudents().add(student3);
        course3.getEnrolledStudents().add(student4);

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);

        System.out.println("Inscriptions effectuées");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Initialisation terminée avec succès !");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Comptes disponibles :");
        System.out.println("   Admin : admin / admin123");
        System.out.println("   Étudiant : student / student123");
        System.out.println("   Étudiant : marie.martin / password123");
        System.out.println("   Étudiant : pierre.dubois / password123");
        System.out.println("   Étudiant : sophie.bernard / password123");
        System.out.println("═══════════════════════════════════════════════════════");
    }

    private User createStudent(String username, String password, String fullName, String email) {
        User student = new User();
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(password));
        student.setEmail(email);
        student.setFullName(fullName);
        student.setRole(User.Role.STUDENT);
        student.setEnabled(true);
        return userRepository.save(student);
    }

    private Course createCourse(User admin, String title, String description, String content) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setContent(content);
        course.setCreatedBy(admin);
        course.setPublished(false);
        return courseRepository.save(course);
    }

    private String getCourseContent1() {
        return """
            # Introduction à Spring Boot
            
            ## Qu'est-ce que Spring Boot ?
            
            Spring Boot est un framework Java open source qui facilite la création d'applications Spring autonomes et prêtes pour la production. Il offre une approche "convention plutôt que configuration" qui permet aux développeurs de se concentrer sur le code métier plutôt que sur la configuration.
            
            ## Avantages de Spring Boot
            
            1. **Configuration automatique** : Spring Boot configure automatiquement votre application en fonction des dépendances présentes dans votre classpath.
            
            2. **Serveur embarqué** : Pas besoin de déployer des fichiers WAR sur un serveur externe. Spring Boot inclut Tomcat, Jetty ou Undertow.
            
            3. **Starter dependencies** : Des dépendances préconfigurées qui regroupent les bibliothèques nécessaires pour des fonctionnalités spécifiques.
            
            4. **Production-ready** : Inclut des fonctionnalités comme les health checks, les métriques et la surveillance.
            
            ## Architecture d'une application Spring Boot
            
            Une application Spring Boot typique comprend :
            - Une classe principale avec l'annotation @SpringBootApplication
            - Des contrôleurs pour gérer les requêtes HTTP
            - Des services pour la logique métier
            - Des repositories pour l'accès aux données
            - Des entités pour modéliser les données
            
            ## Création de votre première application
            
            Pour créer une application Spring Boot, vous pouvez utiliser Spring Initializr (start.spring.io) ou votre IDE préféré. Sélectionnez les dépendances dont vous avez besoin et générez le projet.
            
            ## Les annotations essentielles
            
            - @SpringBootApplication : Combine @Configuration, @EnableAutoConfiguration et @ComponentScan
            - @RestController : Définit un contrôleur REST
            - @Service : Marque une classe comme service
            - @Repository : Marque une classe comme repository
            - @Autowired : Injecte automatiquement les dépendances
            
            ## Configuration avec application.properties
            
            Le fichier application.properties permet de configurer votre application : port du serveur, connexion à la base de données, logging, etc.
            
            Exemple :
            server.port=8080
            spring.datasource.url=jdbc:mysql://localhost:3306/mydb
            
            ## Conclusion
            
            Spring Boot simplifie considérablement le développement d'applications Java modernes. Sa configuration automatique et ses starters permettent de démarrer rapidement et de se concentrer sur la valeur métier.
            """;
    }

    private String getCourseContent2() {
        return """
            # Sécurité avec Spring Security
            
            ## Introduction à Spring Security
            
            Spring Security est un framework puissant et hautement personnalisable pour l'authentification et le contrôle d'accès dans les applications Java. Il est le standard de facto pour sécuriser les applications Spring.
            
            ## Concepts Fondamentaux
            
            ### 1. Authentification
            L'authentification est le processus de vérification de l'identité d'un utilisateur. Spring Security supporte plusieurs mécanismes :
            - Formulaires de connexion
            - HTTP Basic
            - OAuth 2.0
            - LDAP
            - JWT (JSON Web Tokens)
            
            ### 2. Autorisation
            L'autorisation détermine si un utilisateur authentifié a le droit d'accéder à une ressource spécifique. Spring Security utilise :
            - Les rôles (ROLE_USER, ROLE_ADMIN)
            - Les autorités (permissions granulaires)
            - Les expressions SpEL pour des règles complexes
            
            ## Architecture de Spring Security
            
            Spring Security utilise une chaîne de filtres (Filter Chain) qui interceptent les requêtes HTTP. Les principaux composants sont :
            
            1. **SecurityFilterChain** : Définit les règles de sécurité
            2. **AuthenticationManager** : Gère l'authentification
            3. **UserDetailsService** : Charge les informations utilisateur
            4. **PasswordEncoder** : Encode les mots de passe
            
            ## Configuration de base
            
            Pour sécuriser une application, créez une classe de configuration avec @EnableWebSecurity et définissez un SecurityFilterChain.
            
            Les règles d'autorisation peuvent être définies par URL :
            - /public/** : accessible à tous
            - /admin/** : réservé aux administrateurs
            - /user/** : réservé aux utilisateurs authentifiés
            
            ## Protection CSRF
            
            Le Cross-Site Request Forgery (CSRF) est une attaque où un utilisateur malveillant force un utilisateur authentifié à exécuter des actions non désirées. Spring Security active la protection CSRF par défaut.
            
            ## Encodage des mots de passe
            
            Il est crucial de ne jamais stocker des mots de passe en clair. Spring Security recommande BCryptPasswordEncoder qui utilise l'algorithme bcrypt avec un salt aléatoire.
            
            ## Session Management
            
            Spring Security gère automatiquement les sessions utilisateur. Vous pouvez configurer :
            - La politique de création de session
            - Le nombre maximum de sessions concurrentes
            - Le comportement en cas de session expirée
            
            ## Annotations de sécurité
            
            Spring Security fournit des annotations pour sécuriser les méthodes :
            - @PreAuthorize : Vérifie avant l'exécution
            - @PostAuthorize : Vérifie après l'exécution
            - @Secured : Vérifie les rôles
            - @RolesAllowed : Annotation JSR-250
            
            ## Bonnes pratiques
            
            1. Toujours encoder les mots de passe
            2. Utiliser HTTPS en production
            3. Implémenter une politique de mots de passe robuste
            4. Activer la protection CSRF
            5. Limiter les tentatives de connexion
            6. Logger les événements de sécurité
            
            ## Conclusion
            
            Spring Security est un outil essentiel pour sécuriser vos applications. Bien qu'il puisse sembler complexe au début, sa flexibilité et sa puissance en font un choix incontournable pour les applications professionnelles.
            """;
    }

    private String getCourseContent3() {
        return """
            # Bases de données avec JPA (Java Persistence API)
            
            ## Introduction à JPA
            
            JPA (Java Persistence API) est une spécification Java pour la gestion de la persistance des données relationnelles dans les applications Java. Hibernate est l'implémentation JPA la plus populaire.
            
            ## Concepts Fondamentaux
            
            ### 1. Entités
            Une entité est une classe Java qui représente une table de base de données. Chaque instance d'entité correspond à une ligne dans la table.
            
            Annotations principales :
            - @Entity : Marque une classe comme entité JPA
            - @Table : Spécifie le nom de la table
            - @Id : Définit la clé primaire
            - @GeneratedValue : Configure la génération automatique de l'ID
            - @Column : Configure les propriétés de la colonne
            
            ### 2. Relations entre entités
            
            JPA supporte plusieurs types de relations :
            
            **One-to-One** : Une entité est liée à une seule autre entité
            Exemple : Un utilisateur a un seul profil
            
            **One-to-Many / Many-to-One** : Une entité est liée à plusieurs autres
            Exemple : Un département a plusieurs employés
            
            **Many-to-Many** : Plusieurs entités sont liées entre elles
            Exemple : Des étudiants inscrits à plusieurs cours
            
            ### 3. EntityManager
            
            L'EntityManager est l'interface principale pour interagir avec le contexte de persistance. Il permet de :
            - Persister de nouvelles entités
            - Rechercher des entités
            - Mettre à jour des entités
            - Supprimer des entités
            
            ## Spring Data JPA
            
            Spring Data JPA simplifie l'accès aux données en fournissant des repositories. Un repository est une interface qui hérite de JpaRepository et fournit automatiquement des méthodes CRUD.
            
            Avantages :
            - Pas besoin d'implémenter les méthodes CRUD
            - Génération automatique de requêtes basée sur les noms de méthodes
            - Support des requêtes personnalisées avec @Query
            - Pagination et tri intégrés
            
            ## Query Methods
            
            Spring Data JPA génère automatiquement des requêtes à partir des noms de méthodes :
            
            - findByNom : Recherche par nom
            - findByAgeGreaterThan : Recherche par âge supérieur
            - findByNomAndPrenom : Recherche avec plusieurs critères
            - countByStatut : Compte les entités
            - deleteByNom : Supprime par nom
            
            ## JPQL (Java Persistence Query Language)
            
            JPQL est un langage de requête orienté objet pour interroger les entités JPA. Il ressemble à SQL mais opère sur des objets Java plutôt que sur des tables.
            
            Exemple :
            SELECT u FROM User u WHERE u.age > 18
            
            ## Transactions
            
            Les transactions garantissent la cohérence des données. Spring fournit l'annotation @Transactional pour gérer les transactions de manière déclarative.
            
            Propriétés importantes :
            - propagation : Comment la transaction se propage
            - isolation : Niveau d'isolation de la transaction
            - readOnly : Optimisation pour les lectures
            - rollbackFor : Exceptions déclenchant un rollback
            
            ## Lazy vs Eager Loading
            
            **Lazy Loading** : Les données associées sont chargées à la demande
            **Eager Loading** : Les données associées sont chargées immédiatement
            
            Le choix dépend de vos besoins en performance. Le lazy loading évite de charger des données inutiles, mais peut causer des problèmes si la session est fermée.
            
            ## Optimisation des performances
            
            1. **Utiliser des projections** : Ne charger que les champs nécessaires
            2. **Batch fetching** : Charger plusieurs entités en une seule requête
            3. **Caching** : Mettre en cache les requêtes fréquentes
            4. **Index de base de données** : Accélérer les recherches
            5. **Éviter N+1 queries** : Utiliser JOIN FETCH
            
            ## Validation des données
            
            JPA s'intègre avec Bean Validation (JSR-303) pour valider les données :
            - @NotNull : Champ obligatoire
            - @Size : Taille min/max
            - @Email : Format email
            - @Min/@Max : Valeurs numériques
            - @Pattern : Expression régulière
            
            ## Bonnes pratiques
            
            1. Toujours utiliser des transactions
            2. Définir des stratégies de fetch appropriées
            3. Utiliser des index sur les colonnes recherchées
            4. Éviter les requêtes N+1
            5. Valider les données avant la persistance
            6. Utiliser des DTO pour les transferts de données
            7. Fermer les ressources correctement
            
            ## Conclusion
            
            JPA et Spring Data JPA simplifient considérablement la gestion de la persistance des données. En comprenant les concepts fondamentaux et en suivant les bonnes pratiques, vous pouvez créer des applications robustes et performantes.
            """;
    }

    private String getCourseContent4() {
        return """
            # Intelligence Artificielle et Machine Learning
            
            ## Introduction à l'Intelligence Artificielle
            
            L'Intelligence Artificielle (IA) est la simulation de l'intelligence humaine par des machines. Elle englobe plusieurs domaines dont le Machine Learning, le Deep Learning, le traitement du langage naturel et la vision par ordinateur.
            
            ## Qu'est-ce que le Machine Learning ?
            
            Le Machine Learning (ML) est une branche de l'IA qui permet aux machines d'apprendre à partir de données sans être explicitement programmées. Au lieu d'écrire des règles, on entraîne des modèles sur des exemples.
            
            ## Types d'apprentissage
            
            ### 1. Apprentissage Supervisé
            Le modèle apprend à partir de données étiquetées (avec des réponses connues).
            
            Applications :
            - Classification : Catégoriser des emails (spam/non-spam)
            - Régression : Prédire des prix immobiliers
            - Reconnaissance d'images : Identifier des objets
            
            Algorithmes populaires :
            - Régression linéaire
            - Régression logistique
            - Arbres de décision
            - Random Forest
            - Support Vector Machines (SVM)
            - Réseaux de neurones
            
            ### 2. Apprentissage Non Supervisé
            Le modèle découvre des structures cachées dans des données non étiquetées.
            
            Applications :
            - Clustering : Segmentation de clientèle
            - Réduction de dimensionnalité : Visualisation de données
            - Détection d'anomalies : Fraude bancaire
            
            Algorithmes populaires :
            - K-means
            - Clustering hiérarchique
            - PCA (Principal Component Analysis)
            - t-SNE
            
            ### 3. Apprentissage par Renforcement
            Le modèle apprend par essai-erreur en recevant des récompenses ou des pénalités.
            
            Applications :
            - Jeux vidéo (AlphaGo)
            - Robotique
            - Véhicules autonomes
            - Trading algorithmique
            
            ## Le processus de Machine Learning
            
            1. **Collecte des données** : Rassembler des données pertinentes et de qualité
            2. **Préparation des données** : Nettoyage, normalisation, gestion des valeurs manquantes
            3. **Exploration des données** : Analyse statistique et visualisation
            4. **Sélection du modèle** : Choisir l'algorithme approprié
            5. **Entraînement** : Apprendre les paramètres du modèle
            6. **Évaluation** : Mesurer les performances (précision, rappel, F1-score)
            7. **Optimisation** : Ajuster les hyperparamètres
            8. **Déploiement** : Mettre le modèle en production
            
            ## Deep Learning
            
            Le Deep Learning utilise des réseaux de neurones profonds avec plusieurs couches cachées. Il excelle dans :
            - Vision par ordinateur
            - Traitement du langage naturel
            - Génération de contenu (texte, images, audio)
            - Traduction automatique
            
            Architectures populaires :
            - CNN (Convolutional Neural Networks) pour les images
            - RNN (Recurrent Neural Networks) pour les séquences
            - LSTM (Long Short-Term Memory) pour le texte
            - Transformers (BERT, GPT) pour le NLP
            
            ## Traitement du Langage Naturel (NLP)
            
            Le NLP permet aux machines de comprendre et de générer du langage humain.
            
            Applications :
            - Chatbots et assistants virtuels
            - Analyse de sentiment
            - Traduction automatique
            - Résumé de texte
            - Génération de texte
            
            Techniques clés :
            - Tokenization
            - Word embeddings (Word2Vec, GloVe)
            - Named Entity Recognition (NER)
            - Part-of-Speech Tagging
            - Transformers et attention mechanisms
            
            ## Vision par Ordinateur
            
            La vision par ordinateur permet aux machines d'interpréter des images et des vidéos.
            
            Applications :
            - Reconnaissance faciale
            - Détection d'objets
            - Segmentation d'images
            - Classification d'images
            - Véhicules autonomes
            
            ## Évaluation des modèles
            
            Métriques importantes :
            - **Précision** : Proportion de prédictions correctes
            - **Rappel** : Proportion de cas positifs détectés
            - **F1-Score** : Moyenne harmonique de précision et rappel
            - **Matrice de confusion** : Détail des prédictions
            - **AUC-ROC** : Performance du classifieur
            
            ## Défis du Machine Learning
            
            1. **Overfitting** : Le modèle mémorise les données d'entraînement
            2. **Underfitting** : Le modèle est trop simple
            3. **Biais dans les données** : Données non représentatives
            4. **Données déséquilibrées** : Classes inégalement représentées
            5. **Manque de données** : Pas assez d'exemples
            6. **Explicabilité** : Comprendre les décisions du modèle
            
            ## Outils et bibliothèques
            
            Python est le langage dominant en ML/IA avec :
            - **scikit-learn** : ML classique
            - **TensorFlow** : Deep Learning
            - **PyTorch** : Deep Learning et recherche
            - **Keras** : Interface haut niveau pour réseaux de neurones
            - **Pandas** : Manipulation de données
            - **NumPy** : Calcul numérique
            - **Matplotlib/Seaborn** : Visualisation
            
            ## IA Générative
            
            L'IA générative crée du nouveau contenu :
            - **GPT** : Génération de texte
            - **DALL-E** : Génération d'images
            - **Stable Diffusion** : Art génératif
            - **Midjourney** : Création artistique
            
            ## Éthique et IA responsable
            
            Questions importantes :
            - Biais algorithmiques
            - Protection de la vie privée
            - Transparence et explicabilité
            - Responsabilité des décisions
            - Impact environnemental (consommation énergétique)
            
            ## Conclusion
            
            L'IA et le Machine Learning transforment tous les secteurs. Comprendre les concepts fondamentaux, choisir les bons outils et suivre les bonnes pratiques sont essentiels pour créer des systèmes intelligents efficaces et responsables.
            
            Le domaine évolue rapidement avec de nouvelles avancées chaque jour, notamment dans les modèles de langage de grande taille (LLMs) et l'IA générative.
            """;
    }
}