import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Só deixa passar quem está logado; os demais vão para a tela de entrada. */
export const autenticadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.logado() ? true : inject(Router).createUrlTree(['/entrar']);
};

/** Telas de entrada/cadastro: quem já está logado vai direto para a estante. */
export const visitanteGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.logado() ? inject(Router).createUrlTree(['/estante']) : true;
};
