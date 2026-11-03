import { Component, computed, inject, signal } from '@angular/core';
import { LeiturasApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { mensagemDeErro } from '../../core/erros';
import { LivroCliente, StatusLeitura } from '../../core/models';
import { CATEGORIAS, STATUS, nomeCategoria, nomeStatus } from '../../core/rotulos';
import { Lombada } from '../../shared/lombada';
import { LeituraDialog } from './leitura-dialog';

type Filtro = StatusLeitura | 'TODOS';

@Component({
  selector: 'app-estante',
  imports: [Lombada, LeituraDialog],
  template: `
    <div class="pagina">
      <div class="pagina-topo">
        <div>
          <h1>Minha estante</h1>
          <p class="resumo">{{ resumo() }}</p>
        </div>
        <button type="button" class="btn btn-principal" (click)="adicionar()">Adicionar livro</button>
      </div>

      @if (carregando()) {
        <p class="carregando">Carregando sua estante...</p>
      } @else if (erro()) {
        <div class="vazio" role="alert">
          <h3>Não consegui carregar sua estante</h3>
          <p>{{ erro() }}</p>
          <button type="button" class="btn" (click)="carregar()">Tentar de novo</button>
        </div>
      } @else if (leituras().length === 0) {
        <div class="vazio">
          <h3>Sua estante está vazia</h3>
          <p>Escolha um livro do catálogo e marque como lido, lendo ou quero ler.</p>
          <button type="button" class="btn btn-principal" (click)="adicionar()">Adicionar o primeiro livro</button>
        </div>
      } @else {
        <div class="barra">
          <div class="chips" role="group" aria-label="Filtrar por situação">
            <button type="button" class="chip" [attr.aria-pressed]="filtro() === 'TODOS'" (click)="filtro.set('TODOS')">
              Todos <span class="contagem">{{ leituras().length }}</span>
            </button>
            @for (s of status; track s.valor) {
              <button type="button" class="chip" [attr.aria-pressed]="filtro() === s.valor" (click)="filtro.set(s.valor)">
                {{ s.nome }} <span class="contagem">{{ contagem()[s.valor] }}</span>
              </button>
            }
          </div>

          <ul class="legenda" aria-label="Cores das lombadas">
            @for (c of categorias; track c.valor) {
              <li><span class="amostra" [style.background]="'var(--lombada-' + c.valor.toLowerCase() + ')'"></span>{{ c.nome }}</li>
            }
          </ul>
        </div>

        @for (grupo of grupos(); track grupo.status) {
          <section class="grupo" [attr.aria-labelledby]="'g-' + grupo.status">
            <h2 [id]="'g-' + grupo.status">{{ grupo.nome }} <span class="n">{{ grupo.itens.length }}</span></h2>
            <div class="prateleira">
              @for (item of grupo.itens; track item.id) {
                <button type="button" class="cel" (click)="abrir(item)"
                        [attr.aria-label]="rotuloAcessivel(item)">
                  <app-lombada [titulo]="item.livro.titulo" [autor]="item.livro.autor.nome"
                               [categoria]="item.livro.categoria" [paginas]="item.livro.numeroPaginas" [nota]="item.nota" />
                </button>
              }
            </div>
          </section>
        } @empty {
          <div class="vazio"><p>Nenhum livro com essa situação.</p></div>
        }
      }
    </div>

    <app-leitura-dialog
      [aberto]="dialogAberto()"
      [leitura]="selecionada()"
      [titulosNaEstante]="titulos()"
      (fechar)="dialogAberto.set(false)"
      (salvo)="aoSalvar($event)"
      (removido)="aoRemover($event)" />
  `,
  styles: `
    .resumo { font-family: var(--fonte-titulo); font-size: 1.1rem; color: var(--tinta-suave); }
    .barra { display: flex; flex-wrap: wrap; justify-content: space-between; gap: 1rem 2rem; margin-bottom: 2rem; align-items: center; }
    .legenda { display: flex; flex-wrap: wrap; gap: .25rem 1rem; list-style: none; margin: 0; padding: 0; font-size: .85rem; color: var(--tinta-suave); }
    .legenda li { display: flex; align-items: center; gap: .4rem; }
    .amostra { width: .7rem; height: 1.1rem; border-radius: 2px 2px 0 0; display: inline-block; }

    .grupo { margin-bottom: 2.5rem; }
    .grupo h2 { margin-bottom: .75rem; }
    .grupo .n { font-family: var(--fonte-texto); font-size: .95rem; font-weight: 500; color: var(--tinta-suave); margin-left: .35rem; }

    /* Prateleira: cada célula tem a mesma altura e a "tábua" é a borda inferior dela. */
    .prateleira { display: flex; flex-wrap: wrap; }
    .prateleira::after { content: ''; flex: 1 1 0; min-width: 0; height: 292px; border-bottom: 12px solid var(--tinta); }
    .cel {
      display: flex; align-items: flex-end; height: 292px; padding: 0 2px;
      border: 0; border-bottom: 12px solid var(--tinta); background: none; cursor: pointer; font: inherit;
    }
    .cel app-lombada { transition: transform .15s ease-out; }
    .cel:hover app-lombada, .cel:focus-visible app-lombada { transform: translateY(-14px); }
    .cel:focus-visible { outline-offset: -2px; }
  `,
})
export class Estante {
  private readonly api = inject(LeiturasApi);
  private readonly auth = inject(AuthService);

  protected readonly status = STATUS;
  protected readonly categorias = CATEGORIAS;

  protected readonly leituras = signal<LivroCliente[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly filtro = signal<Filtro>('TODOS');

  protected readonly dialogAberto = signal(false);
  protected readonly selecionada = signal<LivroCliente | null>(null);

  protected readonly titulos = computed(() => this.leituras().map((l) => l.livro.titulo));

  protected readonly contagem = computed(() => {
    const total: Record<StatusLeitura, number> = { LENDO: 0, LIDO: 0, LISTA_DE_DESEJOS: 0, DESISTI: 0 };
    for (const l of this.leituras()) total[l.statusLivro]++;
    return total;
  });

  /** Uma prateleira por situação, na ordem em que aparecem nos filtros. */
  protected readonly grupos = computed(() => {
    const filtro = this.filtro();
    return STATUS.filter((s) => filtro === 'TODOS' || filtro === s.valor)
      .map((s) => ({
        status: s.valor,
        nome: s.nome,
        itens: this.leituras()
          .filter((l) => l.statusLivro === s.valor)
          .sort((a, b) => a.livro.titulo.localeCompare(b.livro.titulo, 'pt-BR')),
      }))
      .filter((g) => g.itens.length > 0);
  });

  protected readonly resumo = computed(() => {
    const lidos = this.leituras().filter((l) => l.statusLivro === 'LIDO');
    if (this.leituras().length === 0) return 'Ainda sem leituras registradas.';
    if (lidos.length === 0) return `${this.leituras().length} ${this.leituras().length === 1 ? 'livro' : 'livros'} na estante, nenhum lido ainda.`;
    const paginas = lidos.reduce((soma, l) => soma + l.livro.numeroPaginas, 0);
    const notas = lidos.map((l) => l.nota).filter((n): n is number => n !== null);
    const media = notas.length ? notas.reduce((a, b) => a + b, 0) / notas.length : null;
    const fmt = (n: number) => n.toLocaleString('pt-BR', { maximumFractionDigits: 1 });
    return (
      `Você leu ${lidos.length} ${lidos.length === 1 ? 'livro' : 'livros'} (${paginas.toLocaleString('pt-BR')} páginas)` +
      (media !== null ? `, com nota média ${fmt(media)}.` : '.')
    );
  });

  constructor() {
    this.carregar();
  }

  protected carregar() {
    const usuario = this.auth.usuario();
    if (!usuario) return;
    this.carregando.set(true);
    this.erro.set(null);
    this.api.listarDoUsuario(usuario.id).subscribe({
      next: (lista) => {
        this.leituras.set(lista);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  protected rotuloAcessivel(l: LivroCliente) {
    const nota = l.nota !== null ? `, nota ${l.nota}` : '';
    return `${l.livro.titulo}, de ${l.livro.autor.nome}. ${nomeCategoria(l.livro.categoria)}, ${nomeStatus(l.statusLivro)}${nota}. Abrir detalhes`;
  }

  protected adicionar() {
    this.selecionada.set(null);
    this.dialogAberto.set(true);
  }

  protected abrir(l: LivroCliente) {
    this.selecionada.set(l);
    this.dialogAberto.set(true);
  }

  protected aoSalvar(l: LivroCliente) {
    this.leituras.update((lista) => {
      const existe = lista.some((x) => x.id === l.id);
      return existe ? lista.map((x) => (x.id === l.id ? l : x)) : [...lista, l];
    });
    this.dialogAberto.set(false);
  }

  protected aoRemover(id: number) {
    this.leituras.update((lista) => lista.filter((x) => x.id !== id));
    this.dialogAberto.set(false);
  }
}
