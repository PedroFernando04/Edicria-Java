// Tipos que espelham os DTOs de resposta e requisição da API Java.
// Os enums chegam como texto (o nome do enum Java), ex.: "LIDO".

export type Genero = 'MASCULINO' | 'FEMININO' | 'OUTRO';
export type Categoria = 'HQ' | 'GRAPHIC_NOVEL' | 'MANGA' | 'LIVRO';
export type Formato = 'DIGITAL' | 'FISICO' | 'EBOOK' | 'AUDIOBOOK';
export type StatusLeitura = 'LIDO' | 'LENDO' | 'LISTA_DE_DESEJOS' | 'DESISTI';
export type Lingua = 'PORTUGUES' | 'INGLES' | 'ITALIANO' | 'ESPANHOL' | 'FRANCES';

/** Datas (LocalDate no Java) trafegam como "yyyy-MM-dd". */
export type DataISO = string;

// ---------- Respostas ----------
export interface Usuario {
  id: number;
  nome: string;
  email: string;
  genero: Genero;
  pais: string;
  dataNascimento: DataISO;
  adm: boolean;
}

export interface Autor {
  id: number;
  nome: string;
  paisOrigem: string;
  genero: Genero;
}

export interface Editora {
  id: number;
  nome: string;
  paisOrigem: string | null;
}

export interface Livro {
  id: number;
  titulo: string;
  autor: Autor;
  editora: Editora;
  dataLancamento: DataISO;
  categoria: Categoria;
  formato: Formato;
  numeroPaginas: number;
}

export interface LivroCliente {
  id: number;
  usuario: Usuario;
  livro: Livro;
  statusLivro: StatusLeitura;
  dataInicioLeitura: DataISO | null;
  dataTerminoLeitura: DataISO | null;
  nota: number | null;
  resenha: string | null;
  linguaLida: Lingua | null;
}

// ---------- Requisições ----------
export interface CadastroUsuario {
  nome: string;
  email: string;
  senha: string;
  genero: Genero;
  pais: string;
  dataNascimento: DataISO;
}

export type AtualizacaoUsuario = Partial<CadastroUsuario>;

export interface NovoAutor {
  nome: string;
  paisOrigem: string;
  genero: Genero;
}

export interface NovaEditora {
  nome: string;
  paisOrigem: string;
}

/** A API recebe autor e editora pelo NOME, não pelo id. */
export interface NovoLivro {
  titulo: string;
  autor: string;
  editora: string;
  dataLancamento: DataISO;
  categoria: Categoria;
  formato: Formato;
  numeroPaginas: number;
}

export type AtualizacaoLivro = Partial<NovoLivro>;

/** A API recebe usuário (e-mail) e livro (título) pelo texto. */
export interface NovaLeitura {
  emailUsuario: string;
  nomeLivro: string;
  statusLivro: StatusLeitura;
  dataInicioLeitura: DataISO | null;
  dataTerminoLeitura: DataISO | null;
  nota: number | null;
  resenha: string | null;
  linguaLida: Lingua | null;
}

export type AtualizacaoLeitura = Partial<NovaLeitura>;
