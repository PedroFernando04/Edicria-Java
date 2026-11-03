import { Categoria, Formato, Genero, Lingua, StatusLeitura } from './models';

export interface Opcao<T extends string = string> {
  valor: T;
  nome: string;
}

export const GENEROS: Opcao<Genero>[] = [
  { valor: 'MASCULINO', nome: 'Masculino' },
  { valor: 'FEMININO', nome: 'Feminino' },
  { valor: 'OUTRO', nome: 'Outro' },
];

export const CATEGORIAS: Opcao<Categoria>[] = [
  { valor: 'LIVRO', nome: 'Livro' },
  { valor: 'HQ', nome: 'História em quadrinhos' },
  { valor: 'MANGA', nome: 'Mangá' },
  { valor: 'GRAPHIC_NOVEL', nome: 'Graphic novel' },
];

export const FORMATOS: Opcao<Formato>[] = [
  { valor: 'FISICO', nome: 'Físico' },
  { valor: 'DIGITAL', nome: 'Digital' },
  { valor: 'EBOOK', nome: 'E-book' },
  { valor: 'AUDIOBOOK', nome: 'Audiobook' },
];

export const STATUS: Opcao<StatusLeitura>[] = [
  { valor: 'LENDO', nome: 'Lendo' },
  { valor: 'LIDO', nome: 'Lido' },
  { valor: 'LISTA_DE_DESEJOS', nome: 'Quero ler' },
  { valor: 'DESISTI', nome: 'Desisti' },
];

export const LINGUAS: Opcao<Lingua>[] = [
  { valor: 'PORTUGUES', nome: 'Português' },
  { valor: 'INGLES', nome: 'Inglês' },
  { valor: 'ESPANHOL', nome: 'Espanhol' },
  { valor: 'ITALIANO', nome: 'Italiano' },
  { valor: 'FRANCES', nome: 'Francês' },
];

function indexar<T extends string>(lista: Opcao<T>[]): Record<string, string> {
  return Object.fromEntries(lista.map((o) => [o.valor, o.nome]));
}

const porCategoria = indexar(CATEGORIAS);
const porFormato = indexar(FORMATOS);
const porStatus = indexar(STATUS);
const porLingua = indexar(LINGUAS);
const porGenero = indexar(GENEROS);

export const nomeCategoria = (v: string | null | undefined) => (v ? (porCategoria[v] ?? v) : '');
export const nomeFormato = (v: string | null | undefined) => (v ? (porFormato[v] ?? v) : '');
export const nomeStatus = (v: string | null | undefined) => (v ? (porStatus[v] ?? v) : '');
export const nomeLingua = (v: string | null | undefined) => (v ? (porLingua[v] ?? v) : '');
export const nomeGenero = (v: string | null | undefined) => (v ? (porGenero[v] ?? v) : '');
