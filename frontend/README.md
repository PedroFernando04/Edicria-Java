# Edicria — Frontend (Angular 21)

Interface web para a API Edicria (Spring Boot). Registre livros, HQs, mangás e graphic novels,
organize tudo numa estante de lombadas, dê notas e escreva resenhas.

## Como rodar

1. Suba a API Java (porta 8080, com o PostgreSQL configurado).
2. Neste diretório:

```bash
npm install
npm start        # abre em http://localhost:4200
```

Se o `npm install` falhar com `Cannot read properties of null (reading 'edgesOut')`, use
`npm install --legacy-peer-deps` (o arquivo `.npmrc` já faz isso por padrão).

O `ng serve` encaminha tudo que começa com `/api` para `http://localhost:8080` (`proxy.conf.json`).
Assim o navegador enxerga front e API como a mesma origem: o cookie de sessão funciona e **não é
preciso configurar CORS no Spring**.

## Telas

| Rota | O que faz |
|---|---|
| `/entrar`, `/cadastro` | Login e criação de conta |
| `/estante` | Sua estante: lombadas agrupadas por situação (lendo, lido, quero ler, desisti). Clique numa lombada para editar ou remover |
| `/catalogo` | Todos os livros, com busca e filtro por categoria; cadastro de livro |
| `/catalogo/:id` | Detalhes do livro e a sua leitura (nota, datas, resenha) |
| `/autores`, `/editoras` | Listagem, cadastro e edição |
| `/perfil` | Edição dos seus dados |

Na estante, a **altura e a espessura** da lombada vêm do número de páginas e a **cor** vem da
categoria. A nota aparece no selo amarelo no topo.

## Estrutura

```
src/app/
  core/       models.ts (tipos dos DTOs), api.ts (chamadas HTTP), auth.service.ts,
              api.interceptor.ts, auth.guard.ts, rotulos.ts (nomes dos enums), paises.ts
  shared/     modal (<dialog> nativo), lombada, avisos (toasts)
  layout/     shell (cabeçalho), auth-layout
  features/   auth, shelf, catalog, people, profile
```

## Como o front conversa com a API

- **Login por sessão**: `POST /api/users/login` cria o cookie `JSESSIONID`. O login não devolve
  dados do usuário, então o front busca `GET /api/users` e localiza o usuário pelo e-mail.
- **Status 302 nos GETs de `/api/book-user`**: o controller responde `HttpStatus.FOUND` com corpo
  JSON. O `api.interceptor.ts` converte isso em 200. Se você trocar para `HttpStatus.OK` no Java
  (recomendado), o interceptor continua funcionando.
- **Referências por nome**: a API recebe autor, editora, livro e usuário pelo texto (nome, título,
  e-mail), então os formulários usam listas de seleção com esses valores.
- **Erros**: as exceções de negócio viram HTTP 500 sem mensagem, então as telas mostram a causa
  mais provável ("já existe um autor com esse nome?").

## Pontos do backend que vale corrigir

- `LivroService.updateLivro` chama `existsByTitulo(dto.titulo())` antes de checar `null` e antes de
  comparar com o título atual: enviar o mesmo título falha. O front só envia os campos alterados.
- `updateLivro` não atualiza `numeroPaginas` (o campo fica desabilitado na edição).
- Os updates ignoram campos `null`, então não dá para apagar nota, datas ou resenha depois de salvos.
- Falta um `@ControllerAdvice` para devolver 400/404/409 com mensagem em vez de 500.
- `GET /api/users` é público e lista todos os usuários (e-mails inclusos).
- Não existe `GET /api/users/me`; um endpoint assim evitaria o passo extra no login.
- Para publicar o front em outro domínio, configure CORS com credenciais; em desenvolvimento o proxy resolve.

## Build de produção

```bash
npm run build    # saída em dist/edicria-frontend/browser
```
