import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AutoresApi } from '../../core/api';
import { mensagemDeErro } from '../../core/erros';
import { Autor, Genero } from '../../core/models';
import { PAISES } from '../../core/paises';
import { GENEROS, nomeGenero } from '../../core/rotulos';
import { ToastService } from '../../core/toast.service';
import { Modal } from '../../shared/modal';

const nomePais = (valor: string | null) => PAISES.find((p) => p.valor === valor)?.nome ?? valor ?? '';

@Component({
  selector: 'app-autores',
  imports: [FormsModule, ReactiveFormsModule, Modal],
  template: `
    <div class="pagina">
      <div class="pagina-topo">
        <div>
          <h1>Autores</h1>
          <p>Cadastre o autor antes de cadastrar os livros dele.</p>
        </div>
        <button type="button" class="btn btn-principal" (click)="novo()">Novo autor</button>
      </div>

      @if (carregando()) {
        <p class="carregando">Carregando autores...</p>
      } @else if (erro()) {
        <div class="vazio" role="alert">
          <h3>Não consegui carregar os autores</h3>
          <p>{{ erro() }}</p>
          <button type="button" class="btn" (click)="carregar()">Tentar de novo</button>
        </div>
      } @else if (autores().length === 0) {
        <div class="vazio">
          <h3>Nenhum autor cadastrado</h3>
          <button type="button" class="btn btn-principal" (click)="novo()">Cadastrar autor</button>
        </div>
      } @else {
        <div class="campo busca">
          <label for="busca" class="so-leitor">Buscar autor</label>
          <input id="busca" class="entrada" type="search" placeholder="Buscar autor" [ngModel]="busca()" (ngModelChange)="busca.set($event)" />
        </div>
        <ul class="linhas">
          @for (a of filtrados(); track a.id) {
            <li>
              <span class="nome">{{ a.nome }}</span>
              <span class="suave">{{ pais(a.paisOrigem) }}</span>
              <span class="suave">{{ genero(a.genero) }}</span>
              <button type="button" class="btn btn-pequeno" (click)="editar(a)" [attr.aria-label]="'Editar ' + a.nome">Editar</button>
            </li>
          } @empty {
            <li class="suave">Nenhum autor encontrado.</li>
          }
        </ul>
      }
    </div>

    <app-modal [aberto]="aberto()" [titulo]="editando() ? 'Editar autor' : 'Novo autor'" (fechar)="aberto.set(false)">
      <form [formGroup]="form" (ngSubmit)="salvar()" novalidate class="grade-form">
        @if (erroForm()) { <p class="aviso-form inteira" role="alert">{{ erroForm() }}</p> }
        <div class="campo inteira">
          <label for="au-nome">Nome</label>
          <input id="au-nome" class="entrada" formControlName="nome" [class.invalido]="inv('nome')" />
          @if (inv('nome')) { <span class="erro">Informe o nome.</span> }
        </div>
        <div class="campo">
          <label for="au-pais">País de origem</label>
          <select id="au-pais" class="entrada" formControlName="paisOrigem" [class.invalido]="inv('paisOrigem')">
            <option value="" disabled>Selecione</option>
            @for (p of paises; track p.valor) { <option [value]="p.valor">{{ p.nome }}</option> }
          </select>
          @if (inv('paisOrigem')) { <span class="erro">Escolha um país.</span> }
        </div>
        <div class="campo">
          <label for="au-genero">Gênero</label>
          <select id="au-genero" class="entrada" formControlName="genero" [class.invalido]="inv('genero')">
            <option value="" disabled>Selecione</option>
            @for (g of generos; track g.valor) { <option [value]="g.valor">{{ g.nome }}</option> }
          </select>
          @if (inv('genero')) { <span class="erro">Escolha uma opção.</span> }
        </div>
        <div class="acoes inteira">
          <button type="button" class="btn btn-leve" (click)="aberto.set(false)">Cancelar</button>
          <button type="submit" class="btn btn-principal" [disabled]="enviando()">
            {{ enviando() ? 'Salvando...' : editando() ? 'Salvar alterações' : 'Cadastrar autor' }}
          </button>
        </div>
      </form>
    </app-modal>
  `,
  styles: `
    .busca { max-width: 22rem; margin-bottom: 1.25rem; }
    .linhas > li { grid-template-columns: minmax(0, 2fr) minmax(0, 1.4fr) 7rem auto; }
    .nome { font-family: var(--fonte-titulo); font-weight: 600; font-size: 1.05rem; }
    .suave { color: var(--tinta-suave); }
    .acoes { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    @media (max-width: 700px) { .linhas > li { grid-template-columns: 1fr auto; } }
  `,
})
export class Autores {
  private readonly api = inject(AutoresApi);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);

  protected readonly paises = PAISES;
  protected readonly generos = GENEROS;
  protected readonly pais = nomePais;
  protected readonly genero = nomeGenero;

  protected readonly autores = signal<Autor[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly busca = signal('');
  protected readonly filtrados = computed(() => {
    const t = this.busca().trim().toLowerCase();
    return this.autores()
      .filter((a) => !t || a.nome.toLowerCase().includes(t))
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  });

  protected readonly aberto = signal(false);
  protected readonly editando = signal<Autor | null>(null);
  protected readonly enviando = signal(false);
  protected readonly erroForm = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.pattern(/\S/)]],
    paisOrigem: ['', Validators.required],
    genero: ['' as Genero | '', Validators.required],
  });

  constructor() {
    this.carregar();
  }

  protected carregar() {
    this.carregando.set(true);
    this.erro.set(null);
    this.api.listar().subscribe({
      next: (l) => {
        this.autores.set(l);
        this.carregando.set(false);
      },
      error: (e) => {
        this.erro.set(mensagemDeErro(e));
        this.carregando.set(false);
      },
    });
  }

  protected inv(campo: keyof typeof this.form.controls) {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  protected novo() {
    this.abrirForm(null);
  }

  protected editar(a: Autor) {
    this.abrirForm(a);
  }

  private abrirForm(a: Autor | null) {
    this.editando.set(a);
    this.erroForm.set(null);
    this.enviando.set(false);
    this.form.reset({ nome: a?.nome ?? '', paisOrigem: a?.paisOrigem ?? '', genero: a?.genero ?? '' });
    this.aberto.set(true);
  }

  protected salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const nome = v.nome.trim();
    const atual = this.editando();
    this.enviando.set(true);
    this.erroForm.set(null);

    const req = atual
      ? this.api.atualizar(atual.id, {
          ...(nome !== atual.nome && { nome }),
          ...(v.paisOrigem !== atual.paisOrigem && { paisOrigem: v.paisOrigem }),
          ...(v.genero !== atual.genero && { genero: v.genero as Genero }),
        })
      : this.api.criar({ nome, paisOrigem: v.paisOrigem, genero: v.genero as Genero });

    req.subscribe({
      next: (autor) => {
        this.autores.update((l) => (atual ? l.map((x) => (x.id === autor.id ? autor : x)) : [...l, autor]));
        this.toast.ok(atual ? 'Autor atualizado.' : 'Autor cadastrado.');
        this.aberto.set(false);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erroForm.set(mensagemDeErro(e, 'Não consegui salvar. Já existe um autor com esse nome?'));
      },
    });
  }
}
