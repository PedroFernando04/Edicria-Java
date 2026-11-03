import { Component, effect, inject, input, output, signal, untracked } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AutoresApi, EditorasApi, LivrosApi } from '../../core/api';
import { mensagemDeErro } from '../../core/erros';
import { AtualizacaoLivro, Autor, Categoria, Editora, Formato, Livro } from '../../core/models';
import { CATEGORIAS, FORMATOS } from '../../core/rotulos';
import { ToastService } from '../../core/toast.service';
import { Modal } from '../../shared/modal';

/** Cadastra um livro novo no catálogo ou edita um existente. */
@Component({
  selector: 'app-livro-dialog',
  imports: [Modal, ReactiveFormsModule, RouterLink],
  template: `
    <app-modal [aberto]="aberto()" [titulo]="livro() ? 'Editar livro' : 'Novo livro no catálogo'" (fechar)="fechar.emit()">
      <form [formGroup]="form" (ngSubmit)="salvar()" novalidate class="grade-form">
        @if (erro()) {
          <p class="aviso-form inteira" role="alert">{{ erro() }}</p>
        }

        <div class="campo inteira">
          <label for="lv-titulo">Título</label>
          <input id="lv-titulo" class="entrada" formControlName="titulo" [class.invalido]="invalido('titulo')" />
          @if (invalido('titulo')) { <span class="erro">Informe o título.</span> }
        </div>

        <div class="campo">
          <label for="lv-autor">Autor</label>
          <select id="lv-autor" class="entrada" formControlName="autor" [class.invalido]="invalido('autor')">
            <option value="" disabled>Escolha um autor</option>
            @for (a of autores(); track a.id) { <option [value]="a.nome">{{ a.nome }}</option> }
          </select>
          @if (!carregando() && autores().length === 0) {
            <span class="dica">Nenhum autor cadastrado. <a routerLink="/autores">Cadastre um autor</a> primeiro.</span>
          }
          @if (invalido('autor')) { <span class="erro">Escolha o autor.</span> }
        </div>

        <div class="campo">
          <label for="lv-editora">Editora</label>
          <select id="lv-editora" class="entrada" formControlName="editora" [class.invalido]="invalido('editora')">
            <option value="" disabled>Escolha uma editora</option>
            @for (e of editoras(); track e.id) { <option [value]="e.nome">{{ e.nome }}</option> }
          </select>
          @if (!carregando() && editoras().length === 0) {
            <span class="dica">Nenhuma editora cadastrada. <a routerLink="/editoras">Cadastre uma editora</a> primeiro.</span>
          }
          @if (invalido('editora')) { <span class="erro">Escolha a editora.</span> }
        </div>

        <div class="campo">
          <label for="lv-categoria">Categoria</label>
          <select id="lv-categoria" class="entrada" formControlName="categoria">
            @for (c of categorias; track c.valor) { <option [value]="c.valor">{{ c.nome }}</option> }
          </select>
        </div>

        <div class="campo">
          <label for="lv-formato">Formato</label>
          <select id="lv-formato" class="entrada" formControlName="formato">
            @for (f of formatos; track f.valor) { <option [value]="f.valor">{{ f.nome }}</option> }
          </select>
        </div>

        <div class="campo">
          <label for="lv-lanc">Data de lançamento</label>
          <input id="lv-lanc" class="entrada" type="date" formControlName="dataLancamento" [class.invalido]="invalido('dataLancamento')" />
          @if (invalido('dataLancamento')) { <span class="erro">Informe a data.</span> }
        </div>

        <div class="campo">
          <label for="lv-pag">Número de páginas</label>
          <input id="lv-pag" class="entrada" type="number" min="1" step="1" formControlName="numeroPaginas" [class.invalido]="invalido('numeroPaginas')" />
          @if (livro()) { <span class="dica">As páginas não podem ser alteradas depois do cadastro.</span> }
          @if (invalido('numeroPaginas')) { <span class="erro">O livro precisa ter ao menos 1 página.</span> }
        </div>

        <div class="acoes inteira">
          <button type="button" class="btn btn-leve" (click)="fechar.emit()">Cancelar</button>
          <button type="submit" class="btn btn-principal" [disabled]="enviando() || carregando()">
            {{ enviando() ? 'Salvando...' : livro() ? 'Salvar alterações' : 'Cadastrar livro' }}
          </button>
        </div>
      </form>
    </app-modal>
  `,
  styles: `.acoes { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }`,
})
export class LivroDialog {
  private readonly fb = inject(FormBuilder);
  private readonly livrosApi = inject(LivrosApi);
  private readonly autoresApi = inject(AutoresApi);
  private readonly editorasApi = inject(EditorasApi);
  private readonly toast = inject(ToastService);

  readonly aberto = input.required<boolean>();
  /** Livro a editar; null para cadastrar um novo. */
  readonly livro = input<Livro | null>(null);
  readonly fechar = output<void>();
  readonly salvo = output<Livro>();

  protected readonly categorias = CATEGORIAS;
  protected readonly formatos = FORMATOS;
  protected readonly autores = signal<Autor[]>([]);
  protected readonly editoras = signal<Editora[]>([]);
  protected readonly carregando = signal(false);
  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.pattern(/\S/)]],
    autor: ['', Validators.required],
    editora: ['', Validators.required],
    categoria: ['LIVRO' as Categoria],
    formato: ['FISICO' as Formato],
    dataLancamento: ['', Validators.required],
    numeroPaginas: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
  });

  constructor() {
    effect(() => {
      if (!this.aberto()) return;
      const livro = this.livro();
      untracked(() => this.preparar(livro));
    });
  }

  private preparar(livro: Livro | null) {
    this.erro.set(null);
    this.enviando.set(false);
    this.form.reset({
      titulo: livro?.titulo ?? '',
      autor: livro?.autor.nome ?? '',
      editora: livro?.editora.nome ?? '',
      categoria: livro?.categoria ?? 'LIVRO',
      formato: livro?.formato ?? 'FISICO',
      dataLancamento: livro?.dataLancamento ?? '',
      numeroPaginas: livro?.numeroPaginas ?? null,
    });
    // A API não altera o número de páginas em uma edição.
    if (livro) this.form.controls.numeroPaginas.disable();
    else this.form.controls.numeroPaginas.enable();

    this.carregando.set(true);
    forkJoin({ autores: this.autoresApi.listar(), editoras: this.editorasApi.listar() }).subscribe({
      next: ({ autores, editoras }) => {
        this.autores.set([...autores].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR')));
        this.editoras.set([...editoras].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR')));
        this.carregando.set(false);
      },
      error: (e) => {
        this.carregando.set(false);
        this.erro.set(mensagemDeErro(e));
      },
    });
  }

  protected invalido(campo: keyof typeof this.form.controls) {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  protected salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const atual = this.livro();
    this.enviando.set(true);
    this.erro.set(null);

    let requisicao;
    if (atual) {
      // A API recusa o título se ele já existir — inclusive o do próprio livro —,
      // então só enviamos o que realmente mudou.
      const mudancas: AtualizacaoLivro = {};
      const titulo = v.titulo.trim();
      if (titulo !== atual.titulo) mudancas.titulo = titulo;
      if (v.autor !== atual.autor.nome) mudancas.autor = v.autor;
      if (v.editora !== atual.editora.nome) mudancas.editora = v.editora;
      if (v.dataLancamento !== atual.dataLancamento) mudancas.dataLancamento = v.dataLancamento;
      if (v.categoria !== atual.categoria) mudancas.categoria = v.categoria;
      if (v.formato !== atual.formato) mudancas.formato = v.formato;
      requisicao = this.livrosApi.atualizar(atual.id, mudancas);
    } else {
      requisicao = this.livrosApi.criar({
        titulo: v.titulo.trim(),
        autor: v.autor,
        editora: v.editora,
        dataLancamento: v.dataLancamento,
        categoria: v.categoria,
        formato: v.formato,
        numeroPaginas: v.numeroPaginas as number,
      });
    }

    requisicao.subscribe({
      next: (livro) => {
        this.toast.ok(atual ? 'Livro atualizado.' : 'Livro cadastrado no catálogo.');
        this.salvo.emit(livro);
      },
      error: (e) => {
        this.enviando.set(false);
        this.erro.set(mensagemDeErro(e, 'Não consegui salvar. Já existe um livro com esse título?'));
      },
    });
  }
}
