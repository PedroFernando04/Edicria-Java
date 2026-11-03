import { Routes } from '@angular/router';
import { autenticadoGuard, visitanteGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: 'entrar',
    canActivate: [visitanteGuard],
    loadComponent: () => import('./features/auth/entrar').then((m) => m.Entrar),
    title: 'Entrar · Edicria',
  },
  {
    path: 'cadastro',
    canActivate: [visitanteGuard],
    loadComponent: () => import('./features/auth/cadastro').then((m) => m.Cadastro),
    title: 'Criar conta · Edicria',
  },
  {
    path: '',
    canActivate: [autenticadoGuard],
    loadComponent: () => import('./layout/shell').then((m) => m.Shell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'estante' },
      {
        path: 'estante',
        loadComponent: () => import('./features/shelf/estante').then((m) => m.Estante),
        title: 'Minha estante · Edicria',
      },
      {
        path: 'catalogo',
        loadComponent: () => import('./features/catalog/catalogo').then((m) => m.Catalogo),
        title: 'Catálogo · Edicria',
      },
      {
        path: 'catalogo/:id',
        loadComponent: () => import('./features/catalog/livro-detalhe').then((m) => m.LivroDetalhe),
        title: 'Livro · Edicria',
      },
      {
        path: 'autores',
        loadComponent: () => import('./features/people/autores').then((m) => m.Autores),
        title: 'Autores · Edicria',
      },
      {
        path: 'editoras',
        loadComponent: () => import('./features/people/editoras').then((m) => m.Editoras),
        title: 'Editoras · Edicria',
      },
      {
        path: 'perfil',
        loadComponent: () => import('./features/profile/perfil').then((m) => m.Perfil),
        title: 'Meu perfil · Edicria',
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
