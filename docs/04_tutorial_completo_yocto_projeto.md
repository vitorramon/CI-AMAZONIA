# Tutorial Completo: Construindo uma Distribuição Linux Customizada com Yocto

Este documento apresenta um passo a passo completo para criar uma distribuição Linux embarcada customizada usando o Yocto Project, desde a configuração inicial até a execução no QEMU.

## 1. Preparação do Ambiente

### 1.1 Atualizar o Sistema

```bash
sudo apt update
```

### 1.2 Instalar Dependências

```bash
sudo apt install -y gawk wget git diffstat unzip texinfo chrpath socat cpio \
python3 python3-pip python3-pexpect sed cvs subversion coreutils texi2html \
docbook-utils python3-git build-essential screen pax gzip libsdl1.2-dev \
xterm locales lz4 zstd
```

### 1.3 Configurar Locale

```bash
sudo locale-gen en_US.UTF-8
sudo update-locale LANG=en_US.UTF-8
```

## 2. Obter o Poky (Sistema de Referência do Yocto)

### 2.1 Clonar o Repositório

```bash
cd ~/CI-AMAZONIA
git clone git://git.yoctoproject.org/poky
cd poky
```

### 2.2 Selecionar a Branch (Versão)

```bash
git checkout -b scarthgap origin/scarthgap
```

## 3. Criar a Layer Personalizada

### 3.1 Estrutura de Diretórios

```bash
cd ~/CI-AMAZONIA/poky
mkdir -p meta-meuprojeto/conf
mkdir -p meta-meuprojeto/recipes-core/hello-world/files
mkdir -p meta-meuprojeto/recipes-images/minha-imagem
mkdir -p meta-meuprojeto/recipes-kernel/linux
```

### 3.2 Arquivo de Configuração da Layer

**Caminho:** `meta-meuprojeto/conf/layer.conf`

```bash
# We have a conf and classes directory, add to BBPATH
BBPATH .= ":${LAYERDIR}"

# We have recipes, add to BBFILES
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "meuprojeto"
BBFILE_PATTERN_meuprojeto = "^${LAYERDIR}/"
BBFILE_PRIORITY_meuprojeto = "6"

# This should only be added to a single layer where it is needed
LAYERSERIES_COMPAT_meuprojeto = "scarthgap"
```

## 4. Criar o Programa Hello World

### 4.1 Código Fonte C

**Caminho:** `meta-meuprojeto/recipes-core/hello-world/files/helloworld.c`

```c
#include <stdio.h>

int main(int argc, char **argv) {
    printf("Hello, Yocto!\n");
    return 0;
}
```

### 4.2 Receita do Hello World

**Caminho:** `meta-meuprojeto/recipes-core/hello-world/hello-world_1.0.bb`

```bash
DESCRIPTION = "A simple C program that prints 'Hello, Yocto!'"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://helloworld.c"
S = "${WORKDIR}"

do_compile() {
    ${CC} ${LDFLAGS} helloworld.c -o helloworld
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 helloworld ${D}${bindir}
}
```

**Pontos importantes:**
- `${CC}`: Compilador cross-compiler configurado pelo Yocto
- `${LDFLAGS}`: Flags de linker necessárias para passar no QA
- `${D}${bindir}`: Diretório de destino da instalação (/usr/bin)

## 5. Criar a Receita da Imagem

**Caminho:** `meta-meuprojeto/recipes-images/minha-imagem/minha-imagem.bb`

```bash
DESCRIPTION = "Minha imagem minimalista com Yocto."
LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "package-management ssh-server-dropbear"

# Inclua os pacotes que você quer na sua imagem
IMAGE_INSTALL += "packagegroup-core-boot \
                  hello-world \
                  ${CORE_IMAGE_EXTRA_INSTALL}"
```

**Explicação:**
- `inherit core-image`: Herda a classe base para criar imagens
- `IMAGE_FEATURES`: Funcionalidades adicionais (gerenciamento de pacotes, SSH)
- `IMAGE_INSTALL`: Pacotes a serem instalados na imagem

## 6. Customização do Kernel (Opcional)

**Caminho:** `meta-meuprojeto/recipes-kernel/linux/linux-yocto_%.bbappend`

```bash
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# SRC_URI += "file://meu-patch.patch"
```

**Nota:** O `%` no nome do arquivo faz o bbappend se aplicar a todas as versões do linux-yocto.

## 7. Configurar o Build

### 7.1 Inicializar o Ambiente

```bash
cd ~/CI-AMAZONIA/poky
source oe-init-build-env
```

Isso cria o diretório `build` e configura as variáveis de ambiente.

### 7.2 Adicionar a Layer Personalizada

**Caminho:** `build/conf/bblayers.conf`

Adicione a linha da sua layer:

```bash
BBLAYERS ?= " \
  /home/vitor/CI-AMAZONIA/poky/meta \
  /home/vitor/CI-AMAZONIA/poky/meta-poky \
  /home/vitor/CI-AMAZONIA/poky/meta-yocto-bsp \
  ${TOPDIR}/../meta-meuprojeto \
  "
```

### 7.3 Configurações Locais (Opcional)

**Caminho:** `build/conf/local.conf`

Você pode ajustar configurações como:
- `MACHINE`: Hardware alvo (padrão: qemux86-64)
- `DL_DIR`: Diretório para downloads
- `SSTATE_DIR`: Diretório para cache de estado compartilhado
- `PARALLEL_MAKE`: Número de jobs paralelos

## 8. Compilar a Imagem

### 8.1 Executar o BitBake

```bash
cd ~/CI-AMAZONIA/poky
source oe-init-build-env
bitbake minha-imagem
```

**Tempo estimado:** 2-6 horas na primeira compilação (depende do hardware)

### 8.2 Verificar Progresso

Durante a compilação você verá:
- Parsing de receitas
- Download de fontes
- Compilação de ferramentas nativas
- Cross-compilação
- Criação do rootfs
- Geração da imagem final

### 8.3 Solução de Problemas Comuns

#### Erro de Locale

```bash
sudo locale-gen en_US.UTF-8
```

#### Falta de Ferramentas

```bash
sudo apt install -y lz4 zstd
```

#### Erro de QA no Hello World

Certifique-se de usar `${LDFLAGS}` na compilação:
```bash
${CC} ${LDFLAGS} helloworld.c -o helloworld
```

## 9. Estrutura de Arquivos Gerados

Após a compilação bem-sucedida:

```
build/tmp/deploy/images/qemux86-64/
├── bzImage -> bzImage--6.6.111+git...bin          # Kernel Linux
├── minha-imagem-qemux86-64.rootfs.ext4           # Sistema de arquivos (301M)
├── minha-imagem-qemux86-64.rootfs.tar.bz2        # Imagem compactada (53M)
├── minha-imagem-qemux86-64.rootfs.manifest       # Lista de pacotes
├── minha-imagem-qemux86-64.rootfs.qemuboot.conf  # Configuração QEMU
└── modules-qemux86-64.tgz                        # Módulos do kernel
```

## 10. Executar no QEMU

### 10.1 Método 1: Rede SLIRP (Recomendado para WSL)

```bash
cd ~/CI-AMAZONIA/poky
source oe-init-build-env
runqemu qemux86-64 slirp nographic
```

### 10.2 Método 2: Com Interface Gráfica

```bash
runqemu qemux86-64
```

### 10.3 Login no Sistema

```
Login: root
Password: (vazio - apenas pressione Enter)
```

### 10.4 Testar o Hello World

```bash
root@qemux86-64:~# helloworld
Hello, Yocto!
```

### 10.5 Sair do QEMU

- Pressione `Ctrl+A` depois `X`
- Ou digite: `poweroff`

## 11. Comandos Úteis do BitBake

### 11.1 Limpar Estado de uma Receita

```bash
bitbake -c cleansstate hello-world
```

### 11.2 Forçar Recompilação

```bash
bitbake -c compile -f hello-world
```

### 11.3 Listar Tarefas Disponíveis

```bash
bitbake -c listtasks hello-world
```

### 11.4 Ver Variáveis de uma Receita

```bash
bitbake -e hello-world | grep ^WORKDIR=
```

### 11.5 Informações sobre Layers

```bash
bitbake-layers show-layers
bitbake-layers show-recipes
```

## 12. Estrutura Completa do Projeto

```
CI-AMAZONIA/
├── docs/
│   ├── 01_fundamentos_linux_microeletronica.md
│   ├── 02_ferramentas_avancadas_linux_microeletronica.md
│   ├── 03_yocto_distribuicoes_embutidas.md
│   └── 04_tutorial_completo_yocto_projeto.md
├── README.md
└── poky/
    ├── build/
    │   ├── conf/
    │   │   ├── bblayers.conf
    │   │   └── local.conf
    │   └── tmp/
    │       └── deploy/
    │           └── images/
    │               └── qemux86-64/
    ├── meta/
    ├── meta-poky/
    ├── meta-yocto-bsp/
    └── meta-meuprojeto/
        ├── conf/
        │   └── layer.conf
        ├── recipes-core/
        │   └── hello-world/
        │       ├── files/
        │       │   └── helloworld.c
        │       └── hello-world_1.0.bb
        ├── recipes-images/
        │   └── minha-imagem/
        │       └── minha-imagem.bb
        └── recipes-kernel/
            └── linux/
                └── linux-yocto_%.bbappend
```

## 13. Boas Práticas

### 13.1 Organização

- Uma layer para cada componente ou subsistema
- Nomenclatura clara para receitas e arquivos
- Documentação inline nos arquivos de receitas

### 13.2 Versionamento

- Use branches específicas do Yocto (scarthgap, kirkstone, etc.)
- Mantenha a layer compatível com múltiplas versões quando possível
- Use `LAYERSERIES_COMPAT` para especificar compatibilidade

### 13.3 Cache e Performance

- Configure `DL_DIR` e `SSTATE_DIR` para reutilizar entre builds
- Use `RM_WORK = "1"` para economizar espaço em disco
- Ajuste `BB_NUMBER_THREADS` e `PARALLEL_MAKE` conforme seu hardware

### 13.4 Segurança

- Sempre defina `LICENSE` e `LIC_FILES_CHKSUM`
- Mantenha pacotes atualizados
- Use `ssh-server-openssh` ao invés de dropbear em produção

## 14. Próximos Passos

### 14.1 Adicionar Mais Pacotes

Explore pacotes disponíveis:
```bash
bitbake-layers show-recipes | grep nome-pacote
```

### 14.2 Deploy em Hardware Real

- Raspberry Pi
- BeagleBone
- Placas customizadas

### 14.3 Criar BSP (Board Support Package)

Para suporte a hardware específico.

### 14.4 Integração Contínua

Configure CI/CD para builds automáticos do Yocto.

## 15. Recursos Adicionais

- **Documentação Oficial:** https://docs.yoctoproject.org
- **Layers Index:** https://layers.openembedded.org
- **Comunidade:** https://www.yoctoproject.org/community
- **IRC:** #yocto no Libera.Chat

---

**Conclusão**

Este tutorial cobriu todo o processo de criação de uma distribuição Linux customizada com Yocto, desde a instalação de dependências até a execução no QEMU. Com este conhecimento, você pode criar sistemas Linux embarcados otimizados para suas necessidades específicas.

**Autor:** CI Amazônia - Capacitação em Microeletrônica
**Data:** Dezembro 2025
**Versão:** 1.0
