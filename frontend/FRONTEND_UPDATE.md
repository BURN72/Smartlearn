# SmartLearn Frontend - Guide de Mise à Jour

## 📋 Table des matières
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Changements d'API](#changements-dapi)
- [Lancement](#lancement)
- [Dépannage](#dépannage)

---

## Prérequis

- **Node.js** : version 18.x ou supérieure
- **npm** ou **yarn** : gestionnaire de paquets
- **Backend** : SmartLearn API en cours d'exécution sur `http://localhost:8080`

---

## Installation

### 1. Cloner ou mettre à jour le projet frontend

```bash
# Si c'est la première fois
git clone <repository-frontend-url>
cd smartlearn-frontend

# Si le projet existe déjà
git pull origin main
```

### 2. Installer les dépendances

```bash
npm install
# ou
yarn install
```

---

## Configuration

### 1. Variables d'environnement

Créer un fichier `.env` à la racine du projet frontend :

```env
# API Backend
VITE_API_URL=http://localhost:8080/api
VITE_API_BASE_URL=http://localhost:8080

# Environment
VITE_ENV=development
```

### 2. Configuration CORS

Le backend est configuré avec CORS activé pour :
- Toutes les origines (`*`) en développement
- Méthodes : GET, POST, PUT, PATCH, DELETE, OPTIONS
- Headers : `*`
- Credentials : enabled

**Note :** À adapter en production pour une source spécifique.

---

## Changements d'API

### ⚠️ Changements importants pour les DTOs

#### Lesson (Leçon)

**Ancien format :**
```json
{
  "id": 1,
  "title": "Introduction",
  "OrderIndex": 1,
  ...
}
```

**Nouveau format :**
```json
{
  "id": 1,
  "title": "Introduction",
  "orderIndex": 1,
  ...
}
```

#### Module

**Ancien format :**
```json
{
  "id": 1,
  "title": "Module 1",
  "OrderIndex": 1,
  "lessons": [...]
}
```

**Nouveau format :**
```json
{
  "id": 1,
  "title": "Module 1",
  "orderIndex": 1,
  "lessons": [...]
}
```

### 📌 Points clés

1. **`OrderIndex` → `orderIndex`** : Tous les champs `OrderIndex` en majuscules sont maintenant en camelCase `orderIndex`
2. **Noms de colonnes DB** : Utilisent snake_case (`order_index`) en base de données
3. **API REST** : Les réponses JSON utilisent camelCase

---

## Endpoints API

### Authentication
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
```

### Courses
```
GET /api/courses/published/all
GET /api/courses/:id
POST /api/courses
PUT /api/courses/:id
DELETE /api/courses/:id
POST /api/courses/:id/publish
POST /api/courses/:id/reject
POST /api/courses/:id/submit-review
GET /api/courses/instructor/my-courses
```

### Modules
```
GET /api/courses/:courseId/modules
GET /api/modules/:id
POST /api/modules
PUT /api/modules/:id
DELETE /api/modules/:id
```

### Lessons
```
GET /api/modules/:moduleId/lessons
GET /api/lessons/:id
POST /api/lessons
PUT /api/lessons/:id
DELETE /api/lessons/:id
```

### Enrollments
```
POST /api/enrollments
GET /api/enrollments/me
GET /api/enrollments/:id
PUT /api/enrollments/:id/activate
```

### Quiz
```
GET /api/quizzes/course/:courseId
GET /api/quizzes/:id
POST /api/quiz-attempts/start/:quizId
POST /api/quiz-attempts/:attemptId/submit
```

### Progress
```
GET /api/progress/course/:courseId
POST /api/progress/mark-complete/:lessonId
```

---

## Lancement

### Mode développement

```bash
npm run dev
# ou
yarn dev
```

L'application sera accessible sur `http://localhost:5173` (Vite)

### Mode production

```bash
npm run build
npm run preview
# ou
yarn build
yarn preview
```

---

## Intégration des DTOs

### Exemple: Récupérer un module avec ses leçons

```typescript
// TypeScript/React example

interface Lesson {
  id: number;
  title: string;
  type: 'VIDEO' | 'PDF' | 'QUIZ' | 'ASSIGNMENT';
  content?: string;
  duration?: number;
  orderIndex: number;  // ← Changement important
  isFree: boolean;
  moduleId: number;
}

interface Module {
  id: number;
  title: string;
  description?: string;
  orderIndex: number;  // ← Changement important
  courseId: number;
  lessons: Lesson[];
}

// Récupérer un module
const fetchModule = async (moduleId: number): Promise<Module> => {
  const response = await fetch(`${VITE_API_URL}/modules/${moduleId}`);
  return response.json();
};

// Créer une leçon
const createLesson = async (lessonData: Omit<Lesson, 'id'>) => {
  const response = await fetch(`${VITE_API_URL}/lessons`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...lessonData,
      orderIndex: lessonData.orderIndex  // Important
    })
  });
  return response.json();
};
```

---

## Dépannage

### Erreur: "Cannot read property 'OrderIndex' of undefined"

**Solution :** Mettre à jour tous les appels aux propriétés pour utiliser `orderIndex` en camelCase.

```diff
- const index = lesson.OrderIndex;
+ const index = lesson.orderIndex;
```

### Erreur CORS

Vérifier que :
1. Le backend est en cours d'exécution sur `http://localhost:8080`
2. La variable d'environnement `VITE_API_URL` est correcte
3. Les headers CORS sont bien configurés

### Erreur 404 sur les endpoints

Vérifier que :
1. L'URL de base est `http://localhost:8080/api`
2. Les ID des ressources sont valides
3. L'authentification est en place si nécessaire

---

## Commandes utiles

```bash
# Installation complète
npm install

# Lancer en développement
npm run dev

# Build production
npm run build

# Preview build
npm run preview

# Linter/Formatter
npm run lint

# Tests
npm run test

# Actualiser les types TypeScript
npm run type-check
```

---

## Notes importantes

- 🔐 **Authentification** : Les tokens JWT sont stockés en localStorage
- 🔄 **Refresh Token** : Implémenté avec des intercepteurs HTTP
- 📱 **Responsive** : L'UI est mobile-first
- ♿ **Accessibilité** : Suivre les standards WCAG 2.1

---

## Support

Pour toute question ou problème :
1. Vérifier les logs du backend : `http://localhost:8080/actuator/health`
2. Vérifier la console navigateur (F12)
3. Consulter la documentation API : `http://localhost:8080/swagger-ui.html`

---

**Dernier mise à jour :** 20 Mars 2026
