import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditorasApi } from '../../core/api';
import { mensagemDeErro } from '../../core/erros';
import { Editora } from '../../core/models';
import { PAISES } from '../../core/paises';
import { ToastService } from '../../core/toast.service';
import { Modal } from '../../shared/modal';

const nomePais = (valor: string | null) => PAISES.find((p) => p.valor === valor)?.nome ?? valor ?? 'Não informado';

@Component({
  selector: 'app-editoras',
  imports: [FormsModule, ReactiveFormsModule, Modal],
  template: `
    <div class="pagina">
      <div class="pagina-topo">
        <div>
          <h1>Editoras</h1>
          <p>Cadastre a editora antes de cadastrar os livros dela.</p>
        </div>
        <button type="button" class="btn btn-principal" (click)="abrirForm(null)">Nova editora</button>
      </div>

      @if (carregando()) {
        <p class="carregando">Carregando editoras...</p>
      } @else if (erro()) {
        <div class="vazio" role="alert">
          <h3>Não consegui carregar as editoras</h3>
          <p>{{ erro() }}</p>
          <button type="button" class="btn" (click)="carregar()">Tentar de novo</button>
        </div>
      } @else if (editoras().length === 0) {
        <div class="vazio">
          <h3>Nenhuma editora cadastrada</h3>
          <button type="button" class="btn btn-principal" (click)="abrirForm(null)">Cadastrar editora</button>
        </div>
      } @else {
        <div class="campo busca">
          <label for="busca" class="so-leitor">Buscar editora</label>
          <input id="busca" class="entrada" type="search" placeholder="Buscar editora" [ngModel]="busca()" (ngModelChange)="busca.set($event)" />
        </div>
        <ul class="linhas">
          @for (e of filtradas(); track e.id) {
            <li>
              <span class="nome">{{ e.nome }}</span>
              <span class="suave">{{ pais(e.paisOrigem) }}</span>
              <button type="button" class="btn btn-pequeno" (click)="abrirForm(e)" [attr.aria-label]="'Editar ' + e.nome">Editar</button>
            </li>
          } @empty {
            <li class="suave">Nenhuma editora encontrada.</li>
          }
        </ul>
      }
    </div>

    <app-modal [aberto]="aberto()" [titulo]="editando() ? 'Editar editora' : 'Nova editora'" (fechar)="aberto.set(false)">
      <form [formGroup]="form" (ngSubmit)="salvar()" novalidate class="grade-form">
        @if (erroForm()) { <p class="aviso-form inteira" role="alert">{{ erroForm() }}</p> }
        <div class="campo">
          <label for="ed-nome">Nome</label>
          <input id="ed-nome" class="entrada" formControlName="nome" [class.invalido]="inv('nome')" />
          @if (inv('nome')) { <span class="erro">Informe o nome.</span> }
        </div>
        <div class="campo">
          <label for="ed-pais">País de origem</label>
          <select id="ed-pais" class="entrada" formControlName="paisOrigem" [class.invalido]="inv('paisOrigem')">
            <option value="" disabled>Selecione</option>
            @for (p of paises; track p.valor) { <option [value]="p.valor">{{ p.nome }}</option> }
          </select>
          @if (inv('paisOrigem')) { <span class="erro">Escolha um país.</span> }
        </div>
        <div class="acoes inteira">
          <button type="button" class="btn btn-leve" (click)="aberto.set(false)">Cancelar</button>
          <button type="submit" class="btn btn-principal" [disabled]="enviando()">
            {{ enviando() ? 'Salvando...' : editando() ? 'Salvar alterações' : 'Cadastrar editora' }}
          </button>
        </div>
      </form>
    </app-modal>
  `,
  styles: `
    .busca { max-width: 22rem; margin-bottom: 1.25rem; }
    .linhas > li { grid-template-columns: minmax(0, 2fr) minmax(0, 1.4fr) auto; }
    .nome { font-family: var(--fonte-titulo); font-weight: 600; font-size: 1.05rem; }
    .suave { color: var(--tinta-suave); }
    .acoes { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
  `,
})
export class Editoras {
  private readonly api = inject(EditorasApi);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);

  protected readonly paises = PAISES;
  protected readonly pais = nomePais;

  protected readonly editoras = signal<Editora[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly busca = signal('');
  protected readonly filtradas = computed(() => {
    const t = this.busca().trim().toLowerCase();
    return this.editoras()
      .filter((e) => !t || e.nome.toLowerCase().includes(t))
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  });

  protected readonly aberto = signal(false);
  protected readonly editando = signal<Editora | null>(null);
  protected readonly enviando = signal(false);
  protected readonly erroForm = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.pattern(/\S/)]],
    paisOrigem: ['', Validators.required],
  });

  constructor() {
    this.carregar();
  }

  protected carregar() {
    this.carregando.set(true);
    this.erro.set(null);
    this.api.listar().subscribe({
      next: (l) => {
        this.editoras.set(l);
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

  protected abrirForm(e: Editora | null) {
    this.editando.set(e);
    this.erroForm.set(null);
    this.enviando.set(false);
    this.form.reset({ nome: e?.nome ?? '', paisOrigem: e?.paisOrigem ?? '' });
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
        })
      : this.api.criar({ nome, paisOrigem: v.paisOrigem });

    req.subscribe({
      next: (editora) => {
        this.editoras.update((l) => (atual ? l.map((x) => (x.id === editora.id ? editora : x)) : [...l, editora]));
        this.toast.ok(atual ? 'Editora atualizada.' : 'Editora cadastrada.');
        this.aberto.set(false);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erroForm.set(mensagemDeErro(e, 'Não consegui salvar. Já existe uma editora com esse nome?'));
      },
    });
  }
}
