import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Avisos } from './shared/avisos';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Avisos],
  template: `
    <router-outlet />
    <app-avisos />
  `,
})
export class App {}
