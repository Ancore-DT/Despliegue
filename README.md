# Sistema de Gestión Empresarial

## Descripción

Aplicación web completa para la **gestión empresarial**, que permite administrar empleados, proyectos y tareas. Desarrollada con **Spring Boot 3.5.6** utilizando una arquitectura híbrida de bases de datos: **MySQL** para la gestión de empleados y usuarios, y **MongoDB Atlas** para la gestión de proyectos y tareas embebidas.

El sistema incluye:
- **API REST completa** con 22 endpoints documentados
- **Interfaz web moderna** con Thymeleaf y Bootstrap 5
- **Sistema de autenticación y autorización** con Spring Security
- **Validaciones robustas** del lado del servidor
- **Tema visual personalizado** en púrpura y negro con gradientes modernos
- **Exportación de datos** a Excel y PDF

---

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instaladas las siguientes tecnologías:

| Tecnología | Versión Mínima | Descripción |
|------------|----------------|-------------|
| **Java** | 21 | JDK para ejecutar la aplicación |
| **Maven** | 3.8+ | Gestor de dependencias |
| **MySQL** | 8.0+ | Base de datos relacional |
| **MongoDB Atlas** | - | Base de datos NoSQL en la nube |
| **Git** | 2.0+ | Control de versiones |

### Verificar instalaciones:

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar MySQL
mysql --version

# Verificar Git
git --version
```

---

## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Ancore-DT/gestion-empresarial.git
cd gestion-empresarial
```

### 2. Configurar Base de Datos MySQL

**Opción A: Crear base de datos manualmente**

```bash
# Conectar a MySQL
mysql -u root -p

# Ejecutar en consola MySQL
CREATE DATABASE empresa CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
EXIT;
```

**Opción B: Importar script SQL completo**

```bash
# Desde la carpeta del proyecto
mysql -u root -p < empresa.sql
```

El script `empresa.sql` incluye:
- Estructura completa de tablas (usuarios, empleados, roles)
- Usuarios predefinidos con contraseñas encriptadas
- Datos de ejemplo

### 3. Configurar Variables de Entorno

Edita el archivo `src/main/resources/application.properties` con tus credenciales:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/empresa?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_MYSQL

# MongoDB Atlas Configuration
spring.data.mongodb.uri=mongodb+srv://TU_USUARIO:TU_PASSWORD@cluster.mongodb.net/?retryWrites=true&w=majority
spring.data.mongodb.database=empresa
```

**O configura variables de entorno del sistema:**

```bash
# Windows PowerShell
$env:MYSQL_URL="jdbc:mysql://localhost:3306/empresa"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="tu_password"
$env:MONGO_URI="mongodb+srv://usuario:password@cluster.mongodb.net/empresa"

# Linux/Mac
export MYSQL_URL="jdbc:mysql://localhost:3306/empresa"
export MYSQL_USER="root"
export MYSQL_PASSWORD="tu_password"
export MONGO_URI="mongodb+srv://usuario:password@cluster.mongodb.net/empresa"
```

---

## Ejecución

### Método 1: Maven Wrapper (Recomendado)

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Método 2: Maven Instalado

```bash
mvn spring-boot:run
```

### Método 3: JAR Ejecutable

```bash
# Compilar el proyecto
mvn clean package

# Ejecutar el JAR generado
java -jar target/empresa-0.0.1-SNAPSHOT.jar
```

### Método 4: Script de Inicio Rápido (Windows)

```bash
# Ejecutar el archivo batch
.\iniciar.bat
```

---

## Pruebas

### Verificar que la aplicación está funcionando:

#### 1. Interfaz Web
Abre tu navegador y visita:
- **Página principal**: http://localhost:8080
- **Login**: http://localhost:8080/login
- **Panel de administración**: http://localhost:8080/admin/panel

#### 2. API REST

**Probar con cURL:**

```bash
# Login (obtener cookie de sesión)
curl -i -c cookies.txt -X POST http://localhost:8080/login ^
  -d "username=admin&password=admin123"

# Listar empleados
curl -b cookies.txt http://localhost:8080/api/empleados

# Listar proyectos
curl -b cookies.txt http://localhost:8080/api/proyectos

# Estadísticas de proyectos
curl -b cookies.txt http://localhost:8080/api/proyectos/estadisticas
```

**Probar con Postman/Thunder Client:**

Importa la colección incluida:
- **Archivo**: `Empresa_API_Collection_v2.1.postman.json`
- **Endpoints**: 22 endpoints listos para probar

#### 3. Endpoints de Prueba

| Endpoint | Descripción | Método |
|----------|-------------|--------|
| `http://localhost:8080/api/empleados` | Lista todos los empleados | GET |
| `http://localhost:8080/api/proyectos` | Lista todos los proyectos | GET |
| `http://localhost:8080/api/proyectos/estadisticas` | Obtiene estadísticas | GET |
| `http://localhost:8080/empleados` | Interfaz web de empleados | GET |
| `http://localhost:8080/proyectos` | Interfaz web de proyectos | GET |

---

## Configuración

### Archivo `application.properties`

El archivo principal de configuración se encuentra en:
```
src/main/resources/application.properties
```

### Configuraciones Principales:

#### **Base de Datos MySQL**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/empresa?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

#### **Base de Datos MongoDB Atlas**
```properties
spring.data.mongodb.uri=mongodb+srv://empresa:Sena2025@empresa.czeymou.mongodb.net/?retryWrites=true&w=majority&appName=empresa
spring.data.mongodb.database=empresa
```

#### **Servidor**
```properties
server.port=8080
server.error.include-message=always
```

#### **Seguridad**
```properties
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=ADMIN
```

### Credenciales de Acceso Predefinidas:

| Usuario | Contraseña | Rol | Email |
|---------|------------|-----|-------|
| **admin** | admin123 | ADMIN | admin@empresa.com |
| **user** | user123 | USER | user@empresa.com |
| **ConejoDC** | password123 | ADMIN | conejoDC@empresa.com |

---

## Tecnologías Utilizadas

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Capa de persistencia para MySQL
- **Spring Data MongoDB** - Capa de persistencia para MongoDB
- **Spring Security** - Autenticación y autorización
- **Spring Web** - API REST
- **Jakarta Validation** - Validaciones del lado del servidor

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **CSS personalizado** - Tema púrpura y negro con gradientes

### Bases de Datos
- **MySQL 8.0+** - Base de datos relacional (Empleados, Usuarios)
- **MongoDB Atlas** - Base de datos NoSQL (Proyectos, Tareas)

### Herramientas de Desarrollo
- **Maven** - Gestor de dependencias y build
- **Apache POI** - Exportación a Excel
- **PDFBox** - Exportación a PDF
- **Spring Boot DevTools** - Recarga automática en desarrollo

### Testing & API
- **Postman/Thunder Client** - Colección de pruebas API
- **Spring Boot Actuator** - Métricas y monitoreo

---

## Estructura del Proyecto

```
empresa/
├── src/
│   ├── main/
│   │   ├── java/com/empresa/empresa/
│   │   │   ├── EmpresaApplication.java           # Clase principal
│   │   │   ├── config/                           # Configuraciones
│   │   │   │   ├── SecurityConfig.java           # Seguridad
│   │   │   │   ├── DataInitializer.java          # Datos iniciales
│   │   │   │   └── PasswordConfig.java           # Encriptación
│   │   │   ├── controller/                       # Controladores MVC
│   │   │   │   ├── EmpleadoController.java
│   │   │   │   ├── ProyectoController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   └── api/                          # API REST
│   │   │   │       ├── EmpleadoRestController.java
│   │   │   │       └── ProyectoRestController.java
│   │   │   ├── entity/                           # Entidades JPA/MongoDB
│   │   │   │   ├── Empleado.java                 # MySQL
│   │   │   │   ├── Usuario.java                  # MySQL
│   │   │   │   ├── Proyecto.java                 # MongoDB
│   │   │   │   └── Tarea.java                    # MongoDB (embebida)
│   │   │   ├── repository/                       # Repositorios
│   │   │   │   ├── EmpleadoRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── ProyectoRepository.java
│   │   │   └── service/                          # Lógica de negocio
│   │   │       ├── EmpleadoService.java
│   │   │       ├── UsuarioService.java
│   │   │       └── ProyectoService.java
│   │   └── resources/
│   │       ├── application.properties            # Configuración
│   │       ├── static/                           # Recursos estáticos
│   │       │   └── css/
│   │       │       └── styles.css                # Estilos personalizados
│   │       └── templates/                        # Vistas Thymeleaf
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── empleados/
│   │           │   ├── lista.html
│   │           │   ├── formulario.html
│   │           │   └── detalle.html
│   │           ├── proyectos/
│   │           │   ├── lista.html
│   │           │   ├── formulario.html
│   │           │   └── detalle.html
│   │           └── admin/
│   │               ├── panel.html
│   │               └── usuarios.html
│   └── test/                                     # Tests unitarios
├── pom.xml                                       # Dependencias Maven
├── empresa.sql                                   # Script de base de datos
├── iniciar.bat                                   # Script de inicio Windows
├── mvnw                                          # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                      # Maven Wrapper (Windows)
├── README.md                                     # Este archivo
├── REST_API_DOCUMENTATION.md                     # Documentación API
├── API_IMPLEMENTATION_SUMMARY.md                 # Resumen implementación
└── Empresa_API_Collection_v2.1.postman.json      # Colección Postman
```

---

## Documentación Adicional

| Documento | Descripción |
|-----------|-------------|
| [REST_API_DOCUMENTATION.md](REST_API_DOCUMENTATION.md) | Documentación completa de la API REST (22 endpoints) |
| [API_IMPLEMENTATION_SUMMARY.md](API_IMPLEMENTATION_SUMMARY.md) | Resumen de la implementación técnica |
| [GUIA_EJECUCION_PRUEBAS.md](GUIA_EJECUCION_PRUEBAS.md) | Guía para ejecutar pruebas con Postman/cURL |
| [GUIA_USO_POSTMAN.md](GUIA_USO_POSTMAN.md) | Tutorial de uso de Postman |

---

## Características Visuales

### Tema Personalizado
- **Colores principales**: Púrpura (#7b1fa2) y Negro (#1a1a1a)
- **Degradados modernos** en tarjetas y botones
- **Botones codificados por color**:
  - **Rojo** - Eliminar
  - **Amarillo** - Editar
  - **Azul** - Ver
- **Efectos hover** con elevación y sombras
- **Diseño responsive** para móviles y tablets

---

## API REST Endpoints

### Empleados (9 endpoints)
```
GET    /api/empleados              - Listar todos
GET    /api/empleados/{id}         - Obtener por ID
POST   /api/empleados              - Crear nuevo
PUT    /api/empleados/{id}         - Actualizar
DELETE /api/empleados/{id}         - Eliminar
GET    /api/empleados/buscar       - Buscar por término
GET    /api/empleados/cargo/{cargo} - Filtrar por cargo
PATCH  /api/empleados/{id}/salario - Actualizar salario
GET    /api/empleados/email/{email}/existe - Verificar email
```

### Proyectos (9 endpoints)
```
GET    /api/proyectos              - Listar todos
GET    /api/proyectos/{id}         - Obtener por ID
POST   /api/proyectos              - Crear nuevo
PUT    /api/proyectos/{id}         - Actualizar
DELETE /api/proyectos/{id}         - Eliminar
GET    /api/proyectos/buscar       - Buscar por nombre
GET    /api/proyectos/empleado/{id} - Proyectos de empleado
PATCH  /api/proyectos/{id}/estado  - Actualizar estado
GET    /api/proyectos/estadisticas - Estadísticas generales
```

### Tareas (4 endpoints)
```
GET    /api/proyectos/{id}/tareas          - Listar tareas
POST   /api/proyectos/{id}/tareas          - Agregar tarea
PATCH  /api/proyectos/{id}/tareas/{tareaId}/completar - Completar
DELETE /api/proyectos/{id}/tareas/{tareaId} - Eliminar
```

**Total:** 22 endpoints funcionales

---

## Autor

### **Andrés Tejada**
- GitHub: [@Ancore-DT](https://github.com/Ancore-DT)
- Empresa: INNOVA MANAGER
- Año: 2025

---

## Licencia

Este proyecto está licenciado bajo la **Licencia MIT** - ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2025 Andrés Tejada

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Contacto

Para dudas, sugerencias o colaboraciones:

- **Email**: Kuachotejada@gmail.com
- **GitHub**: [@Ancore-DT](https://github.com/Ancore-DT)

---

## Estado del Proyecto

**COMPLETADO Y FUNCIONAL**

- Backend completo con Spring Boot
- API REST con 22 endpoints
- Interfaz web responsive
- Autenticación y autorización
- Base de datos híbrida (MySQL + MongoDB)
- Validaciones robustas
- Documentación completa
- Colección de pruebas

**Versión actual:** 1.0.0  
**Última actualización:** Octubre 2025

---

## Próximas Mejoras

- [ ] Implementar paginación en listados
- [ ] Agregar filtros avanzados
- [ ] Implementar JWT para autenticación
- [ ] Agregar tests unitarios e integración
- [ ] Documentación con Swagger/OpenAPI
- [ ] Implementar caché con Redis
- [ ] Agregar WebSockets para notificaciones en tiempo real
- [ ] Exportación de reportes a PDF y Excel
- [ ] Dashboard con estadísticas y gráficos

---

**Desarrollado por [Andrés Tejada](https://github.com/Ancore-DT)**
