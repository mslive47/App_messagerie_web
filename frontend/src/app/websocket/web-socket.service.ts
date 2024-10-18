import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';


export type WebSocketEvent = "notif";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private ws: WebSocket | null = null;

  constructor() { }

  public connect(): Observable<WebSocketEvent> {
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);
    const events = new Subject<WebSocketEvent>();

    this.ws.onmessage = () => events.next("notif");  // Émet 'notif' à chaque message reçu
    this.ws.onclose = () => events.complete();        // Termine l'Observable si la connexion est fermée
    this.ws.onerror = () => events.error("error");    // En cas d'erreur, émet une erreur

    return events.asObservable();                     // Retourne l'Observable
}

public disconnect() {
    this.ws?.close();  // Ferme la connexion si elle existe
    this.ws = null;    // Réinitialise la connexion
}
}
