import { Component, input } from '@angular/core';
import { Lombada } from '../shared/lombada';

/** Moldura das telas de entrada e cadastro: frase à esquerda, formulário à direita. */
@Component({
  selector: 'app-auth-layout',
  imports: [Lombada],
  template: `
    <div class="tela">
      <aside>
        <p class="marca">Edicria</p>
        <div class="texto">
          <h1>Registre o que você lê.</h1>
          <p>Guarde livros, quadrinhos e mangás, dê uma nota e escreva a resenha enquanto a leitura ainda está fresca.</p>
        </div>
        <div class="estante" aria-hidden="true">
          <app-lombada titulo="Dom Casmurro" autor="Machado de Assis" categoria="LIVRO" [paginas]="256" />
          <app-lombada titulo="Persépolis" autor="Marjane Satrapi" categoria="GRAPHIC_NOVEL" [paginas]="352" />
          <app-lombada titulo="Akira" autor="Katsuhiro Otomo" categoria="MANGA" [paginas]="320" [nota]="9.5" />
          <app-lombada titulo="Watchmen" autor="Alan Moore" categoria="HQ" [paginas]="416" />
          <app-lombada titulo="Grande Sertão: Veredas" autor="Guimarães Rosa" categoria="LIVRO" [paginas]="608" />
          <app-lombada titulo="Maus" autor="Art Spiegelman" categoria="GRAPHIC_NOVEL" [paginas]="296" />
        </div>
      </aside>
      <main>
        <div class="miolo">
          <h2>{{ titulo() }}</h2>
          <ng-content />
        </div>
      </main>
    </div>
  `,
  styles: `
    .tela { min-height: 100dvh; display: grid; grid-template-columns: minmax(0, 1.05fr) minmax(0, 1fr); }
    aside {
      background: var(--tinta); color: var(--papel);
      display: flex; flex-direction: column; justify-content: space-between; gap: 2rem;
      padding: 2rem clamp(1.5rem, 4vw, 3.5rem) 0;
    }
    .marca { font-family: var(--fonte-titulo); font-weight: 700; font-size: 1.6rem; letter-spacing: -.02em; }
    .texto h1 { max-width: 14ch; }
    .texto p { margin-top: 1rem; max-width: 42ch; color: #c9cde3; }
    .estante {
      display: flex; align-items: flex-end; gap: 4px;
      border-bottom: 10px solid var(--marca); margin-inline: calc(-1 * clamp(1.5rem, 4vw, 3.5rem)); padding-inline: clamp(1.5rem, 4vw, 3.5rem);
    }
    main { display: grid; place-items: center; padding: 2rem 1rem; }
    .miolo { width: min(26rem, 100%); display: flex; flex-direction: column; gap: 1.5rem; }
    @media (max-width: 800px) {
      .tela { grid-template-columns: 1fr; }
      aside { padding-top: 1.25rem; gap: 1.25rem; }
      .texto p { display: none; }
      .estante { display: none; }
      .texto h1 { font-size: 1.6rem; max-width: none; }
    }
  `,
})
export class AuthLayout {
  readonly titulo = input.required<string>();
}
