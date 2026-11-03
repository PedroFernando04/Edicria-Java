import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { mensagemDeErro } from '../../core/erros';
import { Genero } from '../../core/models';
import { PAISES } from '../../core/paises';
import { GENEROS } from '../../core/rotulos';
import { ToastService } from '../../core/toast.service';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule],
  template: `
    <div class="pagina estreita">
      <div class="pagina-topo">
        <div>
          <h1>Meu perfil</h1>
          <p>Altere só o que quiser. Deixe a senha em branco para mantê-la.</p>
        </div>
      </div>

      <form [formGroup]="form" (ngSubmit)="salvar()" novalidate class="grade-form">
        @if (erro()) { <p class="aviso-form inteira" role="alert">{{ erro() }}</p> }

        <div class="campo inteira">
          <label for="pf-nome">Nome de usuário</label>
          <input id="pf-nome" class="entrada" formControlName="nome" [class.invalido]="inv('nome')" />
          @if (inv('nome')) { <span class="erro">O nome não pode ficar vazio.</span> }
        </div>

        <div class="campo inteira">
          <label for="pf-email">E-mail</label>
          <input id="pf-email" class="entrada" type="email" formControlName="email" [class.invalido]="inv('email')" />
          <span class="dica">Se você trocar o e-mail, vai precisar entrar de novo.</span>
          @if (inv('email')) { <span class="erro">Informe um e-mail válido.</span> }
        </div>

        <div class="campo inteira">
          <label for="pf-senha">Nova senha</label>
          <input id="pf-senha" class="entrada" type="password" formControlName="senha" autocomplete="new-password" />
        </div>

        <div class="campo">
          <label for="pf-genero">Gênero</label>
          <select id="pf-genero" class="entrada" formControlName="genero">
            @for (g of generos; track g.valor) { <option [value]="g.valor">{{ g.nome }}</option> }
          </select>
        </div>

        <div class="campo">
          <label for="pf-pais">País</label>
          <select id="pf-pais" class="entrada" formControlName="pais">
            @for (p of paises; track p.valor) { <option [value]="p.valor">{{ p.nome }}</option> }
          </select>
        </div>

        <div class="campo inteira">
          <label for="pf-nasc">Data de nascimento</label>
          <input id="pf-nasc" class="entrada" type="date" formControlName="dataNascimento" [max]="ontem" [class.invalido]="inv('dataNascimento')" />
          @if (inv('dataNascimento')) { <span class="erro">Informe uma data no passado.</span> }
        </div>

        <div class="acoes inteira">
          <button type="submit" class="btn btn-principal" [disabled]="enviando() || form.pristine">
            {{ enviando() ? 'Salvando...' : 'Salvar alterações' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: `
    .estreita { max-width: 38rem; }
    .acoes { display: flex; gap: .5rem; margin-top: .5rem; }
  `,
})
export class Perfil {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastService);

  protected readonly generos = GENEROS;
  protected readonly paises = PAISES;
  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly ontem = (() => {
    const d = new Date();
    d.setDate(d.getDate() - 1);
    return d.toISOString().slice(0, 10);
  })();

  private readonly u = this.auth.usuario();

  protected readonly form = this.fb.nonNullable.group({
    nome: [this.u?.nome ?? '', [Validators.required, Validators.pattern(/\S/)]],
    email: [this.u?.email ?? '', [Validators.required, Validators.email]],
    senha: [''],
    genero: [(this.u?.genero ?? 'OUTRO') as Genero],
    pais: [this.u?.pais ?? ''],
    dataNascimento: [this.u?.dataNascimento ?? '', Validators.required],
  });

  protected inv(campo: keyof typeof this.form.controls) {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  protected salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const atual = this.u;
    if (!atual) return;
    const v = this.form.getRawValue();
    const emailMudou = v.email.trim() !== atual.email;

    // A API só altera o que vem preenchido e diferente; enviamos apenas as mudanças.
    const mudancas = {
      ...(v.nome.trim() !== atual.nome && { nome: v.nome.trim() }),
      ...(emailMudou && { email: v.email.trim() }),
      ...(v.senha.trim() && { senha: v.senha }),
      ...(v.genero !== atual.genero && { genero: v.genero }),
      ...(v.pais !== atual.pais && { pais: v.pais }),
      ...(v.dataNascimento !== atual.dataNascimento && { dataNascimento: v.dataNascimento }),
    };

    this.enviando.set(true);
    this.erro.set(null);
    this.auth.atualizarPerfil(mudancas).subscribe({
      next: (novo) => {
        this.enviando.set(false);
        if (emailMudou) {
          // A sessão no servidor continua amarrada ao e-mail antigo; entrar de novo evita erros.
          this.toast.ok('E-mail alterado. Entre de novo com o novo e-mail.');
          this.auth.sair().subscribe();
          return;
        }
        this.toast.ok('Perfil atualizado.');
        this.form.reset({ ...v, senha: '', nome: novo.nome });
      },
      error: (e) => {
        this.enviando.set(false);
        this.erro.set(mensagemDeErro(e, 'Esse nome ou e-mail já está em uso por outra conta.'));
      },
    });
  }
}
