import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './config';
import {
  AtualizacaoLeitura,
  AtualizacaoLivro,
  Autor,
  Editora,
  Livro,
  LivroCliente,
  NovaEditora,
  NovaLeitura,
  NovoAutor,
  NovoLivro,
} from './models';

/** GET/POST/PUT /api/books */
@Injectable({ providedIn: 'root' })
export class LivrosApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/books`;

  listar(): Observable<Livro[]> {
    return this.http.get<Livro[]>(this.url);
  }
  buscar(id: number): Observable<Livro> {
    return this.http.get<Livro>(`${this.url}/${id}`);
  }
  buscarPorTitulo(titulo: string): Observable<Livro> {
    return this.http.get<Livro>(`${this.url}/titulo`, { params: new HttpParams().set('titulo', titulo) });
  }
  criar(dados: NovoLivro): Observable<Livro> {
    return this.http.post<Livro>(this.url, dados);
  }
  atualizar(id: number, dados: AtualizacaoLivro): Observable<Livro> {
    return this.http.put<Livro>(`${this.url}/${id}`, dados);
  }
}

/** GET/POST/PUT /api/authors */
@Injectable({ providedIn: 'root' })
export class AutoresApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/authors`;

  listar(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.url);
  }
  criar(dados: NovoAutor): Observable<Autor> {
    return this.http.post<Autor>(this.url, dados);
  }
  atualizar(id: number, dados: Partial<NovoAutor>): Observable<Autor> {
    return this.http.put<Autor>(`${this.url}/${id}`, dados);
  }
}

/** GET/POST/PUT /api/publishers */
@Injectable({ providedIn: 'root' })
export class EditorasApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/publishers`;

  listar(): Observable<Editora[]> {
    return this.http.get<Editora[]>(this.url);
  }
  criar(dados: NovaEditora): Observable<Editora> {
    return this.http.post<Editora>(this.url, dados);
  }
  atualizar(id: number, dados: Partial<NovaEditora>): Observable<Editora> {
    return this.http.put<Editora>(`${this.url}/${id}`, dados);
  }
}

/** A estante pessoal de cada usuário: /api/book-user */
@Injectable({ providedIn: 'root' })
export class LeiturasApi {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/book-user`;

  listarDoUsuario(idUsuario: number): Observable<LivroCliente[]> {
    return this.http.get<LivroCliente[]>(`${this.url}/all/${idUsuario}`);
  }
  buscarPorLivroEUsuario(idLivro: number, idUsuario: number): Observable<LivroCliente> {
    return this.http.get<LivroCliente>(`${this.url}/${idLivro}/${idUsuario}`);
  }
  criar(dados: NovaLeitura): Observable<LivroCliente> {
    return this.http.post<LivroCliente>(this.url, dados);
  }
  atualizar(idLivro: number, idUsuario: number, dados: AtualizacaoLeitura): Observable<LivroCliente> {
    return this.http.put<LivroCliente>(`${this.url}/${idLivro}/${idUsuario}`, dados);
  }
  remover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
