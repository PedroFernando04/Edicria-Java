import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { AuthLayout } from '../../layout/auth-layout';
import { mensagemDeErro } from '../../core/erros';
import { GENEROS } from '../../core/rotulos';
import { PAISES } from '../../core/paises';
import { Genero } from '../../core/models';
import { ToastService } from '../../core/toast.service';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout],
  template: `
    <app-auth-layout titulo="Criar sua conta">
      <form [formGroup]="form" (ngSubmit)="enviar()" novalidate class="grade-form">
        @if (erro()) {
          <p class="aviso-form inteira" role="alert">{{ erro() }}</p>
        }

        <div class="campo inteira">
          <label for="nome">Nome de usuário</label>
          <input id="nome" class="entrada" formControlName="nome" autocomplete="nickname" [class.invalido]="invalido('nome')" />
          <span class="dica">Aparece no seu perfil. Precisa ser único.</span>
          @if (invalido('nome')) { <span class="erro">Informe um nome.</span> }
        </div>

        <div class="campo inteira">
          <label for="email">E-mail</label>
          <input id="email" class="entrada" type="email" formControlName="email" autocomplete="email" [class.invalido]="invalido('email')" />
          @if (invalido('email')) { <span class="erro">Informe um e-mail válido.</span> }
        </div>

        <div class="campo inteira">
          <label for="senha">Senha</label>
          <input id="senha" class="entrada" type="password" formControlName="senha" autocomplete="new-password" [class.invalido]="invalido('senha')" />
          @if (invalido('senha')) { <span class="erro">Crie uma senha.</span> }
        </div>

        <div class="campo">
          <label for="genero">Gênero</label>
          <select id="genero" class="entrada" formControlName="genero" [class.invalido]="invalido('genero')">
            <option value="" disabled>Selecione</option>
            @for (g of generos; track g.valor) { <option [value]="g.valor">{{ g.nome }}</option> }
          </select>
          @if (invalido('genero')) { <span class="erro">Escolha uma opção.</span> }
        </div>

        <div class="campo">
          <label for="pais">País</label>
          <select id="pais" class="entrada" formControlName="pais" [class.invalido]="invalido('pais')">
            <option value="" disabled>Selecione</option>
            @for (p of paises; track p.valor) { <option [value]="p.valor">{{ p.nome }}</option> }
          </select>
          @if (invalido('pais')) { <span class="erro">Escolha um país.</span> }
        </div>

        <div class="campo inteira">
          <label for="nasc">Data de nascimento</label>
          <input id="nasc" class="entrada" type="date" formControlName="dataNascimento" [max]="ontem" [class.invalido]="invalido('dataNascimento')" />
          @if (invalido('dataNascimento')) { <span class="erro">Informe uma data no passado.</span> }
        </div>

        <button class="btn btn-principal inteira" type="submit" [disabled]="enviando()">
          {{ enviando() ? 'Criando conta...' : 'Criar conta' }}
        </button>

        <p class="inteira troca">Já tem conta? <a routerLink="/entrar">Entrar</a></p>
      </form>
    </app-auth-layout>
  `,
  styles: `.troca { color: var(--tinta-suave); }`,
})
export class Cadastro {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
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

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.pattern(/\S/)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', Validators.required],
    genero: ['' as Genero | '', Validators.required],
    pais: ['', Validators.required],
    dataNascimento: ['', Validators.required],
  });

  protected invalido(campo: keyof typeof this.form.controls) {
    const c = this.form.controls[campo];
    return c.invalid && (c.touched || c.dirty);
  }

  protected enviar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.erro.set(null);
    const v = this.form.getRawValue();
    const dados = { ...v, nome: v.nome.trim(), email: v.email.trim(), genero: v.genero as Genero };

    this.auth.cadastrar(dados).subscribe({
      next: () => {
        // Cadastro não abre sessão na API: entramos em seguida com os mesmos dados.
        this.auth.entrar(dados.email, dados.senha).subscribe({
          next: () => {
            this.toast.ok('Conta criada. Bem-vindo ao Edicria!');
            this.router.navigate(['/estante']);
          },
          error: () => this.router.navigate(['/entrar']),
        });
      },
      error: (e: unknown) => {
        this.enviando.set(false);
        this.erro.set(mensagemDeErro(e, 'Esse e-mail ou nome de usuário já está em uso. Tente outro.'));
      },
    });
  }
}
