# Ferramentas Avançadas e Gerenciamento de Projetos Linux para Microeletrônica

Este documento detalha ferramentas e práticas avançadas para otimizar o ambiente de desenvolvimento Linux, focando em gerenciamento de pacotes, configuração de shell, depuração de sistemas e controle de versão aplicado a projetos de hardware (HDL).

## 1. Instalação e Gerenciamento de Pacotes

Os gerenciadores de pacotes são essenciais para manter o sistema e as ferramentas de desenvolvimento atualizadas, lidando automaticamente com dependências.

### 1.1 APT (Advanced Package Tool)

Padrão em distribuições baseadas em Debian (Ubuntu, Mint).

**Comandos principais:**

- **Atualizar lista de repositórios:** `sudo apt update` (Executar antes de instalar qualquer coisa)
- **Atualizar pacotes instalados:** `sudo apt upgrade`
- **Instalar pacotes:** `sudo apt install <nome_do_pacote>`
  - *Exemplo para toolchain básica:* `sudo apt install build-essential git make gcc`
- **Remover pacotes:** `sudo apt remove <nome_do_pacote>`
- **Remoção completa (purge):** `sudo apt purge <nome_do_pacote>` (Remove também arquivos de configuração)
- **Limpeza automática:** `sudo apt autoremove` (Remove dependências que não são mais usadas)

### 1.2 YUM / DNF

Padrão em distribuições baseadas em Red Hat (Fedora, CentOS, RHEL). O `dnf` é a versão mais moderna do `yum`.

**Comandos principais:**

- **Atualizar todo o sistema:** `sudo dnf update`
- **Instalar pacotes:** `sudo dnf install <nome_do_pacote>`
- **Remover pacotes:** `sudo dnf remove <nome_do_pacote>`
- **Pesquisar pacotes:** `dnf search <termo>`

---

## 2. Variáveis de Ambiente e Configuração de Shell

As variáveis de ambiente controlam o comportamento do shell e das ferramentas de EDA (Electronic Design Automation).

### 2.1 Variáveis Importantes

- **PATH:** Define onde o sistema busca por executáveis. Se uma ferramenta não está no PATH, você precisa digitar o caminho completo
- **HOME:** Diretório pessoal do usuário
- **LD_LIBRARY_PATH:** Caminho para bibliotecas compartilhadas, frequentemente necessário para ferramentas de FPGA e simulação

### 2.2 Arquivos de Configuração (.bashrc vs .profile)

Para tornar as configurações persistentes, edite os arquivos no seu diretório `home` (`~`):

#### `.bashrc`

- Executado toda vez que um novo terminal interativo é aberto
- Ideal para: `aliases`, funções e variáveis usadas frequentemente no terminal
- *Exemplo:* Criar um atalho `alias ll='ls -alF'`

#### `.profile`

- Executado apenas uma vez, no login do usuário
- Ideal para: Configurações globais como adicionar diretórios ao `PATH`

**Como aplicar uma mudança imediatamente:**

Execute `source ~/.bashrc` após editar o arquivo.

---

## 3. Logs e Depuração (Debugging)

Saber onde encontrar e como ler logs é crucial para resolver falhas no ambiente de desenvolvimento.

### 3.1 Onde encontrar os Logs (`/var/log`)

- **`syslog`** ou **`messages`**: Logs gerais do sistema e kernel
- **`auth.log`**: Registros de autenticação e tentativas de login (segurança)
- **`dmesg`**: Mensagens do buffer do kernel, útil para verificar detecção de hardware (ex: ao conectar uma placa FPGA via USB)

### 3.2 Ferramentas de Análise

- **`tail -f arquivo.log`**: Monitora o arquivo em tempo real. Essencial para ver erros acontecendo no momento da execução
- **`grep "erro" arquivo.log`**: Filtra o arquivo buscando apenas linhas que contenham a palavra "erro"
- **`journalctl`**: Ferramenta para logs do sistema moderno (`systemd`)
  - `journalctl -f`: Monitoramento em tempo real (similar ao tail)
  - `journalctl -u ssh`: Logs específicos do serviço SSH

### 3.3 Estratégias de Depuração

1. **Ler o erro:** A mensagem no terminal geralmente aponta a causa
2. **Verificar Logs:** Use o `tail -f` em logs do sistema enquanto reproduz o erro
3. **Permissões:** Verifique se você tem permissão de acesso ao arquivo ou dispositivo (`ls -l`)
4. **Recursos:** Use `top` ou `htop` para ver se há falta de memória ou uso excessivo de CPU

---

## 4. Makefiles em Microeletrônica

Makefiles automatizam o processo de compilação e implementação, garantindo consistência.

### 4.1 Estrutura Básica

Um Makefile consiste em "alvos" (targets), suas dependências e os comandos para criá-los.

> **Importante:** O recuo dos comandos deve ser feito com **TAB**, não espaços.

```makefile
alvo: dependencias
	comando_para_criar_alvo
```

### 4.2 Aplicação em Projetos HDL (Exemplo Prático)

Em projetos de FPGA (ex: Verilog), um Makefile pode gerenciar todo o fluxo:

- **`simulate`**: Compila os arquivos `.v` e executa o testbench
- **`synth`**: Executa a síntese lógica (transforma código em portas lógicas)
- **`bitstream`**: Realiza o Place & Route e gera o arquivo binário para o hardware
- **`clean`**: Remove arquivos temporários gerados pelas ferramentas

-----

## 5. Git para Versionamento de Projetos HDL

O Git permite rastrear alterações, criar ramificações para testes (branches) e colaborar em equipe.

### 5.1 Comandos Essenciais

- **`git init`**: Inicia um repositório no diretório atual
- **`git status`**: Mostra o estado dos arquivos (modificados, novos, etc.)
- **`git add .`**: Adiciona alterações ao índice (staging area)
- **`git commit -m "mensagem"`**: Salva as alterações no histórico
- **`git branch <nome>`**: Cria uma nova ramificação para desenvolvimento paralelo
- **`git checkout <nome>`**: Alterna entre ramificações
- **`git merge <nome>`**: Funde as alterações de uma ramificação na atual

### 5.2 Boas Práticas para HDL (.gitignore)

Projetos de hardware geram muitos arquivos temporários e binários grandes que não devem ir para o repositório. Use um arquivo `.gitignore` para excluí-los:

**Exemplo de `.gitignore` para FPGA:**

```text
*.log       # Logs de simulação
*.bin       # Arquivos binários
*.bit       # Bitstreams finais
*.rpt       # Relatórios de síntese
*.jou       # Arquivos de journal (comuns em ferramentas Xilinx/Vivado)
build/      # Diretórios de build
```

---

**Fonte:** CI Amazônia - Capacitação em Microeletrônica - Unidade 01: Ferramentas Avançadas e Gerenciamento de Projetos Linux para Microeletrônica.