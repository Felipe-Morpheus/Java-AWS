# Define a imagem base a ser usada, que contém o OpenJDK 17
FROM openjdk:17

# Define um volume '/tmp' no contêiner
VOLUME /tmp

# Define o argumento 'DEPENDENCY' para o caminho dos arquivos de dependência da aplicação
ARG DEPENDENCY=target/dependency

# Copia os arquivos de dependência da aplicação para o diretório '/app/lib' no contêiner
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib

# Copia os arquivos do diretório 'META-INF' da aplicação para o diretório '/app/META-INF' no contêiner
COPY ${DEPENDENCY}/META-INF /app/META-INF

# Copia os arquivos de classes da aplicação para o diretório '/app' no contêiner
COPY ${DEPENDENCY}/BOOT-INF/classes /app

# Define o ponto de entrada (entrypoint) do contêiner, que é o comando para iniciar a aplicação
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "br.com.vulpicula.aws_project01.AwsProject01Application"]
