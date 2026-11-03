/**
 * Endereço base da API.
 *
 * Em desenvolvimento usamos um caminho relativo ("/api") e o `ng serve` encaminha
 * essas chamadas para http://localhost:8080 (veja proxy.conf.json). Assim o navegador
 * enxerga frontend e API como a mesma origem: o cookie de sessão (JSESSIONID) funciona
 * e não é preciso configurar CORS no Spring.
 *
 * Em produção, sirva o frontend e a API sob o mesmo domínio (ex.: um proxy reverso)
 * ou troque este valor pela URL da API — nesse caso será necessário liberar CORS com
 * credenciais no backend.
 */
export const API_URL = '/api';
