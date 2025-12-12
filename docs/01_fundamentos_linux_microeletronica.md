# Fundamentos Essenciais de Linux para Microeletrônica

Este documento resume os conceitos fundamentais do sistema operacional Linux aplicados ao contexto da microeletrônica, abrangendo desde a estrutura básica até automação de tarefas.

## 1. Introdução e Relevância
A microeletrônica beneficia-se de sistemas operacionais robustos devido à complexidade crescente dos circuitos e à necessidade de controle preciso. O Linux é uma escolha popular para desenvolvimento, depuração e controle de dispositivos embarcados devido à sua natureza de código aberto.

### Por que usar Linux na Microeletrônica?
* **Controle Total:** Permite acesso de baixo nível ao hardware (registradores, portas de E/S).
* **Flexibilidade e Código Aberto:** O sistema pode ser otimizado para remover componentes desnecessários, reduzindo tamanho e consumo de recursos.
* **Ecossistema de Ferramentas:** Suporte nativo a compiladores (GCC), depuradores (GDB) e ferramentas de simulação.
* **Suporte a Arquiteturas:** Compatível com ARM, MIPS, RISC-V, entre outras.

> **Nota:** Embora o Linux seja robusto, projetos com recursos muito limitados podem exigir um Sistema Operacional em Tempo Real (RTOS).

---

## 2. Estrutura de Diretórios e Permissões

O Linux organiza arquivos a partir de um diretório raiz (`/`), sem o uso de letras de drive como no Windows.

### Estrutura de Diretórios Comuns
* `/`: Diretório Raiz.
* `/bin`: Comandos binários essenciais (ex: ls, cp).
* `/sbin`: Binários de sistema para o administrador (root).
* `/etc`: Arquivos de configuração do sistema.
* `/home`: Diretórios pessoais dos usuários.
* `/dev`: Arquivos que representam dispositivos de hardware.
* `/var`: Arquivos variáveis como logs e caches.

### Permissões de Arquivos
Cada arquivo possui permissões para três categorias: **Proprietário** (User), **Grupo** (Group) e **Outros** (Others).
* **r (Read):** Leitura.
* **w (Write):** Escrita/Modificação.
* **x (Execute):** Execução.

---

## 3. Gerenciamento de Pacotes
As distribuições utilizam gerenciadores para instalar e atualizar softwares.

### APT (Debian/Ubuntu)
* Atualizar lista: `sudo apt update`.
* Atualizar pacotes instalados: `sudo apt upgrade`.
* Instalar: `sudo apt install nome_do_pacote`.

### YUM/DNF (Red Hat/CentOS)
* Atualizar sistema: `sudo yum update` ou `sudo dnf update`.
* Instalar: `sudo yum install nome_do_pacote`.

---

## 4. Comandos Essenciais do Terminal
O domínio do terminal é crucial em ambientes onde interfaces gráficas são limitadas.

### Navegação e Arquivos
* `ls`: Lista conteúdo (`ls -l` para detalhes, `ls -a` para ocultos).
* `cd`: Muda de diretório (`cd ..` volta um nível).
* `pwd`: Exibe o caminho atual.
* `mkdir`: Cria diretórios.
* `rm`: Remove arquivos (`rm -r` para diretórios).
* `cp`: Copia arquivos ou diretórios (`cp -r`).
* `mv`: Move ou renomeia arquivos.

### Manipulação e Sistema
* `grep`: Busca texto em arquivos (ex: `grep "erro" log.txt`).
* `find`: Busca arquivos por critérios (ex: `find -name "*.c"`).
* `chmod`: Altera permissões (ex: `chmod +x script.sh`).
* `df -h`: Exibe espaço em disco legível.
* `ps aux`: Lista processos em execução.
* `top` / `htop`: Monitoramento de recursos em tempo real.

---

## 5. Edição de Arquivos
A edição de código e configuração é diária no desenvolvimento microeletrônico.

* **nano:** Simples e intuitivo, ideal para edições rápidas no terminal.
* **vim:** Poderoso e configurável, opera em modos (Normal, Inserção, Visual). Essencial para servidores sem interface.
    * Salvar e sair: `:wq`.
    * Sair sem salvar: `:q!`.
* **gedit:** Editor gráfico padrão do GNOME, fácil de usar.

---

## 6. Scripts em Bash
Scripts permitem automatizar tarefas como compilação, flash de firmware e testes.

### Passos para criar um script:
1.  Criar arquivo (ex: `nano script.sh`).
2.  Incluir o "shebang" no início: `#!/bin/bash`.
3.  Adicionar comandos.
4.  Dar permissão de execução: `chmod +x script.sh`.
5.  Executar: `./script.sh`.

Scripts suportam variáveis, condicionais (`if/else`) e loops, tornando-os ferramentas poderosas.

---

## 7. Redirecionamento, Pipes e Automação

### Redirecionamento
* `>`: Redireciona a saída para um arquivo, sobrescrevendo-o.
* `>>`: Anexa a saída ao final de um arquivo existente.
* `2>`: Redireciona a saída de erro.

### Pipes (`|`)
Conectam a saída de um comando à entrada de outro.
* Exemplo: `cat log.txt | grep "erro"` (Exibe o log e filtra por "erro").

### Automação com Cron
O serviço `cron` agenda a execução de scripts em intervalos regulares.
* Comando para editar: `crontab -e`.
* Sintaxe: `minuto hora dia_mes mes dia_semana comando`.
* Exemplo: `*/5 * * * * script.sh` (Executa a cada 5 minutos).

---
**Fonte:** CI Amazônia - Capacitação em Microeletrônica - Unidade 01: Fundamentos Essenciais de Linux para Microeletrônica.