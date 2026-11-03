import { Component, ElementRef, effect, input, output, viewChild } from '@angular/core';

/**
 * Janela de diálogo usando o <dialog> nativo do navegador: ele já cuida de
 * prender o foco, fechar com Esc e escurecer o fundo.
 *
 * Uso: <app-modal [aberto]="mostrar()" titulo="..." (fechar)="mostrar.set(false)"> ... </app-modal>
 */
let proximoId = 0;

@Component({
  selector: 'app-modal',
  template: `
    <dialog #dlg [attr.aria-labelledby]="idTitulo" (cancel)="aoCancelar($event)" (click)="aoClicarFundo($event)">
      <div class="caixa">
        <header>
          <h2 [id]="idTitulo">{{ titulo() }}</h2>
          <button type="button" class="x" (click)="fechar.emit()" aria-label="Fechar">×</button>
        </header>
        <div class="corpo"><ng-content /></div>
      </div>
    </dialog>
  `,
  styles: `
    dialog {
      width: min(40rem, 100% - 1.5rem); max-height: calc(100dvh - 2rem);
      padding: 0; border: 1.5px solid var(--tinta); border-radius: 8px;
      background: var(--superficie); color: var(--tinta);
      box-shadow: 8px 8px 0 rgb(21 26 59 / .85);
    }
    dialog::backdrop { background: rgb(21 26 59 / .55); }
    .caixa { display: flex; flex-direction: column; max-height: calc(100dvh - 2rem); }
    header {
      display: flex; align-items: center; justify-content: space-between; gap: 1rem;
      padding: 1rem 1.25rem; border-bottom: 1px solid var(--linha);
    }
    .corpo { padding: 1.25rem; overflow-y: auto; }
    .x {
      border: 0; background: transparent; font-size: 1.7rem; line-height: 1;
      cursor: pointer; color: var(--tinta); padding: 0 .35rem;
    }
  `,
})
export class Modal {
  /** Cada janela precisa de um id próprio para o aria-labelledby. */
  protected readonly idTitulo = `modal-titulo-${proximoId++}`;

  readonly aberto = input.required<boolean>();
  readonly titulo = input.required<string>();
  readonly fechar = output<void>();

  private readonly dlg = viewChild.required<ElementRef<HTMLDialogElement>>('dlg');

  constructor() {
    effect(() => {
      const el = this.dlg().nativeElement;
      if (this.aberto() && !el.open) el.showModal();
      if (!this.aberto() && el.open) el.close();
    });
  }

  protected aoCancelar(evento: Event) {
    // Esc: deixamos o componente pai decidir (ele muda `aberto`).
    evento.preventDefault();
    this.fechar.emit();
  }

  protected aoClicarFundo(evento: MouseEvent) {
    if (evento.target === this.dlg().nativeElement) this.fechar.emit();
  }
}
