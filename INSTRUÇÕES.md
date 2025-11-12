# Instruções de Uso - BuzzHeavier App

## 📱 Sobre o Aplicativo

Este é um aplicativo Android não oficial para o serviço de armazenamento em nuvem BuzzHeavier, desenvolvido com:
- **Kotlin** 
- **Jetpack Compose**
- **Material You (Material Design 3)**

## 🚀 Como Compilar

### Pré-requisitos
- JDK 17
- Android SDK (se estiver usando Android Studio)
- Git (para clonar o projeto)

### Passos para Compilar

1. **Extrair o projeto**
   ```bash
   unzip buzzheavier-app.zip
   cd buzzheavier-app
   ```

2. **Compilar o APK Debug**
   ```bash
   ./gradlew assembleDebug
   ```
   
   O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

3. **Instalar no dispositivo**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 🔧 Usando o GitHub Actions

### Configuração Automática

1. Faça upload do projeto para o GitHub
2. O workflow `.github/workflows/build.yml` será executado automaticamente em cada push/PR
3. O APK compilado ficará disponível na aba **Actions** do repositório como artefato

### Branches Monitorados
- main
- master

Você pode modificar os branches no arquivo `build.yml` se necessário.

## 📋 Funcionalidades Implementadas

✅ **Login com Account ID**
- Tela de login segura
- Armazenamento persistente do token
- Validação de credenciais

✅ **Gerenciador de Arquivos**
- Navegação por pastas
- Visualização de arquivos e pastas
- Lista com ícones Material

✅ **Operações com Pastas**
- Criar novas pastas
- Renomear pastas
- Deletar pastas
- Navegar entre diretórios

✅ **Operações com Arquivos**
- Enviar arquivos (qualquer tipo)
- Renomear arquivos
- Deletar arquivos
- Visualizar informações (nome, tamanho)

✅ **Interface Material You**
- Tema dinâmico (Android 12+)
- Cores adaptativas do sistema
- Ícones Material
- Animações fluidas

## 🔑 Como Obter o Account ID

1. Acesse [buzzheavier.com](https://buzzheavier.com)
2. Faça login ou crie uma conta
3. No perfil/configurações, localize seu **Account ID**
4. Copie o ID e use no app

**Nota:** O Account ID funciona como sua senha de acesso à API.

## 📁 Estrutura do Projeto

```
buzzheavier-app/
├── .github/
│   └── workflows/
│       └── build.yml              # GitHub Actions workflow
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/buzzheavier/app/
│   │       │   ├── data/
│   │       │   │   ├── api/      # Retrofit APIs
│   │       │   │   ├── model/    # Data classes
│   │       │   │   └── repository/ # Repository pattern
│   │       │   ├── ui/
│   │       │   │   ├── screens/  # Telas (Login, FileManager)
│   │       │   │   ├── navigation/ # Sistema de navegação
│   │       │   │   └── theme/    # Material You theme
│   │       │   ├── util/         # Utilitários
│   │       │   └── MainActivity.kt
│   │       ├── res/              # Recursos Android
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## 🎨 Personalização

### Alterar Cores
Edite: `app/src/main/java/com/buzzheavier/app/ui/theme/Color.kt`

### Alterar Nome do App
Edite: `app/src/main/res/values/strings.xml`

### Alterar Ícone
Substitua os arquivos em: `app/src/main/res/mipmap-*/`

## 🐛 Resolução de Problemas

### Erro: SDK not found
Edite `local.properties` e defina o caminho do Android SDK:
```properties
sdk.dir=/caminho/para/android/sdk
```

### Erro: Permission denied (gradlew)
```bash
chmod +x gradlew
```

### Erro de compilação
```bash
./gradlew clean
./gradlew assembleDebug
```

## 📱 Requisitos do App

- **Android 7.0** (API 24) ou superior
- Permissões:
  - Internet (para API)
  - Leitura de arquivos (para upload)

## 🔐 Segurança

- O Account ID é armazenado localmente usando DataStore (criptografado)
- Todas as comunicações usam HTTPS
- Nenhuma informação é enviada para terceiros

## 📞 Suporte

Para dúvidas sobre a API BuzzHeavier, consulte:
https://buzzheavier.com/developers

## ⚠️ Aviso Legal

Este é um aplicativo **NÃO OFICIAL** e não tem afiliação com o BuzzHeavier.
Use por sua conta e risco.

## 📄 Licença

Projeto de código aberto. Use livremente.

---

**Desenvolvido com ❤️ usando Kotlin e Jetpack Compose**
