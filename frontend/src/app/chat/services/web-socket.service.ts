import { Injectable } from '@angular/core';
import { Observable, Subject } from "rxjs";
import { environment } from "src/environments/environment";

export type WebSocketEvent = "notif";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private ws: WebSocket | null = null;
  private reconnectDelay = 2000; // 2 seconds
  private isManuallyDisconnected = false;

  private events = new Subject<WebSocketEvent>();

 /* public connect(): Observable<WebSocketEvent> {
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);
    const events = new Subject<WebSocketEvent>();

    this.ws.onmessage = () => events.next("notif");
    this.ws.onclose = () => events.complete();
    this.ws.onerror = () => events.error("error");

    return events.asObservable();
  }

  public disconnect() {
    this.ws?.close();
    this.ws = null
  }*/

    /** Méthode pour se connecter au WebSocket */
  public connect(): Observable<WebSocketEvent> {
    if (this.ws) {
      console.warn('WebSocket already connected');
      return this.events.asObservable();
    }

    this.isManuallyDisconnected = false;
    this.initializeWebSocket();
    return this.events.asObservable();
  }

  /** Méthode pour déconnecter manuellement */
  public disconnect(): void {
    this.isManuallyDisconnected = true;
    this.ws?.close();
    this.ws = null;
  }

  /** Méthode pour gérer la reconnexion */
  private initializeWebSocket(): void {
    console.log('Attempting to connect to WebSocket...');
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);

    this.ws.onopen = () => {
      console.log('WebSocket connected');
      this.events.next("notif"); // Indiquer que la connexion est établie
    };

    this.ws.onmessage = () => this.events.next("notif");

    this.ws.onclose = () => {
      console.warn('WebSocket disconnected');
      this.events.complete();
      if (!this.isManuallyDisconnected) {
        this.scheduleReconnect(); // Planifier une tentative de reconnexion
      }
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket error', error);
      this.events.error('error');
      this.ws?.close(); // Forcer la fermeture pour déclencher la reconnexion
    };
  }

  /** Planifie une reconnexion après un délai défini */
  private scheduleReconnect(): void {
    console.log(`Reconnecting in ${this.reconnectDelay / 1000} seconds...`);
    setTimeout(() => {
      if (!this.isManuallyDisconnected) {
        this.initializeWebSocket();
      }
    }, this.reconnectDelay);
  }
}
