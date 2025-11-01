# ChatApp – Application de messagerie en temps réel

## Aperçu du projet
ChatApp est une application de messagerie instantanée full-stack développée avec :

**Frontend : Angular 18**
** Backend : Spring Boot 3**
**Base de données & stockage : Firebase (Firestore & Cloud Storage)**
**Communication temps réel : WebSockets**
L’application permet aux utilisateurs de s’authentifier, d’échanger des messages texte et image, et de recevoir des notifications en temps réel lors de l’envoi d’un nouveau message.
 Fonctionnalités principales
 Authentification
Connexion des utilisateurs via un endpoint /auth/login
Génération et validation d’un JWT pour sécuriser les requêtes
Gestion de la session côté frontend avec stockage du token
 Messagerie
Envoi et réception de messages texte et image
Affichage dynamique des messages sans rechargement de la page
Gestion du format NewMessageRequest (avec ou sans image)
Stockage des images dans Firebase Cloud Storage
Enregistrement des métadonnées du message dans Firestore
 Notifications temps réel
Utilisation de WebSocket côté backend et RxJS côté Angular
Notification automatique lorsqu’un nouveau message est publié
Rafraîchissement automatique de la liste des messages
 Gestion des images
Téléversement via Firebase Admin SDK (Java)
Génération automatique d’URL d’accès public
Affichage intégré dans le flux de messages
