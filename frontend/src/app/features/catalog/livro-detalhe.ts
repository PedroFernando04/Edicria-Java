import { Component, computed, effect, inject, input, signal, untracked } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { LeiturasApi, LivrosApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { mensagemDeErro } from '../../core/erros';
import { Livro, LivroCliente } from '../../core/models';
import { nomeCategoria, nomeFormato, nomeLingua, nomeStatus } from '../../core/rotulos';
import { Lombada } from '../../shared/lombada';
import { LeituraDialog } from '../shelf/leitura-dialog';
import { LivroDialog } from './livro-dialog';

@Component({
  selector: 'app-livro-detalhe',
  imports: [RouterLink, DatePipe, Lombada, LeituraDialog, LivroDialog],
  template: `
    <div class="pagina">
      <p class="voltar"><a routerLink="/catalogo">Voltar ao catálogo</a></p>

      @if (carregando()) {
        <p class="carregando">Carregando livro...</p>
      } @else if (erro()) {
        <div class="vazio" role="alert">
          <h3>Não consegui abrir esse livro</h3>
          <p>{{ erro() }}</p>
        </div>
      } @else if (livro(); as l) {
        <div class="topo">
          <div class="prateleira" aria-hidden="true">
            <app-lombada [titulo]="l.titulo" [autor]="l.autor.nome" [categoria]="l.categoria" [paginas]="l.numeroPaginas" [nota]="leitura()?.nota ?? null" />
          </div>
          <div>
            <h1>{{ l.titulo }}</h1>
            <p class="autor">{{ l.autor.nome }}</p>
            <dl>
              <div><dt>Editora</dt><dd>{{ l.editora.nome }}</dd></div>
              <div><dt>Categoria</dt><dd>{{ nomeCat(l.categoria) }}</dd></div>
              <div><dt>Formato</dt><dd>{{ nomeFmt(l.formato) }}</dd></div>
              <div><dt>Páginas</dt><dd>{{ l.numeroPaginas }}</dd></div>
              <div><dt>Lançamento</dt><dd>{{ l.dataLancamento | date: 'dd/MM/yyyy' : 'UTC' }}</dd></div>
            </dl>
            <button type="button" class="btn btn-pequeno" (click)="editarLivro.set(true)">Editar dados do livro</button>
          </div>
        </div>

        <section class="leitura" aria-labelledby="h-leitura">
          <h2 id="h-leitura">Sua leitura</h2>
          @if (leitura(); as r) {
            <p class="status">{{ nomeSt(r.statusLivro) }}
              @if (r.nota !== null) { · nota <span class="nota-marca">{{ r.nota }}</span> }
              @if (r.linguaLida) { · em {{ nomeLg(r.linguaLida) }} }
            </p>
            @if (r.dataInicioLeitura || r.dataTerminoLeitura) {
              <p class="datas">
                @if (r.dataInicioLeitura) { Começou em {{ r.dataInicioLeitura | date: 'dd/MM/yyyy' : 'UTC' }}. }
                @if (r.dataTerminoLeitura) { Terminou em {{ r.dataTerminoLeitura | date: 'dd/MM/yyyy' : 'UTC' }}. }
              </p>
            }
            @if (r.resenha) { <blockquote>{{ r.resenha }}</blockquote> }
            <button type="button" class="btn" (click)="dialogLeitura.set(true)">Editar minha leitura</button>
          } @else {
            <p class="datas">Esse livro ainda não está na sua estante.</p>
            <button type="button" class="btn btn-principal" (click)="dialogLeitura.set(true)">Adicionar à estante</button>
          }
        </section>
      }
    </div>

    <app-leitura-dialog
      [aberto]="dialogLeitura()"
      [leitura]="leitura()"
      [livroInicial]="livro()"
      [titulosNaEstante]="titulosOcupados()"
      (fechar)="dialogLeitura.set(false)"
      (salvo)="leitura.set($event); dialogLeitura.set(false)"
      (removido)="leitura.set(null); dialogLeitura.set(false)" />

    <app-livro-dialog [aberto]="editarLivro()" [livro]="livro()" (fechar)="editarLivro.set(false)"
                      (salvo)="livro.set($event); editarLivro.set(false)" />
  `,
  styles: `
    .voltar { margin-bottom: 1.5rem; }
    .topo { display: grid; grid-template-columns: auto minmax(0, 1fr); gap: 2rem; align-items: start; }
    .prateleira { display: flex; align-items: flex-end; border-bottom: 12px solid var(--tinta); padding: 0 1rem; min-width: 6.5rem; justify-content: center; }
    .autor { font-family: var(--fonte-titulo); font-size: 1.25rem; color: var(--tinta-suave); margin: .35rem 0 1.25rem; }
    dl { display: grid; grid-template-columns: repeat(auto-fill, minmax(10rem, 1fr)); gap: 1rem 1.5rem; margin: 0 0 1.25rem; }
    dt { font-size: .85rem; color: var(--tinta-suave); }
    dd { margin: 0; font-weight: 600; }
    .leitura { margin-top: 3rem; padding-top: 1.5rem; border-top: 1.5px solid var(--tinta); display: flex; flex-direction: column; align-items: flex-start; gap: .75rem; max-width: 46rem; }
    .status { font-size: 1.1rem; font-weight: 500; }
    .datas { color: var(--tinta-suave); }
    blockquote {
      margin: .25rem 0; padding-left: 1.1rem; border-left: 4px solid var(--marca);
      font-family: var(--fonte-titulo); font-size: 1.05rem; line-height: 1.7; white-space: pre-line; max-width: 62ch;
    }
    @media (max-width: 640px) { .topo { grid-template-columns: 1fr; } .prateleira { justify-self: start; } }
  `,
})
export class LivroDetalhe {
  private readonly livrosApi = inject(LivrosApi);
  private readonly leiturasApi = inject(LeiturasApi);
  private readonly auth = inject(AuthService);

  /** Vem da rota /catalogo/:id (withComponentInputBinding). */
  readonly id = input.required<string>();

  protected readonly nomeCat = nomeCategoria;
  protected readonly nomeFmt = nomeFormato;
  protected readonly nomeSt = nomeStatus;
  protected readonly nomeLg = nomeLingua;

  protected readonly livro = signal<Livro | null>(null);
  protected readonly leitura = signal<LivroCliente | null>(null);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly dialogLeitura = signal(false);
  protected readonly editarLivro = signal(false);
  private readonly todasLeituras = signal<LivroCliente[]>([]);

  /** Outros títulos já na estante (o livro atual pode aparecer no seletor). */
  protected readonly titulosOcupados = computed(() => this.todasLeituras().map((l) => l.livro.titulo));

  constructor() {
    effect(() => {
      const id = Number(this.id());
      untracked(() => this.carregar(id));
    });
  }

  private carregar(id: number) {
    const usuario = this.auth.usuario();
    this.carregando.set(true);
    this.erro.set(null);
    forkJoin({
      livro: this.livrosApi.buscar(id),
      leituras: this.leiturasApi.listarDoUsuario(usuario?.id ?? 0),
    }).subscribe({
      next: ({ livro, leituras }) => {
        this.livro.set(livro);
        this.todasLeituras.set(leituras);
        this.leitura.set(leituras.find((l) => l.livro.id === livro.id) ?? null);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e, 'Esse livro não existe ou foi removido.'));
        this.carregando.set(false);
      },
    });
  }
}
