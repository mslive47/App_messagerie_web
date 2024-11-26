import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { NewMessageRequest } from '../model/message.model';

export type WebSocketEvent = 'notif';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private ws: WebSocket | null = null;
  private reconnectDelay = 2000; // Délai entre les tentatives de reconnexion
  private isManuallyClosed = false; // Permet de distinguer une déconnexion volontaire
  private messageQueue: NewMessageRequest[] = []; // File d’attente pour les messages non envoyés
  private webSocketEvents$ = new Subject<WebSocketEvent>(); // Événements WebSocket

  public connect(): Observable<WebSocketEvent> {
    if (this.ws) {
      console.warn('WebSocket déjà connecté.');
      return this.webSocketEvents$.asObservable();
    }

    console.log('Connexion au WebSocket...');
    this.isManuallyClosed = false;
    this.initializeWebSocket();

    return this.webSocketEvents$.asObservable();
  }

  private initializeWebSocket(): void {
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);

    this.ws.onopen = () => {
      console.log('WebSocket connecté.');

      this.webSocketEvents$.next('notif'); // Notifier l'ouverture
      this.flushMessageQueue(); // Envoyer les messages en attenteS
    };

    this.ws.onmessage = (event) => {
      console.log('Message reçu via WebSocket :', event.data);
      this.webSocketEvents$.next(event.data as WebSocketEvent);
    };

    this.ws.onclose = () => {
      console.warn('WebSocket fermé.');
      this.ws = null;

      if (!this.isManuallyClosed) {
        console.log(
          `Reconnexion dans ${this.reconnectDelay / 1000} secondes...`
        );
        setTimeout(() => this.initializeWebSocket(), this.reconnectDelay);
      }
    };

    this.ws.onerror = (error) => {
      console.error('Erreur WebSocket :', error);
      this.ws?.close(); // Fermeture pour activer `onclose`
    };
  }

  /** Ajoute un message à la file d’attente */
  public queueMessage(message: NewMessageRequest): void {
    console.log('Ajout du message dans la file d’attente :', message);
    this.messageQueue.push(message);
  }

  /** Vide la file d’attente en envoyant les messages lorsque le WebSocket est prêt */
  /** Vide la file d’attente en envoyant les messages lorsque le WebSocket est prêt */
  private flushMessageQueue(): void {
    console.log('Vérification des messages en file d’attente...');
    while (this.messageQueue.length > 0) {
      const message = this.messageQueue.shift();
      console.log('Envoi du message mis en file d’attente :', message);

      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify(message));
      } else {
        console.warn('Impossible d’envoyer le message, WebSocket fermé.');
        break; // Arrêter si la connexion est de nouveau perdue
      }
    }
  }

  /* public disconnect() {
    this.ws?.close();
    this.ws = null;
  }*/

  /** Fermeture manuelle du WebSocket */
  public disconnect(): void {
    console.log('Déconnexion manuelle du WebSocket.');
    this.isManuallyClosed = true;
    this.ws?.close();
  }
}
