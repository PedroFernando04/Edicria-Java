import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, of, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Faz duas coisas por conta da API Java:
 *
 * 1. Envia o cookie de sessão em toda chamada (withCredentials).
 * 2. Alguns GETs de /api/book-user respondem "302 Found" COM corpo JSON (o
 *    controller usa HttpStatus.FOUND em vez de OK). O navegador não segue o
 *    redirecionamento (não há header Location) e o Angular trataria como erro.
 *    Aqui convertemos esse 302 em uma resposta 200 normal.
 *
 * Também trata 401: se a sessão caiu, limpa o login e volta para /entrar.
 */
export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return next(req.clone({ withCredentials: true })).pipe(
    catchError((erro: unknown) => {
      if (erro instanceof HttpErrorResponse) {
        if (erro.status === 302 && erro.error !== null && typeof erro.error === 'object') {
          return of(new HttpResponse({ body: erro.error, status: 200, url: erro.url ?? undefined }));
        }
        const ehLogin = req.url.endsWith('/users/login');
        if (erro.status === 401 && !ehLogin) {
          auth.limparSessao();
          router.navigate(['/entrar']);
        }
      }
      return throwError(() => erro);
    }),
  );
};
