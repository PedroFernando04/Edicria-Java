import { Injectable, signal } from '@angular/core';

export interface Aviso {
  id: number;
  texto: string;
  tipo: 'ok' | 'erro';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private proximoId = 1;
  readonly avisos = signal<Aviso[]>([]);

  ok(texto: string) {
    this.mostrar(texto, 'ok');
  }

  erro(texto: string) {
    this.mostrar(texto, 'erro');
  }

  fechar(id: number) {
    this.avisos.update((lista) => lista.filter((a) => a.id !== id));
  }

  private mostrar(texto: string, tipo: Aviso['tipo']) {
    const id = this.proximoId++;
    this.avisos.update((lista) => [...lista, { id, texto, tipo }].slice(-3));
    setTimeout(() => this.fechar(id), tipo === 'erro' ? 7000 : 3500);
  }
}
