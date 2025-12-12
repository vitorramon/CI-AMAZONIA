# CI-AMAZONIA

## Sobre o Projeto

Repositório dedicado à capacitação em microeletrônica, abrangendo conceitos fundamentais, projetos práticos e desenvolvimento de habilidades na área.

## Objetivos

- Fornecer material didático sobre microeletrônica
- Desenvolver projetos práticos na área
- Capacitar profissionais e estudantes em tecnologias de microeletrônica

## Conteúdo

Este repositório contém recursos e materiais relacionados a:

- Fundamentos de Linux para microeletrônica
- Ferramentas avançadas e gerenciamento de projetos
- Distribuições embarcadas com Yocto Project
- Projetos práticos de sistemas embarcados

## Documentação

- [01 - Fundamentos Linux para Microeletrônica](docs/01_fundamentos_linux_microeletronica.md)
- [02 - Ferramentas Avançadas Linux](docs/02_ferramentas_avancadas_linux_microeletronica.md)
- [03 - Yocto: Distribuições Embutidas](docs/03_yocto_distribuicoes_embutidas.md)
- [04 - Tutorial Completo Yocto](docs/04_tutorial_completo_yocto_projeto.md)

## Setup do Projeto Yocto

### Pré-requisitos

```bash
sudo apt update
sudo apt install -y gawk wget git diffstat unzip texinfo chrpath socat cpio \
python3 python3-pip python3-pexpect sed cvs subversion coreutils texi2html \
docbook-utils python3-git build-essential screen pax gzip libsdl1.2-dev \
xterm locales lz4 zstd
```

### Clonar o Poky

```bash
cd CI-AMAZONIA
git clone git://git.yoctoproject.org/poky
cd poky
git checkout -b scarthgap origin/scarthgap
```

### Copiar a Layer Personalizada

A layer `meta-meuprojeto` está incluída neste repositório. Após clonar o Poky, copie a layer para dentro:

```bash
# Se estiver no diretório poky
cp -r ../poky/meta-meuprojeto ./
```

### Compilar a Imagem

```bash
source oe-init-build-env
# Adicione a layer ao build/conf/bblayers.conf conforme documentação
bitbake minha-imagem
```

Para mais detalhes, consulte o [Tutorial Completo](docs/04_tutorial_completo_yocto_projeto.md).

## Como Contribuir

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

## Licença

Este projeto está sob licença MIT.

## Contato

Para mais informações, entre em contato através do GitHub.

