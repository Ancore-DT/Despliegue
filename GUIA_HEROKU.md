# Guía de Despliegue en Heroku con PostgreSQL

## Requisitos Previos

- Cuenta en Heroku: https://signup.heroku.com/
- Heroku CLI instalado: https://devcenter.heroku.com/articles/heroku-cli
- Git instalado
- Proyecto compilado correctamente en local

---

## Paso 1: Instalar y Configurar Heroku CLI

### Windows (PowerShell)
```powershell
# Descargar e instalar desde: https://devcenter.heroku.com/articles/heroku-cli

# Verificar instalación
heroku --version

# Login
heroku login
```

### Linux/Mac
```bash
# Instalar Heroku CLI
curl https://cli-assets.heroku.com/install.sh | sh

# Verificar instalación
heroku --version

# Login
heroku login
```

---

## Paso 2: Preparar el Proyecto

### 2.1 Compilar el proyecto
```bash
# Limpiar y compilar
.\mvnw.cmd clean package -DskipTests

# Verificar que se generó el JAR
dir target\empresa-0.0.1-SNAPSHOT.jar
```

### 2.2 Inicializar Git (si no lo has hecho)
```bash
git init
git add .
git commit -m "Configuración inicial para Heroku con PostgreSQL"
```

---

## Paso 3: Crear Aplicación en Heroku

```bash
# Crear aplicación (Heroku generará un nombre automático)
heroku create

# O crear con nombre personalizado
heroku create nombre-empresa-app

# Verificar aplicación creada
heroku apps:info
```

---

## Paso 4: Agregar PostgreSQL

```bash
# Agregar PostgreSQL addon (plan gratuito Mini)
heroku addons:create heroku-postgresql:mini

# Verificar que se creó la base de datos
heroku addons

# Ver información de conexión (DATABASE_URL se configura automáticamente)
heroku config:get DATABASE_URL
```

La variable `DATABASE_URL` se establece automáticamente y tiene este formato:
```
postgres://usuario:password@host:5432/database
```

---

## Paso 5: Configurar Variables de Entorno

```bash
# Configurar perfil de Spring Boot para usar PostgreSQL
heroku config:set SPRING_PROFILES_ACTIVE=postgres

# Configurar MongoDB Atlas
heroku config:set MONGO_URI="mongodb+srv://empresa:Sena2025@empresa.czeymou.mongodb.net/?retryWrites=true&w=majority&appName=empresa"

# Verificar todas las variables configuradas
heroku config
```

**Salida esperada:**
```
=== nombre-empresa-app Config Vars
DATABASE_URL:           postgres://usuario:password@host:5432/database
MONGO_URI:              mongodb+srv://...
SPRING_PROFILES_ACTIVE: postgres
```

---

## Paso 6: Desplegar la Aplicación

```bash
# Conectar repositorio local con Heroku (si no se hizo en paso 3)
heroku git:remote -a nombre-empresa-app

# Desplegar
git push heroku main

# O si tu rama principal es master
git push heroku master
```

**Proceso de despliegue:**
1. Heroku detecta que es una aplicación Java/Maven
2. Descarga dependencias
3. Compila el proyecto
4. Crea el slug (aplicación empaquetada)
5. Inicia la aplicación

---

## Paso 7: Verificar el Despliegue

```bash
# Ver logs en tiempo real
heroku logs --tail

# Abrir aplicación en el navegador
heroku open

# Ver estado de la aplicación
heroku ps

# Verificar que la aplicación está corriendo
heroku ps:scale web=1
```

---

## Paso 8: Inicializar Base de Datos (Opcional)

### Opción A: Dejar que Hibernate cree las tablas automáticamente
La aplicación creará las tablas automáticamente al iniciar (configurado con `ddl-auto=update`)

### Opción B: Ejecutar script SQL manualmente
```bash
# Conectar a la base de datos PostgreSQL de Heroku
heroku pg:psql

# Ejecutar comandos SQL manualmente o copiar desde empresa-postgres.sql
# Luego escribir: \q para salir
```

### Opción C: Ejecutar script desde archivo local
```bash
# Ejecutar script completo
heroku pg:psql < empresa-postgres.sql
```

---

## Paso 9: Probar la Aplicación

### Desde el navegador
```
https://nombre-empresa-app.herokuapp.com
```

### Desde cURL
```bash
# Obtener la URL de tu app
heroku apps:info

# Login
curl -i -c cookies.txt -X POST https://nombre-empresa-app.herokuapp.com/login ^
  -d "username=admin&password=admin123"

# Listar empleados
curl -b cookies.txt https://nombre-empresa-app.herokuapp.com/api/empleados

# Listar proyectos
curl -b cookies.txt https://nombre-empresa-app.herokuapp.com/api/proyectos
```

---

## Comandos Útiles de Heroku

### Gestión de la Aplicación
```bash
# Ver logs
heroku logs --tail

# Reiniciar aplicación
heroku restart

# Ver información de la app
heroku apps:info

# Escalar dynos (instancias)
heroku ps:scale web=1

# Ver métricas
heroku ps
```

### Gestión de Base de Datos
```bash
# Ver información de PostgreSQL
heroku pg:info

# Backup de la base de datos
heroku pg:backups:capture

# Restaurar backup
heroku pg:backups:restore

# Conectar a PostgreSQL
heroku pg:psql

# Ver credenciales de la BD
heroku pg:credentials:url
```

### Gestión de Variables de Entorno
```bash
# Ver todas las variables
heroku config

# Establecer variable
heroku config:set VARIABLE_NAME=valor

# Eliminar variable
heroku config:unset VARIABLE_NAME

# Ver variable específica
heroku config:get VARIABLE_NAME
```

### Gestión de Código
```bash
# Ver git remotes
git remote -v

# Agregar remote de Heroku
heroku git:remote -a nombre-app

# Ver releases
heroku releases

# Hacer rollback a versión anterior
heroku rollback v123
```

---

## Cambiar entre MySQL (Local) y PostgreSQL (Heroku)

### Desarrollo Local con MySQL
```bash
# No hacer nada, el perfil por defecto es mysql
.\mvnw.cmd spring-boot:run

# O especificar explícitamente
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Desarrollo Local con PostgreSQL
```bash
# Especificar perfil postgres
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres

# O configurar variable de entorno
$env:SPRING_PROFILES_ACTIVE="postgres"
.\mvnw.cmd spring-boot:run
```

### Producción en Heroku
```bash
# Heroku usa automáticamente el perfil postgres (configurado en Procfile)
# Si necesitas cambiarlo:
heroku config:set SPRING_PROFILES_ACTIVE=postgres
```

---

## Solución de Problemas

### Error: "Application failed to start"
```bash
# Ver logs detallados
heroku logs --tail

# Verificar que el JAR se compiló correctamente
.\mvnw.cmd clean package

# Verificar Java version en system.properties
cat system.properties
```

### Error: "Database connection failed"
```bash
# Verificar DATABASE_URL
heroku config:get DATABASE_URL

# Verificar addon de PostgreSQL
heroku addons

# Reconectar addon si es necesario
heroku addons:attach postgresql-addon-name
```

### Error: "Port binding failed"
```bash
# Verificar que el Procfile usa $PORT
cat Procfile

# Debe contener: -Dserver.port=$PORT
```

### Error: "MongoDB connection timeout"
```bash
# Verificar MONGO_URI
heroku config:get MONGO_URI

# Reconfigurar si es necesario
heroku config:set MONGO_URI="tu_uri_completa"
```

### La aplicación se duerme (plan gratuito)
```bash
# El dyno gratuito se duerme después de 30 minutos de inactividad
# Primera petición tardará ~10 segundos en despertar

# Para mantener activo (requiere plan de pago):
heroku ps:scale web=1:hobby
```

---

## Actualizar la Aplicación

```bash
# Hacer cambios en el código
git add .
git commit -m "Descripción de cambios"

# Desplegar nueva versión
git push heroku main

# Ver el despliegue
heroku logs --tail
```

---

## Monitoreo y Mantenimiento

### Ver métricas
```bash
# Dashboard web
heroku dashboard

# Métricas desde CLI
heroku ps
heroku pg:info
```

### Backup de Base de Datos
```bash
# Crear backup manual
heroku pg:backups:capture

# Listar backups
heroku pg:backups

# Descargar backup
heroku pg:backups:download
```

### Escalar la Aplicación (requiere plan de pago)
```bash
# Más dynos
heroku ps:scale web=2

# Dyno más potente
heroku ps:resize web=standard-1x
```

---

## Costos de Heroku

| Plan | Precio | Características |
|------|--------|-----------------|
| **Free (Eco)** | $0/mes | 1000 horas/mes compartidas entre apps |
| **Hobby** | $7/mes por dyno | Siempre activo, sin límite de horas |
| **Standard** | $25+/mes | Más potencia, métricas avanzadas |
| **PostgreSQL Mini** | $0/mes | 1GB almacenamiento, 20 conexiones |

**Nota:** Los planes gratuitos pueden cambiar. Consulta: https://www.heroku.com/pricing

---

## Recursos Adicionales

- **Documentación Heroku Java**: https://devcenter.heroku.com/categories/java-support
- **Heroku PostgreSQL**: https://devcenter.heroku.com/articles/heroku-postgresql
- **Spring Boot en Heroku**: https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku
- **Heroku CLI Commands**: https://devcenter.heroku.com/articles/heroku-cli-commands

---

## Resumen de Comandos Esenciales

```bash
# Setup inicial
heroku login
heroku create nombre-app
heroku addons:create heroku-postgresql:mini
heroku config:set SPRING_PROFILES_ACTIVE=postgres
heroku config:set MONGO_URI="tu_uri_mongodb"

# Desplegar
git push heroku main

# Monitorear
heroku logs --tail
heroku ps
heroku open

# Base de datos
heroku pg:psql
heroku pg:info
heroku pg:backups:capture

# Actualizar
git add .
git commit -m "cambios"
git push heroku main
```

---

**Última actualización:** Noviembre 2025
