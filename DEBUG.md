# Guia de Debug - BuzzHeavier App

## Problemas Comuns e Soluções

### 1. Não Consegue Criar Pastas

**Sintomas:**
- Ao clicar em "Criar" na pasta, nada acontece
- Mensagem de erro aparece na tela

**Possíveis Causas:**
1. **Account ID inválido ou expirado**
   - Verifique se o Account ID está correto
   - Tente fazer logout e login novamente

2. **Problema de conexão**
   - Verifique sua conexão com a internet
   - Tente atualizar a página (botão de refresh)

3. **Diretório pai inválido**
   - Certifique-se de estar dentro de um diretório válido
   - Volte para a raiz e tente novamente

**Solução:**
1. Abra o Logcat no Android Studio
2. Filtre por "BuzzHeavier" ou "OkHttp"
3. Procure pelo código de erro HTTP (400, 401, 403, 404, 500)
4. Verifique a resposta da API

**Códigos de Erro Comuns:**
- `401 Unauthorized` - Account ID inválido
- `403 Forbidden` - Sem permissão
- `404 Not Found` - Diretório pai não existe
- `500 Internal Server Error` - Problema no servidor

### 2. Não Consegue Enviar Arquivos

**Sintomas:**
- Arquivo selecionado mas não aparece upload
- Erro ao tentar enviar arquivo
- Upload fica travado no loading

**Possíveis Causas:**
1. **Arquivo muito grande**
   - Verifique o limite de tamanho da sua conta
   - Tente com arquivos menores primeiro

2. **Permissões de arquivo**
   - O app precisa de permissão para ler arquivos
   - Vá em Configurações > Apps > BuzzHeavier > Permissões
   - Ative "Arquivos e mídia" ou "Armazenamento"

3. **Problema de conexão**
   - Upload requer conexão estável
   - Tente com WiFi ao invés de dados móveis

4. **Content-Type incorreto**
   - Alguns tipos de arquivo podem não ser suportados
   - Tente renomear o arquivo com extensão correta

**Solução:**
1. Verifique os logs do Logcat:
```
adb logcat | grep -i "buzzheavier\|upload\|okhttp"
```

2. Procure por:
   - Status code da requisição
   - Tamanho do arquivo sendo enviado
   - Resposta da API

3. Tente com arquivo pequeno (< 1MB) de teste primeiro

### 3. Debug com Logcat

**Como ver logs completos:**

1. Conecte o dispositivo via USB
2. Ative "Depuração USB" nas Opções do Desenvolvedor
3. Execute no terminal:
```bash
adb logcat -s OkHttp
```

**Logs importantes:**
- `OkHttp` - Requisições HTTP completas
- `System.err` - Exceções e stack traces
- `BuzzHeavierRepository` - Operações do repositório

### 4. Testar API Manualmente

**Criar Pasta:**
```bash
curl -X POST "https://buzzheavier.com/api/fs/{parentDirectoryId}" \
  -H "Authorization: Bearer SEU_ACCOUNT_ID" \
  -H "Content-Type: application/json" \
  -d '{"name": "teste"}'
```

**Upload de Arquivo:**
```bash
curl -X PUT "https://w.buzzheavier.com/{parentId}/teste.txt" \
  -H "Authorization: Bearer SEU_ACCOUNT_ID" \
  -H "Content-Type: text/plain" \
  --data-binary "@arquivo.txt"
```

**Listar Diretório Raiz:**
```bash
curl -X GET "https://buzzheavier.com/api/fs" \
  -H "Authorization: Bearer SEU_ACCOUNT_ID"
```

### 5. Verificar Permissões do App

No dispositivo Android:
1. Vá em **Configurações** > **Apps**
2. Procure por **BuzzHeavier**
3. Toque em **Permissões**
4. Verifique se tem:
   - ✅ Internet (obrigatório)
   - ✅ Arquivos e mídia / Armazenamento (para upload)

### 6. Limpar Cache e Dados

Se o app está com comportamento estranho:
1. Vá em **Configurações** > **Apps** > **BuzzHeavier**
2. Toque em **Armazenamento**
3. Toque em **Limpar cache**
4. Se necessário, toque em **Limpar dados** (irá fazer logout)

### 7. Recompilar o App

Se fez alterações no código:
```bash
cd buzzheavier-app
./gradlew clean
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 8. Verificar Conectividade com a API

Teste se a API está acessível:
```bash
curl -I https://buzzheavier.com/api/locations
curl -I https://w.buzzheavier.com
```

Ambos devem retornar `200 OK` ou `401 Unauthorized` (que significa que a API está funcionando, só precisa de autenticação).

### 9. Problemas de Timeout

Se uploads grandes estão falhando com timeout:

1. Edite `RetrofitClient.kt`
2. Aumente os timeouts:
```kotlin
.connectTimeout(120, TimeUnit.SECONDS)
.readTimeout(240, TimeUnit.SECONDS)
.writeTimeout(240, TimeUnit.SECONDS)
```

### 10. Logs Detalhados

Para ver TODAS as requisições HTTP:

1. O app já está configurado com `HttpLoggingInterceptor`
2. Os logs aparecem automaticamente no Logcat
3. Procure por:
   - `-->` - Requisição sendo enviada
   - `<--` - Resposta recebida
   - Status code (200, 400, 500, etc)
   - Headers (Authorization, Content-Type)
   - Body da requisição e resposta

## Checklist de Troubleshooting

Quando algo não funciona:

- [ ] Verifiquei os logs do Logcat
- [ ] Confirmei que o Account ID está correto
- [ ] Testei com conexão WiFi estável
- [ ] Verifiquei as permissões do app
- [ ] Testei a API manualmente com curl
- [ ] Limpei o cache do app
- [ ] Tentei fazer logout e login novamente
- [ ] Recompilei o app após alterações
- [ ] Verifiquei que a API da BuzzHeavier está online

## Contato

Se o problema persistir:
1. Colete os logs do Logcat
2. Teste a API manualmente com curl
3. Verifique a documentação oficial: https://buzzheavier.com/developers
4. Abra uma issue no repositório com os logs e detalhes
