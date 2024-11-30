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
      this.events.next("notif"); 
    };

    this.ws.onmessage = () => this.events.next("notif");

    this.ws.onclose = () => {
      console.warn('WebSocket disconnected');
      this.events.complete();
      if (!this.isManuallyDisconnected) {
        this.scheduleReconnect(); 
      }
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket error', error);
      this.events.error('error');
      this.ws?.close(); 
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
