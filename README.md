# BuzzHeavier App (Não Oficial)

Um aplicativo Android não oficial para o serviço de armazenamento em nuvem BuzzHeavier, desenvolvido com Kotlin e Material You.

## Funcionalidades

- ✅ Login com Account ID
- ✅ Navegação por pastas
- ✅ Visualização de arquivos e pastas
- ✅ Criar novas pastas
- ✅ Enviar arquivos
- ✅ Renomear arquivos e pastas
- ✅ Deletar arquivos e pastas
- ✅ Interface Material You (Material Design 3)
- ✅ Suporte a tema dinâmico (Android 12+)

## Tecnologias Utilizadas

- **Kotlin** - Linguagem de programação
- **Jetpack Compose** - UI moderna e declarativa
- **Material 3** - Design System do Google
- **Retrofit** - Cliente HTTP para chamadas API
- **OkHttp** - Gerenciamento de requisições HTTP
- **DataStore** - Armazenamento de preferências
- **Coil** - Carregamento de imagens
- **Coroutines & Flow** - Programação assíncrona
- **ViewModel** - Gerenciamento de estado

## Requisitos

- Android 7.0 (API 24) ou superior
- Account ID do BuzzHeavier

## Como Obter o Account ID

1. Acesse [buzzheavier.com](https://buzzheavier.com)
2. Faça login na sua conta
3. Vá em configurações/perfil
4. Copie seu Account ID

## Compilação

### Compilar APK Debug

```bash
./gradlew assembleDebug
```

O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Compilar APK Release

```bash
./gradlew assembleRelease
```

## GitHub Actions

O projeto inclui um workflow do GitHub Actions que compila automaticamente o APK de debug em cada push/pull request.

O APK compilado fica disponível como artefato na aba Actions do repositório.

## Estrutura do Projeto

```
app/
├── src/main/
│   ├── java/com/buzzheavier/app/
│   │   ├── data/
│   │   │   ├── api/          # Interfaces Retrofit
│   │   │   ├── model/        # Modelos de dados
│   │   │   └── repository/   # Repositórios
│   │   ├── ui/
│   │   │   ├── screens/      # Telas do app
│   │   │   ├── navigation/   # Navegação
│   │   │   └── theme/        # Tema Material You
│   │   ├── util/             # Utilitários
│   │   └── MainActivity.kt
│   ├── res/                  # Recursos (layouts, strings, etc)
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## API BuzzHeavier

Este app utiliza a API oficial do BuzzHeavier. Documentação disponível em:
https://buzzheavier.com/developers

## Licença

Este é um projeto não oficial e não tem afiliação com o BuzzHeavier.

## Aviso

Use por sua conta e risco. Este aplicativo não é oficial e não é mantido pelo BuzzHeavier.
