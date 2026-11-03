import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

/** Cabeçalho + área de conteúdo das telas que exigem login. */
@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <a class="pular" href="#conteudo">Pular para o conteúdo</a>
    <header class="topo">
      <div class="topo-interno">
        <a routerLink="/estante" class="marca" aria-label="Edicria, ir para minha estante">Edicria</a>

        <nav aria-label="Principal">
          <a routerLink="/estante" routerLinkActive="ativo">Minha estante</a>
          <a routerLink="/catalogo" routerLinkActive="ativo">Catálogo</a>
          <a routerLink="/autores" routerLinkActive="ativo">Autores</a>
          <a routerLink="/editoras" routerLinkActive="ativo">Editoras</a>
        </nav>

        <div class="conta">
          <a routerLink="/perfil" routerLinkActive="ativo" class="nome">{{ auth.usuario()?.nome }}</a>
          <button type="button" class="btn btn-pequeno btn-leve" (click)="sair()">Sair</button>
        </div>
      </div>
    </header>
    <main id="conteudo" tabindex="-1">
      <router-outlet />
    </main>
  `,
  styles: `
    .pular {
      position: absolute; left: .5rem; top: -4rem; z-index: 200;
      background: var(--marca); padding: .5rem .8rem; border-radius: 6px; font-weight: 600;
    }
    .pular:focus { top: .5rem; }
    .topo { background: var(--tinta); color: var(--papel); }
    .topo-interno {
      width: min(var(--largura), 100% - 2rem); margin-inline: auto;
      display: flex; flex-wrap: wrap; align-items: center; gap: .5rem 2rem; padding-block: .7rem;
    }
    .marca {
      font-family: var(--fonte-titulo); font-weight: 700; font-size: 1.5rem;
      letter-spacing: -.02em; text-decoration: none; margin-right: .5rem;
    }
    nav { display: flex; flex-wrap: wrap; gap: .25rem 1.25rem; flex: 1; }
    .conta { margin-left: auto; }
    @media (max-width: 760px) {
      .topo-interno { gap: .25rem 1rem; }
      nav { order: 3; flex: 0 0 100%; }
      .marca { margin-right: 0; }
    }
    nav a, .nome {
      color: var(--papel); text-decoration: none; font-weight: 500; padding: .35rem .1rem;
      background: linear-gradient(var(--marca), var(--marca)) no-repeat 0 100% / 0 3px;
      transition: background-size .18s ease-out;
    }
    nav a:hover, .nome:hover { background-size: 100% 3px; }
    nav a.ativo, .nome.ativo { background-size: 100% 3px; font-weight: 600; }
    .conta { display: flex; align-items: center; gap: .5rem; }
    .conta .nome { max-width: 12rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .conta .btn { color: var(--papel); }
    .conta .btn:hover { background: rgb(255 255 255 / .12); }
    :host a:focus-visible, :host button:focus-visible { outline-color: var(--marca); }
    main:focus { outline: none; }
  `,
})
export class Shell {
  protected readonly auth = inject(AuthService);

  protected sair() {
    this.auth.sair().subscribe();
  }
}
