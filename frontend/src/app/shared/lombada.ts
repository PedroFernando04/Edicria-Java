import { Component, computed, input } from '@angular/core';
import { Categoria } from '../core/models';

/**
 * Uma lombada de livro. A altura e a espessura vêm do número de páginas e a cor
 * vem da categoria (livro, HQ, mangá, graphic novel).
 */
@Component({
  selector: 'app-lombada',
  template: `
    <span class="titulo">{{ titulo() }}</span>
    <span class="autor">{{ autor() }}</span>
    @if (nota() !== null) {
      <span class="selo">{{ notaFormatada() }}</span>
    }
  `,
  host: {
    '[style.height.px]': 'altura()',
    '[style.width.px]': 'largura()',
    '[style.--cor]': '"var(--lombada-" + categoriaCss() + ")"',
  },
  styles: `
    :host {
      position: relative; display: flex; flex-direction: column; align-items: center;
      justify-content: space-between; gap: .5rem; flex: none;
      padding: 1.9rem .15rem .7rem; overflow: hidden;
      background:
        linear-gradient(90deg, rgb(255 255 255 / .16) 0 3px, transparent 3px calc(100% - 3px), rgb(0 0 0 / .18) calc(100% - 3px)),
        var(--cor);
      color: #fff; border-radius: 3px 3px 0 0;
      box-shadow: inset 0 -4px 0 rgb(0 0 0 / .22);
    }
    .titulo {
      writing-mode: vertical-rl; font-family: var(--fonte-titulo); font-weight: 600;
      font-size: .95rem; line-height: 1.1; text-align: left; flex: 1; min-height: 0;
      overflow: hidden; text-overflow: ellipsis; max-height: 100%;
    }
    .autor {
      writing-mode: vertical-rl; font-size: .68rem; opacity: .8; white-space: nowrap;
      max-height: 4.2rem; overflow: hidden; text-overflow: ellipsis;
    }
    .selo {
      position: absolute; top: 0; left: 0; right: 0; padding: .1rem 0;
      background: var(--marca); color: var(--tinta); text-align: center;
      font-size: .7rem; font-weight: 700; font-variant-numeric: tabular-nums;
    }
  `,
})
export class Lombada {
  readonly titulo = input.required<string>();
  readonly autor = input<string>('');
  readonly categoria = input.required<Categoria>();
  readonly paginas = input.required<number>();
  readonly nota = input<number | null>(null);

  protected readonly categoriaCss = computed(() => this.categoria().toLowerCase());
  protected readonly notaFormatada = computed(() => (this.nota() ?? 0).toLocaleString('pt-BR', { maximumFractionDigits: 1 }));

  // 100 páginas ≈ 170px de altura; 600+ páginas ≈ 280px (limites para não distorcer a estante)
  protected readonly altura = computed(() => Math.round(Math.min(280, Math.max(170, 150 + this.paginas() * 0.22))));
  protected readonly largura = computed(() => Math.round(Math.min(64, Math.max(38, 32 + this.paginas() * 0.045))));
}
