import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LivrosApi } from '../../core/api';
import { mensagemDeErro } from '../../core/erros';
import { Categoria, Livro } from '../../core/models';
import { CATEGORIAS, nomeCategoria, nomeFormato } from '../../core/rotulos';
import { Router } from '@angular/router';
import { LivroDialog } from './livro-dialog';

@Component({
  selector: 'app-catalogo',
  imports: [FormsModule, RouterLink, LivroDialog],
  template: `
    <div class="pagina">
      <div class="pagina-topo">
        <div>
          <h1>Catálogo</h1>
          <p>Todos os livros cadastrados. Abra um livro para adicionar à sua estante.</p>
        </div>
        <button type="button" class="btn btn-principal" (click)="dialogAberto.set(true)">Novo livro</button>
      </div>

      <div class="filtros">
        <div class="campo busca">
          <label for="busca" class="so-leitor">Buscar por título, autor ou editora</label>
          <input id="busca" class="entrada" type="search" placeholder="Buscar por título, autor ou editora"
                 [ngModel]="busca()" (ngModelChange)="busca.set($event)" />
        </div>
        <div class="chips" role="group" aria-label="Filtrar por categoria">
          <button type="button" class="chip" [attr.aria-pressed]="categoria() === 'TODAS'" (click)="categoria.set('TODAS')">Todas</button>
          @for (c of categorias; track c.valor) {
            <button type="button" class="chip" [attr.aria-pressed]="categoria() === c.valor" (click)="categoria.set(c.valor)">{{ c.nome }}</button>
          }
        </div>
      </div>

      @if (carregando()) {
        <p class="carregando">Carregando catálogo...</p>
      } @else if (erro()) {
        <div class="vazio" role="alert">
          <h3>Não consegui carregar o catálogo</h3>
          <p>{{ erro() }}</p>
          <button type="button" class="btn" (click)="carregar()">Tentar de novo</button>
        </div>
      } @else if (livros().length === 0) {
        <div class="vazio">
          <h3>O catálogo está vazio</h3>
          <p>Cadastre o primeiro livro. Você vai precisar de um autor e uma editora já cadastrados.</p>
          <button type="button" class="btn btn-principal" (click)="dialogAberto.set(true)">Cadastrar livro</button>
        </div>
      } @else {
        <ul class="linhas">
          @for (l of filtrados(); track l.id) {
            <li>
              <a class="titulo" [routerLink]="['/catalogo', l.id]">{{ l.titulo }}</a>
              <span class="autor">{{ l.autor.nome }}<span class="suave"> · {{ l.editora.nome }}</span></span>
              <span class="meta">{{ nomeCat(l.categoria) }} · {{ nomeFmt(l.formato) }}</span>
              <span class="paginas">{{ l.numeroPaginas }} p.</span>
            </li>
          } @empty {
            <li class="sem-resultado">Nenhum livro encontrado para esse filtro.</li>
          }
        </ul>
      }
    </div>

    <app-livro-dialog [aberto]="dialogAberto()" (fechar)="dialogAberto.set(false)" (salvo)="aoCriar($event)" />
  `,
  styles: `
    .filtros { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 1.5rem; }
    .busca { max-width: 26rem; }
    .linhas > li { grid-template-columns: minmax(0, 2.2fr) minmax(0, 2fr) minmax(0, 1.4fr) 4.5rem; }
    .titulo { font-family: var(--fonte-titulo); font-weight: 600; font-size: 1.1rem; }
    .suave, .meta { color: var(--tinta-suave); }
    .paginas { text-align: right; font-variant-numeric: tabular-nums; color: var(--tinta-suave); }
    .sem-resultado { display: block !important; color: var(--tinta-suave); }
    @media (max-width: 760px) {
      .linhas > li { grid-template-columns: 1fr auto; }
      .autor, .meta { grid-column: 1; }
      .paginas { grid-column: 2; grid-row: 1; }
    }
  `,
})
export class Catalogo {
  private readonly api = inject(LivrosApi);
  private readonly router = inject(Router);

  protected readonly categorias = CATEGORIAS;
  protected readonly nomeCat = nomeCategoria;
  protected readonly nomeFmt = nomeFormato;

  protected readonly livros = signal<Livro[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly busca = signal('');
  protected readonly categoria = signal<Categoria | 'TODAS'>('TODAS');
  protected readonly dialogAberto = signal(false);

  protected readonly filtrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    const cat = this.categoria();
    return this.livros()
      .filter((l) => cat === 'TODAS' || l.categoria === cat)
      .filter(
        (l) =>
          !termo ||
          l.titulo.toLowerCase().includes(termo) ||
          l.autor.nome.toLowerCase().includes(termo) ||
          l.editora.nome.toLowerCase().includes(termo),
      )
      .sort((a, b) => a.titulo.localeCompare(b.titulo, 'pt-BR'));
  });

  constructor() {
    this.carregar();
  }

  protected carregar() {
    this.carregando.set(true);
    this.erro.set(null);
    this.api.listar().subscribe({
      next: (lista) => {
        this.livros.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  protected aoCriar(livro: Livro) {
    this.dialogAberto.set(false);
    this.router.navigate(['/catalogo', livro.id]);
  }
}
