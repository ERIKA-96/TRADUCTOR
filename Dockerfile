```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Crear carpeta para la página web
RUN mkdir -p /app/site

# Copiar archivos de la página
COPY index.html /app/site/
COPY equipo.html /app/site/
COPY informativa.html /app/site/
COPY estilos.css /app/site/
COPY script.js /app/site/

# Copiar imágenes y audios
COPY IMAGENES /app/site/IMAGENES
COPY AUDIOS /app/site/AUDIOS
COPY img /app/site/img

# Copiar backend
COPY backend /app/backend

# Compilar Java
WORKDIR /app/backend

RUN javac -cp "postgresql-42.7.13.jar" Conexion.java Servidor.java

# Iniciar servidor
CMD ["java", "-cp", ".:postgresql-42.7.13.jar", "Servidor"]
```
