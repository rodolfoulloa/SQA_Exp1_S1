# PixelMeet · SQA_Exp1_S1

Aplicación web segura para la comunidad gamer de Chile, desarrollada para la actividad formativa
**"Creando una aplicación web segura usando frameworks y librerías de seguridad"**
(ISY2202 · Seguridad y Calidad en el Desarrollo de Software · Duoc UC · Experiencia 1, Semana 1).

## Caso

La comunidad gamer crece y organiza cada vez más eventos. PixelMeet reúne la información de esos
eventos en un sitio amigable e intuitivo, donde los gamers pueden revisar lo que viene, registrarse,
armar su perfil y buscar eventos por categoría, fecha y ubicación.

## Stack

- Java 17
- Spring Boot 3.5 (Spring Web MVC)
- Spring Security 6 (autenticación por formulario, BCrypt, CSRF, cabeceras de seguridad)
- Thymeleaf + thymeleaf-extras-springsecurity6
- Bootstrap 5 (WebJar servido desde la propia app, sin CDN, para respetar la CSP)
- Bean Validation (Jakarta Validation)
- Maven (con Maven Wrapper)
- JUnit 5 + MockMvc + spring-security-test
- Docker / Docker Compose para el despliegue

En esta etapa los datos (eventos, usuarios registrados y perfiles) se guardan **en memoria**.
El código está separado en capas `model / repository / service / controller / config` para migrar
a una base de datos con JPA en las semanas siguientes.

## Ejecutar en local

Requisitos: JDK 17 o superior.

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Abrir: <http://localhost:8081/recetas>

Ejecutar las pruebas:

```bash
.\mvnw.cmd clean verify      # Windows
./mvnw clean verify          # Linux / macOS
```

## Usuarios de prueba

| Usuario  | Contraseña  | Roles       |
|----------|-------------|-------------|
| admin    | Admin123!   | USER, ADMIN |
| gamer1   | Gamer123!   | USER        |
| gamer2   | Gamer123!   | USER        |

Los usuarios que se registran en `/registro` inician sesión con su **correo** y contraseña.

## Rutas

Todas las rutas cuelgan del context path `/recetas` (configurable en `application.properties`).

| Ruta              | Acceso  | Descripción                                                            |
|-------------------|---------|------------------------------------------------------------------------|
| `/`, `/home`      | Pública | Próximos eventos, eventos destacados y publicidad de marcas aliadas    |
| `/registro`       | Pública | Registro con nombre, correo y contraseña                               |
| `/login`          | Pública | Inicio de sesión                                                       |
| `/eventos`        | Privada | Búsqueda de eventos por categoría, fecha (desde/hasta) y ubicación     |
| `/eventos/{id}`   | Privada | Detalle: descripción, dirección, fecha y hora, organizadores, servicios y expositores |
| `/perfil`         | Privada | Juegos de interés, avatar, nombre de usuario y opciones de notificación |
| `/logout` (POST)  | Privada | Cierre de sesión                                                       |

## Medidas de seguridad implementadas

- Rutas privadas protegidas con Spring Security; solo inicio, registro, login y recursos estáticos son públicos.
- Contraseñas cifradas con **BCrypt**.
- Protección **CSRF** activa en todos los formularios (token agregado por Thymeleaf).
- Cambio de ID de sesión al autenticarse (protección contra fijación de sesión).
- Logout por POST, invalidando la sesión y la cookie `JSESSIONID`.
- Cabeceras: `Content-Security-Policy` (solo recursos propios), `X-Frame-Options: DENY`,
  `X-Content-Type-Options: nosniff` y `Referrer-Policy`.
- Validación de entradas con Bean Validation y listas blancas (avatares, juegos, categorías).
- Salida escapada en las vistas (`th:text`, sin `th:utext`) para prevenir XSS.
- Errores sin trazas ni mensajes internos (`server.error.include-stacktrace=never`).
- El contenedor Docker ejecuta la aplicación con un usuario sin privilegios.

## Despliegue en la máquina virtual con Docker

```bash
git clone https://github.com/rodolfoulloa/SQA_Exp1_S1.git
cd SQA_Exp1_S1
docker compose up -d --build
```

La aplicación queda publicada en `http://[IP]/recetas` (el puerto 80 de la VM se mapea al 8081 del contenedor).
Recuerda abrir el puerto 80 en el firewall o en el grupo de seguridad de la VM.

## Estructura del proyecto

```
src/main/java/com/duoc/seguridadcalidad
├── SqaExp1S1Application.java
├── config/WebSecurityConfig.java      # Rutas públicas/privadas, login, usuarios, cabeceras
├── controller/                        # Home, Auth (login/registro), Evento, Perfil
├── model/                             # Evento, Perfil, RegistroForm, PerfilForm
├── repository/                        # Repositorios en memoria
└── service/                           # Lógica de eventos y usuarios
src/main/resources
├── application.properties
├── static/images/                     # Avatares del perfil (SVG)
└── templates/                         # Vistas Thymeleaf
src/test/java/...                      # Pruebas de seguridad de rutas y del servicio de eventos
Dockerfile, docker-compose.yml
```
