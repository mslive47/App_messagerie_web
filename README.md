# ChatApp – Application de messagerie en temps réel
---
## Aperçu du projet
ChatApp est une application de messagerie instantanée full-stack développée avec :

- Frontend : Angular 18
- Backend : Spring Boot 3
- Base de données & stockage : Firebase (Firestore & Cloud Storage)
- Communication temps réel : WebSockets
  
L’application permet aux utilisateurs de : 
- s’authentifier, d’échanger des messages texte et image,
- de recevoir des notifications en temps réel lors de l’envoi d’un nouveau message.
---  
## Fonctionnalités principales
- Authentification
- Connexion des utilisateurs via un endpoint /auth/login
- Génération et validation d’un JWT pour sécuriser les requêtes
- Gestion de la session côté frontend avec stockage du token
- Messagerie
- Envoi et réception de messages texte et image
- Affichage dynamique des messages sans rechargement de la page
- Gestion du format NewMessageRequest (avec ou sans image)
- Stockage des images dans Firebase Cloud Storage
- Enregistrement des métadonnées du message dans Firestore
- Notifications temps réel
- Utilisation de WebSocket côté backend et RxJS côté Angular
- Notification automatique lorsqu’un nouveau message est publié
- Rafraîchissement automatique de la liste des messages
- Gestion des images
- Téléversement via Firebase Admin SDK (Java)
- Génération automatique d’URL d’accès public
- Affichage intégré dans le flux de messages
---
## Installation et exécution
### Prérequis
 ```bash
- Node.js ≥ 18
- Angular CLI ≥ 18
- Java ≥ 17
- Maven ou Gradle
- Compte Firebase + fichier de configuration (clé admin SDK)
```
### Configuration du backend (Spring Boot)
- Cloner le projet :
- Ajouter le fichier de clé Firebase Admin (firebase-admin-key.json) dans src/main/resources/
- Configurer application.properties :
```bash
server.port=8080
spring.application.name=chatapp
firebase.config.path=classpath:firebase-admin-key.json
firebase.storage.bucket=ton-bucket.appspot.com
Lancer le backend :
mvn spring-boot:run
ou
./mvnw spring-boot:run
```
### Configuration du frontend (Angular)
- Cloner le frontend 
- Installer les dépendances :
```bash
npm install
Configurer l’environnement (src/environments/environment.ts) :
export const environment = {
  production: false,
  backendUrl: 'http://localhost:8080',
  firebaseConfig: {
    apiKey: "XXX",
    authDomain: "XXX.firebaseapp.com",
    projectId: "XXX",
    storageBucket: "XXX.appspot.com",
    messagingSenderId: "XXX",
    appId: "XXX"
  }
};
```
- Démarrer le serveur Angular :
```bash
ng serve
```
## Technologies utilisées
- Frontend	Angular 18, TypeScript, RxJS, WebSocket
- Backend	Spring Boot 3, WebSocket, Firebase Admin SDK, JJWT
- Base de données / Cloud	Firebase Firestore, Cloud Storage
- Sécurité	JWT (JSON Web Token), Spring Security Crypto
### Tests
```bash
Les tests unitaires Angular peuvent être lancés via :
ng test
Les tests backend (JUnit) via :
mvn test
```

