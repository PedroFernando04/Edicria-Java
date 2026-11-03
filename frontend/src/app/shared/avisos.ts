import { Component, inject } from '@angular/core';
import { ToastService } from '../core/toast.service';

/** Mensagens rápidas no canto da tela ("Livro adicionado", erros de API...). */
@Component({
  selector: 'app-avisos',
  template: `
    <div class="pilha" aria-live="polite">
      @for (aviso of toast.avisos(); track aviso.id) {
        <div class="aviso" [class.erro]="aviso.tipo === 'erro'" [attr.role]="aviso.tipo === 'erro' ? 'alert' : 'status'">
          <span>{{ aviso.texto }}</span>
          <button type="button" (click)="toast.fechar(aviso.id)" aria-label="Fechar aviso">×</button>
        </div>
      }
    </div>
  `,
  styles: `
    .pilha {
      position: fixed; z-index: 100; right: 1rem; bottom: 1rem;
      display: flex; flex-direction: column; gap: .5rem; width: min(24rem, 100% - 2rem);
    }
    .aviso {
      display: flex; align-items: flex-start; justify-content: space-between; gap: .75rem;
      padding: .75rem .9rem; border-radius: 6px; font-weight: 500;
      background: var(--tinta); color: var(--papel);
      border-left: 6px solid var(--marca);
      animation: entra .18s ease-out;
    }
    .aviso.erro { background: var(--perigo); border-left-color: #fff; color: #fff; }
    button {
      flex: none; border: 0; background: transparent; color: inherit;
      font-size: 1.3rem; line-height: 1; cursor: pointer; padding: 0 .2rem;
    }
    @keyframes entra { from { transform: translateY(8px); opacity: 0; } }
  `,
})
export class Avisos {
  protected readonly toast = inject(ToastService);
}
