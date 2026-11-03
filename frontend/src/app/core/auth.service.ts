import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { API_URL } from './config';
import { AtualizacaoUsuario, CadastroUsuario, Usuario } from './models';

const CHAVE = 'edicria.usuario';

function lerSalvo(): Usuario | null {
  try {
    const bruto = localStorage.getItem(CHAVE);
    return bruto ? (JSON.parse(bruto) as Usuario) : null;
  } catch {
    return null;
  }
}

function salvar(usuario: Usuario | null) {
  try {
    if (usuario) localStorage.setItem(CHAVE, JSON.stringify(usuario));
    else localStorage.removeItem(CHAVE);
  } catch {
    /* sem armazenamento: o usuário só precisará entrar de novo ao recarregar */
  }
}

/**
 * Guarda quem está logado.
 *
 * A API usa sessão por cookie (JSESSIONID), então o navegador cuida do "token".
 * Aqui guardamos apenas os dados do usuário (principalmente o `id`, que várias
 * rotas da API exigem). Se a sessão expirar no servidor, o interceptor recebe um
 * 401 e chama `limparSessao()`.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly usuario = signal<Usuario | null>(lerSalvo());
  readonly logado = computed(() => this.usuario() !== null);

  cadastrar(dados: CadastroUsuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${API_URL}/users`, dados);
  }

  /**
   * O login da API responde só "200 OK" (sem dados do usuário), então depois de
   * autenticar buscamos a lista de usuários e localizamos o nosso pelo e-mail.
   */
  entrar(email: string, senha: string): Observable<Usuario> {
    return this.http.post<void>(`${API_URL}/users/login`, { email, senha }).pipe(
      switchMap(() => this.http.get<Usuario[]>(`${API_URL}/users`)),
      map((lista) => {
        const alvo = email.trim().toLowerCase();
        const achado = lista.find((u) => u.email.toLowerCase() === alvo);
        if (!achado) throw new Error('Usuário autenticado não encontrado na lista.');
        return achado;
      }),
      tap((u) => this.definirUsuario(u)),
    );
  }

  sair(): Observable<void> {
    return this.http.post<void>(`${API_URL}/users/logout`, {}).pipe(
      catchError(() => of(undefined)),
      tap(() => {
        this.limparSessao();
        this.router.navigate(['/entrar']);
      }),
      map(() => undefined),
    );
  }

  atualizarPerfil(dados: AtualizacaoUsuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${API_URL}/users/me`, dados).pipe(
      tap((u) => this.definirUsuario(u)),
    );
  }

  limparSessao() {
    this.definirUsuario(null);
  }

  private definirUsuario(usuario: Usuario | null) {
    this.usuario.set(usuario);
    salvar(usuario);
  }
}
