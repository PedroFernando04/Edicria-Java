import { HttpErrorResponse } from '@angular/common/http';

/**
 * A API Java não devolve mensagens de erro úteis: as exceções de negócio viram
 * HTTP 500 sem texto, e a validação (@Valid) vira 400. Por isso cada tela passa
 * um `contexto` com a explicação mais provável para o 500.
 */
export function mensagemDeErro(erro: unknown, contexto?: string): string {
  if (!(erro instanceof HttpErrorResponse)) {
    return 'Algo deu errado. Tente de novo.';
  }
  switch (erro.status) {
    case 0:
      return 'Não consegui falar com o servidor. Verifique se a API está rodando na porta 8080.';
    case 400:
      return 'Algum campo está inválido. Confira os dados e tente de novo.';
    case 401:
      return 'Sua sessão expirou. Entre de novo.';
    case 403:
      return 'Você não tem permissão para fazer isso.';
    case 404:
      return 'Não encontrei o que você pediu.';
    default:
      return contexto ?? 'O servidor não conseguiu concluir a ação.';
  }
}
