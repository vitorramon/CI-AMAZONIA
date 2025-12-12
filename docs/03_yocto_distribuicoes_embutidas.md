# Construindo Distribuições Embutidas com Yocto: do Zero ao Deploy

Este documento serve como guia prático sobre o **Yocto Project**, um framework colaborativo open-source para criar sistemas baseados em Linux customizados para sistemas embarcados.

## 1. Introdução e Motivação

Diferente de distribuições genéricas (como Ubuntu ou Fedora), o Yocto permite construir um sistema operacional "do zero", contendo apenas os pacotes necessários para o projeto.

### 1.1 Vantagens

- **Minimalismo:** Otimização de tamanho e performance
- **Customização:** Controle total sobre pacotes e patches de kernel
- **Reprodutibilidade:** Capacidade de recriar o mesmo sistema exato repetidamente

### 1.2 Arquitetura Principal

1. **BitBake:** A ferramenta de automação de build (semelhante ao `make`, mas mais poderoso). Ele interpreta as receitas (recipes) para construir desde o compilador (cross-compiler) até a imagem final
2. **Poky:** O sistema de referência do Yocto. Inclui o BitBake, metadados padrão e configurações base. É o ponto de partida da maioria dos projetos
3. **Layers (Camadas):** Coleções modulares de metadados. Permitem organizar e reutilizar configurações (ex: uma layer para o BSP da placa, outra para a aplicação gráfica)

---

## 2. Setup do Ambiente de Build

### 2.1 Passo 1: Instalar Dependências

O host de compilação precisa de ferramentas específicas.

- **Debian/Ubuntu:** `gawk`, `wget`, `git-core`, `diffstat`, `unzip`, `texinfo`, `gcc-multilib`, `build-essential`, `python3`, etc.
- **Fedora/CentOS:** `gawk`, `make`, `tar`, `bzip2`, `python3`, `perl`, `patch`, etc.

### 2.2 Passo 2: Clonar o Poky

Obtenha o código fonte do projeto e selecione a versão desejada (branch):

```bash
git clone git://git.yoctoproject.org/poky
cd poky
git checkout -b meu-branch yocto-4.0 # Exemplo de versão
```

### 2.3 Passo 3: Inicializar o Ambiente

Este comando cria o diretório de build e configura as variáveis de ambiente necessárias:

```bash
source oe-init-build-env
```

-----

## 3. Criação de Camadas (Layers) e Recipes

A melhor prática é criar uma camada própria para suas customizações, separando-as do núcleo do Poky.

### 3.1 Estrutura da Layer

```bash
bitbake-layers create-layer meta-meuprojeto
# Ou manualmente criando a estrutura de pastas:
# meta-meuprojeto/
# ├── conf/layer.conf
# ├── recipes-core/
# ├── recipes-images/
# └── recipes-kernel/
```

### 3.2 Recipes (.bb)

Arquivos que contêm instruções para construir um pacote: onde baixar o código, licenças, dependências, compilação e instalação.

**Exemplo de Recipe (Hello World em C):**

```bitbake
DESCRIPTION = "Programa simples Hello Yocto"
LICENSE = "MIT"
SRC_URI = "file://helloworld.c"

S = "${WORKDIR}"

do_compile() {
    ${CC} helloworld.c -o helloworld
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 helloworld ${D}${bindir}
}
```

### 3.3 Recipes de Imagem

Definem o que vai no sistema de arquivos final (Rootfs).

```bitbake
DESCRIPTION = "Minha imagem minimalista"
IMAGE_FEATURES += "ssh-server-dropbear"
IMAGE_INSTALL += "packagegroup-core-boot hello-world kernel-image"
```

-----

## 4. Customização do Kernel e Rootfs

### 4.1 Kernel (.bbappend)

Para modificar o kernel sem alterar a receita original, usa-se arquivos `.bbappend` na sua camada personalizada.

- Útil para adicionar patches (`SRC_URI += "file://fix.patch"`)
- Útil para alterar configurações do `.config`

### 4.2 Rootfs

Configurações finais do sistema de arquivos podem ser feitas via:

- Adição de pacotes na variável `IMAGE_INSTALL`
- Scripts de pós-processamento (`ROOTFS_POSTPROCESS_COMMAND`) para criar usuários ou arquivos de configuração específicos

-----

## 5. Construção e Deploy

### 5.1 Compilação

1. Adicione sua layer ao arquivo `conf/bblayers.conf`
2. Execute o BitBake:

```bash
bitbake minha-imagem
```

> **Nota:** A primeira compilação é demorada pois constrói todo o toolchain. As subsequentes usam cache.

### 5.2 Deploy e Testes

#### Emulação (QEMU)

Para testar sem hardware físico:

```bash
runqemu qemux86-64
```

#### Hardware Real (Raspberry Pi)

Gravação da imagem em cartão SD usando `dd`:

> **⚠️ CUIDADO:** O comando `dd` pode apagar dados do disco errado se o caminho de saída (`of=`) estiver incorreto. Verifique com `lsblk`.

```bash
sudo dd if=build/tmp/deploy/images/raspberrypi4/imagem.wic.bz2 of=/dev/sdX bs=4M status=progress
```

### 5.3 Depuração (Debug)

- **Logs do Kernel:** Utilize o comando `dmesg` na placa
- **Logs do Sistema:** Verifique `/var/log`
- **GDB Remoto:** O Yocto permite conectar o GDB do seu PC host à aplicação rodando na placa embarcada para debugar código passo-a-passo

---

## 6. Dicas de Trouble

### 6.1 Comandos para Setup Inicial

```bash
# 1- Atualizar repositórios
sudo apt update

# 2- Instalar dependências necessárias
sudo apt install -y gawk wget git diffstat unzip texinfo chrpath socat cpio python3 python3-pip python3-pexpect sed cvs subversion coreutils texi2html docbook-utils python3-git build-essential screen pax gzip libsdl1.2-dev xterm locales

# 3- Clonar o repositório Poky
git clone git://git.yoctoproject.org/poky

# 4- Entrar no diretório
cd poky

# 5- Fazer checkout da branch scarthgap
git checkout -b scarthgap origin/scarthgap

# 6- Inicializar ambiente de build
source oe-init-build-env

# 7- Voltar ao diretório anterior
cd ..

# 8- Criar nova layer personalizada
yocto-layer create minhacamada

# 9- Entrar no diretório da layer
cd meta-minhacamada

# 10- Editar configuração da layer (conferir se está tudo correto)
gedit conf/layer.conf

# 11- Criar estrutura de diretórios para receitas de imagem
mkdir -p recipes-images/minha-imagem/

# 12- Criar arquivo de receita da imagem
gedit recipes-images/minha-imagem/minha-imagem.bb

# 13- Verificar versão do kernel disponível
bitbake -e virtual/kernel | grep "^PV="

# 14- Criar arquivo de configuração do kernel
gedit recipes-kernel/linux/linux-yocto_6.6.bbappend

# 15- Criar diretório para arquivos de configuração
mkdir files

# 16- Navegar para o diretório e editar configuração do kernel
cd files && gedit meukernel.cfg

# 17- Compilar a imagem
bitbake minha-imagem
```

### 6.2 Observações Importantes

- Certifique-se de que todas as dependências estão instaladas antes de iniciar
- A primeira compilação pode levar várias horas dependendo do hardware
- Use a branch `scarthgap` que é uma versão estável do Yocto
- Sempre verifique a versão do kernel antes de criar o `.bbappend`
- O arquivo `meukernel.cfg` deve conter as configurações específicas do kernel que você deseja customizar

---

**Fonte:** CI Amazônia - Capacitação em Microeletrônica - Unidade 01: Construindo Distribuições Embutidas com Yocto: do Zero ao Deploy.