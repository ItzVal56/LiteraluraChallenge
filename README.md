# 📚 LiterAlura

Una aplicación de consola desarrollada con **Java + Spring Boot + PostgreSQL** que permite consultar libros desde la API de Gutendex y almacenarlos localmente. Además, ofrece funciones para listar libros, autores y obtener información útil sobre obras literarias.

---

## 🚀 Funcionalidades principales

🔎 **Buscar libro por título**  
Consulta un libro usando su título (o parte de él). Si no existe en la base de datos, se busca automáticamente en la API de [Gutendex](https://gutendex.com/) y se guarda.

📖 **Listar libros registrados**  
Muestra todos los libros que han sido guardados en la base de datos.

👤 **Listar autores registrados**  
Visualiza todos los autores y los libros asociados a cada uno.

🧓 **Listar autores vivos en determinado año**  
Filtra y muestra los autores que estaban vivos durante un año específico.

🌍 **Listar libros por idioma**  
Permite visualizar libros registrados según el idioma en que están escritos (por ejemplo, `en`, `es`, `fr`...).

🔥 **Ver top 10 libros más descargados**  
Consulta el ranking actualizado de los libros más populares según Gutendex.

---

## 🛠️ Tecnologías utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **WebClient (Reactive)**
- **Gutendex API (https://gutendex.com)**

---

## 📦 Estructura del proyecto

```bash
LiteraturaA/
├── src/main/java/com/alura/literatura/
│   ├── model/               # Entidades: Libro, Autor
│   ├── repository/          # Interfaces JpaRepository
│   ├── service/             # Lógica de negocio (LibroService, AutorService)
│   ├── DesafioApp.java      # Clase principal con menú en consola
│   └── LiteraturaController.java (opcional si se extiende a REST)
├── src/main/resources/
│   ├── application.properties
│   ├── application-local.properties  # (en .gitignore)
├── pom.xml
└── README.md
```
---
## ⚙️ Configuración local
1. Asegúrate de tener PostgreSQL corriendo y crea una base de datos llamada:

```bash
CREATE DATABASE literatura;
```
2. Crea un usuario y dale permisos
3. Configura tu archivo application-local.properties (NO lo subas a GitHub):
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/literatura
spring.datasource.username=<USER>
spring.datasource.password=<PASSWORD>
spring.jpa.hibernate.ddl-auto=update
```
4. Corre el proyecto desde tu IDE o con:
```bash
./mvnw spring-boot:run
```
---

## 🛡️ Seguridad
Este proyecto NO sube información sensible gracias al uso de .gitignore. El archivo application-local.properties está ignorado para proteger credenciales locales.

---

## ✨ Créditos
Este proyecto fue desarrollado como parte del Challenge Backend Java de Alura Latam con fines educativos y prácticos.

---

## 📷 Vista previa (modo consola)

```bash
===============================
       MENÚ LITERAlura
===============================
1 - Buscar libro por título
2 - Listar libros registrados
3 - Listar autores registrados
4 - Listar autores vivos en un determinado año
5 - Listar libros por idioma
6 - Ver top 10 libros más descargados
0 - Salir

```
---
### 📄 Licencia
Este proyecto es de uso libre con fines educativos. Puedes adaptarlo, compartirlo o extenderlo bajo tu criterio.
**Creado por Valeria López Arroyo.**


