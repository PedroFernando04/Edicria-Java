import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/auth.service';
import { AuthLayout } from '../../layout/auth-layout';
import { mensagemDeErro } from '../../core/erros';

@Component({
  selector: 'app-entrar',
  imports: [ReactiveFormsModule, RouterLink, AuthLayout],
  template: `
    <app-auth-layout titulo="Entrar na sua conta">
      <form [formGroup]="form" (ngSubmit)="enviar()" novalidate class="form">
        @if (erro()) {
          <p class="aviso-form" role="alert">{{ erro() }}</p>
        }

        <div class="campo">
          <label for="email">E-mail</label>
          <input id="email" class="entrada" type="email" formControlName="email" autocomplete="username"
                 [class.invalido]="invalido('email')" />
          @if (invalido('email')) { <span class="erro">Informe um e-mail válido.</span> }
        </div>

        <div class="campo">
          <label for="senha">Senha</label>
          <input id="senha" class="entrada" type="password" formControlName="senha" autocomplete="current-password"
                 [class.invalido]="invalido('senha')" />
          @if (invalido('senha')) { <span class="erro">Informe a senha.</span> }
        </div>

        <button class="btn btn-principal" type="submit" [disabled]="enviando()">
          {{ enviando() ? 'Entrando...' : 'Entrar' }}
        </button>

        <p class="troca">Ainda não tem conta? <a routerLink="/cadastro">Criar conta</a></p>
      </form>
    </app-auth-layout>
  `,
  styles: `
    .form { display: flex; flex-direction: column; gap: 1rem; }
    .troca { color: var(--tinta-suave); }
  `,
})
export class Entrar {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', Validators.required],
  });

  protected invalido(campo: 'email' | 'senha') {
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
    const { email, senha } = this.form.getRawValue();
    this.auth.entrar(email.trim(), senha).subscribe({
      next: () => this.router.navigate(['/estante']),
      error: (e: unknown) => {
        this.enviando.set(false);
        this.erro.set(
          e instanceof HttpErrorResponse && (e.status === 401 || e.status === 403)
            ? 'E-mail ou senha incorretos.'
            : mensagemDeErro(e, 'Não foi possível entrar. Confira e-mail e senha.'),
        );
      },
    });
  }
}
