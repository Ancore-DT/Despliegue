# Sistema de Perfiles - MySQL y PostgreSQL

El proyecto está configurado para funcionar con **MySQL** (desarrollo local) y **PostgreSQL** (producción en Heroku) mediante el sistema de perfiles de Spring Boot.

---

## Configuración de Perfiles

### Archivos de Configuración

| Archivo | Perfil | Base de Datos | Uso |
|---------|--------|---------------|-----|
| `application.properties` | Default | - | Configuración base y selector de perfil |
| `application-mysql.properties` | mysql | MySQL | Desarrollo local |
| `application-postgres.properties` | postgres | PostgreSQL | Producción (Heroku) |

---

## Usar MySQL (Desarrollo Local)

### Opción 1: Perfil por Defecto
El proyecto usa MySQL por defecto. Solo ejecuta:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Opción 2: Especificar Perfil Explícitamente
```bash
# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Opción 3: Variable de Entorno
```powershell
# PowerShell
$env:SPRING_PROFILES_ACTIVE="mysql"
.\mvnw.cmd spring-boot:run
```

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=mysql
./mvnw spring-boot:run
```

### Opción 4: IDE (IntelliJ/Eclipse)
1. Ir a Run/Debug Configurations
2. En VM Options agregar: `-Dspring.profiles.active=mysql`
3. Ejecutar la aplicación

### Requisitos para MySQL
- MySQL Server 8.0+ corriendo en `localhost:3306`
- Base de datos: `empresa` (se crea automáticamente)
- Usuario: `root`
- Contraseña: (vacía o configurar en `application-mysql.properties`)

---

## Usar PostgreSQL (Desarrollo Local)

### Opción 1: Especificar Perfil
```bash
# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Opción 2: Variable de Entorno
```powershell
# PowerShell
$env:SPRING_PROFILES_ACTIVE="postgres"
.\mvnw.cmd spring-boot:run
```

### Opción 3: IDE
1. Ir a Run/Debug Configurations
2. En VM Options agregar: `-Dspring.profiles.active=postgres`
3. Ejecutar la aplicación

### Requisitos para PostgreSQL Local
- PostgreSQL 12+ instalado
- Base de datos: `empresa` creada manualmente
- Configurar credenciales en `application-postgres.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/empresa
spring.datasource.username=postgres
spring.datasource.password=tu_password
```

### Crear Base de Datos PostgreSQL Local
```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE empresa;

# Salir
\q

# Ejecutar script de inicialización
psql -U postgres -d empresa -f empresa-postgres.sql
```

---

## Usar PostgreSQL en Heroku (Producción)

### Configuración Automática
Heroku configura automáticamente PostgreSQL mediante:

1. **Procfile**: Especifica `-Dspring.profiles.active=postgres`
2. **DATABASE_URL**: Variable automática de Heroku
3. **Variables de entorno**: Configuradas con `heroku config:set`

### Variables de Heroku
```bash
# Establecer perfil (ya está en Procfile, pero puede configurarse también)
heroku config:set SPRING_PROFILES_ACTIVE=postgres

# Configurar MongoDB (requerido)
heroku config:set MONGO_URI="mongodb+srv://user:pass@cluster.mongodb.net/empresa"

# Verificar configuración
heroku config
```

### Desplegar
```bash
git push heroku main
```

Ver la [GUIA_HEROKU.md](GUIA_HEROKU.md) para instrucciones completas.

---

## Verificar Perfil Activo

### En Logs de la Aplicación
Al iniciar la aplicación, busca esta línea en los logs:

```
The following 1 profile is active: "mysql"
```

O para PostgreSQL:
```
The following 1 profile is active: "postgres"
```

### Ver Logs
```bash
# Local
# Los logs aparecen en la consola

# Heroku
heroku logs --tail
```

---

## Cambiar de Base de Datos

### De MySQL a PostgreSQL

1. **Instalar PostgreSQL** (si no está instalado)
2. **Crear base de datos** `empresa`
3. **Ejecutar script**: `empresa-postgres.sql`
4. **Cambiar perfil**: `-Dspring.profiles.active=postgres`
5. **Reiniciar aplicación**

### De PostgreSQL a MySQL

1. **Asegurar MySQL corriendo**
2. **Base de datos** `empresa` existe
3. **Cambiar perfil**: `-Dspring.profiles.active=mysql` (o quitar el parámetro)
4. **Reiniciar aplicación**

---

## Diferencias entre MySQL y PostgreSQL

| Característica | MySQL | PostgreSQL |
|----------------|-------|------------|
| **Puerto por defecto** | 3306 | 5432 |
| **Tipo Boolean** | BIT(1) | BOOLEAN |
| **Auto increment** | AUTO_INCREMENT | SERIAL/BIGSERIAL |
| **Case sensitivity** | Insensible | Sensible |
| **Dialect Hibernate** | MySQLDialect | PostgreSQLDialect |
| **Driver JDBC** | com.mysql.cj.jdbc.Driver | org.postgresql.Driver |
| **Licencia** | GPL + Commercial | PostgreSQL (tipo MIT) |

---

## Troubleshooting

### Error: "No suitable driver found"
**Causa**: Falta el driver de la base de datos

**Solución**:
1. Verificar que `pom.xml` contiene ambos drivers (MySQL y PostgreSQL)
2. Recompilar: `.\mvnw.cmd clean package`

### Error: "Connection refused"
**Causa**: Base de datos no está corriendo

**Solución MySQL**:
```bash
# Windows
net start MySQL80

# Linux
sudo systemctl start mysql
```

**Solución PostgreSQL**:
```bash
# Windows
net start postgresql-x64-14

# Linux
sudo systemctl start postgresql
```

### Error: "Unknown database 'empresa'"
**Causa**: Base de datos no existe

**Solución MySQL**:
```sql
CREATE DATABASE empresa;
```

**Solución PostgreSQL**:
```sql
CREATE DATABASE empresa;
```

### Error: "Access denied for user"
**Causa**: Credenciales incorrectas

**Solución**: Verificar usuario/contraseña en:
- `application-mysql.properties` (MySQL)
- `application-postgres.properties` (PostgreSQL)

### Perfil no cambia
**Causa**: Variable de entorno o parámetro mal configurado

**Solución**: Verificar en los logs qué perfil está activo:
```
The following 1 profile is active: "PERFIL_ACTIVO"
```

---

## Scripts de Base de Datos

### MySQL
- **Archivo**: `empresa.sql`
- **Ejecutar**: `mysql -u root -p < empresa.sql`

### PostgreSQL
- **Archivo**: `empresa-postgres.sql`
- **Ejecutar**: `psql -U postgres -d empresa -f empresa-postgres.sql`

Ambos scripts incluyen:
- Estructura de tablas
- Usuarios predefinidos (admin, user, ConejoDC)
- Datos de ejemplo (empleados)

---

## MongoDB (Sin Cambios)

MongoDB usa **MongoDB Atlas** (cloud) y funciona igual para ambos perfiles:
- La configuración está en ambos archivos de perfil
- Usa la misma URI en desarrollo y producción
- Las colecciones se crean automáticamente

---

## Resumen de Comandos

### MySQL (por defecto)
```bash
.\mvnw.cmd spring-boot:run
```

### PostgreSQL (local)
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

### PostgreSQL (Heroku)
```bash
git push heroku main
```

### Ver perfil activo
Revisar logs al iniciar la aplicación.

---

## Archivos Creados

| Archivo | Descripción |
|---------|-------------|
| `application-mysql.properties` | Configuración MySQL |
| `application-postgres.properties` | Configuración PostgreSQL |
| `empresa-postgres.sql` | Script SQL PostgreSQL |
| `Procfile` | Configuración Heroku |
| `system.properties` | Java version para Heroku |
| `GUIA_HEROKU.md` | Guía completa de despliegue |
| `PERFILES.md` | Este archivo |

---

**Última actualización:** Noviembre 2025
