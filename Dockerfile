FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY backend /app/backend

WORKDIR /app/backend

RUN javac -cp "postgresql-42.7.13.jar" Conexion.java Servidor.java

CMD ["java", "-cp", ".:postgresql-42.7.13.jar", "Servidor"]