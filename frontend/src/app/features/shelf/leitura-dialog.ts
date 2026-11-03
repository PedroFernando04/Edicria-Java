import { Component, computed, effect, inject, input, output, signal, untracked } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { LeiturasApi, LivrosApi } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { mensagemDeErro } from '../../core/erros';
import { Livro, LivroCliente, Lingua, StatusLeitura } from '../../core/models';
import { LINGUAS, STATUS, nomeCategoria } from '../../core/rotulos';
import { ToastService } from '../../core/toast.service';
import { Modal } from '../../shared/modal';

/** O término da leitura não pode ser antes do início. */
function terminoDepoisDoInicio(grupo: AbstractControl): ValidationErrors | null {
  const inicio = grupo.get('inicio')?.value as string;
  const fim = grupo.get('termino')?.value as string;
  return inicio && fim && fim < inicio ? { terminoAntes: true } : null;
}

/**
 * Janela usada para adicionar um livro à estante e para editar/remover uma leitura.
 *  - Modo "adicionar": `leitura` é null.
 *  - Modo "editar": `leitura` traz o registro existente.
 */
@Component({
  selector: 'app-leitura-dialog',
  imports: [Modal, ReactiveFormsModule],
  template: `
    <app-modal [aberto]="aberto()" [titulo]="leitura() ? 'Sua leitura' : 'Adicionar à estante'" (fechar)="fechar.emit()">
      <form [formGroup]="form" (ngSubmit)="salvar()" novalidate class="grade-form">
        @if (erro()) {
          <p class="aviso-form inteira" role="alert">{{ erro() }}</p>
        }

        @if (leitura(); as l) {
          <div class="inteira livro-info">
            <p class="livro-titulo">{{ l.livro.titulo }}</p>
            <p>{{ l.livro.autor.nome }} · {{ nomeCat(l.livro.categoria) }} · {{ l.livro.numeroPaginas }} páginas</p>
          </div>
        } @else {
          <div class="campo inteira">
            <label for="ld-livro">Livro</label>
            <select id="ld-livro" class="entrada" formControlName="livro" [class.invalido]="invalido('livro')">
              <option value="" disabled>{{ carregandoLivros() ? 'Carregando livros...' : 'Escolha um livro do catálogo' }}</option>
              @for (livro of opcoesLivros(); track livro.id) {
                <option [value]="livro.titulo">{{ livro.titulo }} — {{ livro.autor.nome }}</option>
              }
            </select>
            @if (!carregandoLivros() && opcoesLivros().length === 0) {
              <span class="dica">Todos os livros do catálogo já estão na sua estante. Cadastre um novo no catálogo.</span>
            }
            @if (invalido('livro')) { <span class="erro">Escolha um livro.</span> }
          </div>
        }

        <div class="campo">
          <label for="ld-status">Situação</label>
          <select id="ld-status" class="entrada" formControlName="status">
            @for (s of status; track s.valor) { <option [value]="s.valor">{{ s.nome }}</option> }
          </select>
        </div>

        <div class="campo">
          <label for="ld-lingua">Idioma da leitura</label>
          <select id="ld-lingua" class="entrada" formControlName="lingua">
            <option value="">Não informar</option>
            @for (l of linguas; track l.valor) { <option [value]="l.valor">{{ l.nome }}</option> }
          </select>
        </div>

        @if (mostraDetalhes()) {
          <div class="campo">
            <label for="ld-inicio">Comecei em</label>
            <input id="ld-inicio" class="entrada" type="date" formControlName="inicio" [max]="hoje" />
          </div>

          <div class="campo">
            <label for="ld-termino">Terminei em</label>
            <input id="ld-termino" class="entrada" type="date" formControlName="termino" [max]="hoje"
                   [class.invalido]="form.hasError('terminoAntes')" />
            @if (form.hasError('terminoAntes')) { <span class="erro">O término não pode ser antes do início.</span> }
          </div>

          <div class="campo">
            <label for="ld-nota">Nota (0 a 10)</label>
            <input id="ld-nota" class="entrada" type="number" min="0" max="10" step="0.1" inputmode="decimal"
                   formControlName="nota" [class.invalido]="invalido('nota')" />
            @if (invalido('nota')) { <span class="erro">Use um valor entre 0 e 10.</span> }
          </div>

          <div class="campo inteira">
            <label for="ld-resenha">Resenha</label>
            <textarea id="ld-resenha" class="entrada" formControlName="resenha" maxlength="3000"
                      placeholder="O que ficou na sua cabeça depois da leitura?"></textarea>
            <span class="dica">{{ form.controls.resenha.value.length }} / 3000</span>
          </div>
        }

        <div class="acoes inteira">
          @if (leitura()) {
            @if (confirmandoRemocao()) {
              <span class="confirma">Remover da estante?</span>
              <button type="button" class="btn btn-perigo btn-pequeno" (click)="remover()" [disabled]="enviando()">Sim, remover</button>
              <button type="button" class="btn btn-pequeno btn-leve" (click)="confirmandoRemocao.set(false)">Cancelar</button>
            } @else {
              <button type="button" class="btn btn-perigo btn-pequeno" (click)="confirmandoRemocao.set(true)">Remover</button>
            }
          }
          <span class="espaco"></span>
          <button type="button" class="btn btn-leve" (click)="fechar.emit()">Cancelar</button>
          <button type="submit" class="btn btn-principal" [disabled]="enviando()">
            {{ enviando() ? 'Salvando...' : leitura() ? 'Salvar alterações' : 'Adicionar' }}
          </button>
        </div>
      </form>
    </app-modal>
  `,
  styles: `
    .livro-info { padding-bottom: .75rem; border-bottom: 1px solid var(--linha); color: var(--tinta-suave); }
    .livro-titulo { font-family: var(--fonte-titulo); font-size: 1.25rem; font-weight: 600; color: var(--tinta); }
    .acoes { display: flex; flex-wrap: wrap; align-items: center; gap: .5rem; margin-top: .5rem; }
    .espaco { flex: 1; }
    .confirma { font-weight: 600; color: var(--perigo); }
  `,
})
export class LeituraDialog {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly leituras = inject(LeiturasApi);
  private readonly livrosApi = inject(LivrosApi);
  private readonly toast = inject(ToastService);

  readonly aberto = input.required<boolean>();
  /** Registro a editar; null para adicionar um livro novo à estante. */
  readonly leitura = input<LivroCliente | null>(null);
  /** Livro já escolhido (quando o usuário vem da página do livro). */
  readonly livroInicial = input<Livro | null>(null);
  /** Títulos que já estão na estante (não podem ser adicionados de novo). */
  readonly titulosNaEstante = input<string[]>([]);

  readonly fechar = output<void>();
  readonly salvo = output<LivroCliente>();
  readonly removido = output<number>();

  protected readonly status = STATUS;
  protected readonly linguas = LINGUAS;
  protected readonly nomeCat = nomeCategoria;
  protected readonly hoje = new Date().toISOString().slice(0, 10);

  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly confirmandoRemocao = signal(false);
  protected readonly carregandoLivros = signal(false);
  private readonly livros = signal<Livro[]>([]);

  protected readonly opcoesLivros = computed(() => {
    const ocupados = new Set(this.titulosNaEstante());
    const inicial = this.livroInicial();
    return this.livros()
      .filter((l) => !ocupados.has(l.titulo) || l.titulo === inicial?.titulo)
      .sort((a, b) => a.titulo.localeCompare(b.titulo, 'pt-BR'));
  });

  protected readonly form = this.fb.nonNullable.group(
    {
      livro: ['', Validators.required],
      status: ['LENDO' as StatusLeitura],
      lingua: ['' as Lingua | ''],
      inicio: [''],
      termino: [''],
      nota: this.fb.control<number | null>(null, [Validators.min(0), Validators.max(10)]),
      resenha: [''],
    },
    { validators: terminoDepoisDoInicio },
  );

  private readonly statusAtual = signal<StatusLeitura>('LENDO');
  protected readonly mostraDetalhes = computed(() => this.statusAtual() !== 'LISTA_DE_DESEJOS');

  constructor() {
    this.form.controls.status.valueChanges.subscribe((s) => this.statusAtual.set(s));

    // Sempre que a janela abre, o formulário é preenchido do zero.
    effect(() => {
      if (!this.aberto()) return;
      const leitura = this.leitura();
      const inicial = this.livroInicial();
      untracked(() => this.preparar(leitura, inicial));
    });
  }

  private preparar(leitura: LivroCliente | null, inicial: Livro | null) {
    this.erro.set(null);
    this.enviando.set(false);
    this.confirmandoRemocao.set(false);

    this.form.reset({
      livro: leitura?.livro.titulo ?? inicial?.titulo ?? '',
      status: leitura?.statusLivro ?? 'LENDO',
      lingua: leitura?.linguaLida ?? '',
      inicio: leitura?.dataInicioLeitura ?? '',
      termino: leitura?.dataTerminoLeitura ?? '',
      nota: leitura?.nota ?? null,
      resenha: leitura?.resenha ?? '',
    });
    this.statusAtual.set(this.form.controls.status.value);

    if (!leitura && this.livros().length === 0) {
      this.carregandoLivros.set(true);
      this.livrosApi.listar().subscribe({
        next: (lista) => {
          this.livros.set(lista);
          this.carregandoLivros.set(false);
        },
        error: (e) => {
          this.carregandoLivros.set(false);
          this.erro.set(mensagemDeErro(e));
        },
      });
    }
  }

  protected invalido(campo: 'livro' | 'nota') {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  protected salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const usuario = this.auth.usuario();
    if (!usuario) return;

    const v = this.form.getRawValue();
    const detalhes = v.status !== 'LISTA_DE_DESEJOS';
    // A API ignora campos nulos ao atualizar; em "Quero ler" não enviamos datas, nota e resenha.
    const resenha = detalhes ? v.resenha.trim() || null : null;
    const comum = {
      statusLivro: v.status,
      linguaLida: v.lingua || null,
      dataInicioLeitura: detalhes ? v.inicio || null : null,
      dataTerminoLeitura: detalhes ? v.termino || null : null,
      nota: detalhes ? v.nota : null,
      resenha,
    };

    this.enviando.set(true);
    this.erro.set(null);

    const atual = this.leitura();
    const requisicao = atual
      ? this.leituras.atualizar(atual.livro.id, usuario.id, comum)
      : this.leituras.criar({ emailUsuario: usuario.email, nomeLivro: v.livro, ...comum });

    requisicao.subscribe({
      next: (resultado) => {
        this.toast.ok(atual ? 'Leitura atualizada.' : 'Livro adicionado à estante.');
        this.salvo.emit(resultado);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erro.set(mensagemDeErro(e, 'Não consegui salvar. Esse livro já pode estar na sua estante.'));
      },
    });
  }

  protected remover() {
    const atual = this.leitura();
    if (!atual) return;
    this.enviando.set(true);
    this.leituras.remover(atual.id).subscribe({
      next: () => {
        this.toast.ok('Livro removido da estante.');
        this.removido.emit(atual.id);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erro.set(mensagemDeErro(e, 'Não consegui remover esse livro.'));
      },
    });
  }
}
