DESCRIPTION = "Minha imagem minimalista com Yocto."
LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "package-management ssh-server-dropbear"

# Inclua os pacotes que você quer na sua imagem
IMAGE_INSTALL += "packagegroup-core-boot \
                  hello-world \
                  ${CORE_IMAGE_EXTRA_INSTALL}"
