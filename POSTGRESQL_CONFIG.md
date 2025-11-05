# Configuración de PostgreSQL - Heroku/AWS

## Información de Conexión

La base de datos PostgreSQL está alojada en **AWS RDS** a través de Heroku.

### Detalles de Conexión
- **Host**: c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com
- **Puerto**: 5432
- **Base de datos**: d6dt2bekhdnmov
- **Usuario**: u4g83sogj05dcq
- **Contraseña**: pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc

### URL Completa
```
postgres://u4g83sogj05dcq:pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc@c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov
```

### URL JDBC (Spring Boot)
```
jdbc:postgresql://c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov
```

---

## Usar la Base de Datos PostgreSQL

### Opción 1: Ejecución Local con PostgreSQL de Heroku

```bash
# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Opción 2: Variables de Entorno en Heroku

Si despliegas en Heroku, configura la variable `DATABASE_URL`:

```bash
heroku config:set DATABASE_URL="postgres://u4g83sogj05dcq:pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc@c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov"
```

**Nota**: Heroku probablemente ya configuró esta variable automáticamente.

---

## Conectar desde Heroku

Al desplegar en Heroku, la aplicación usará automáticamente esta base de datos:

```bash
# Desplegar
git push heroku main

# Ver logs
heroku logs --tail

# Verificar conexión
heroku run echo $DATABASE_URL
```

---

## Conectar Manualmente con psql

### Desde línea de comandos local
```bash
psql postgres://u4g83sogj05dcq:pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc@c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov
```

### Desde Heroku CLI
```bash
heroku pg:psql
```

### Desde pgAdmin o DBeaver
- **Host**: c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com
- **Port**: 5432
- **Database**: d6dt2bekhdnmov
- **Username**: u4g83sogj05dcq
- **Password**: pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc
- **SSL Mode**: Require (o Prefer)

---

## Inicializar la Base de Datos

### Opción 1: Automática (Recomendada)
La aplicación creará las tablas automáticamente al iniciar gracias a:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Opción 2: Manual con Script SQL

```bash
# Conectar a la base de datos
heroku pg:psql

# O desde local
psql postgres://u4g83sogj05dcq:...@c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov

# Luego ejecutar comandos SQL del archivo empresa-postgres.sql
```

O ejecutar el script completo:
```bash
psql postgres://u4g83sogj05dcq:pd1ee93be1db8c4de0af6b37fbea8b39dce4cad6043b797006b49fde19d49a3bc@c7b4i1efuvdata.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/d6dt2bekhdnmov < empresa-postgres.sql
```

---

## Verificar Conexión

### Desde la aplicación Spring Boot
Al iniciar con el perfil `postgres`, busca en los logs:

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

### Desde Heroku
```bash
# Ver estado de la base de datos
heroku pg:info

# Ver conexiones activas
heroku pg:ps

# Diagnóstico
heroku pg:diagnose
```

---

## Comandos Útiles

### Ver información de la base de datos
```bash
heroku pg:info
```

### Backup
```bash
# Crear backup
heroku pg:backups:capture

# Listar backups
heroku pg:backups

# Descargar último backup
heroku pg:backups:download
```

### Ver credenciales
```bash
heroku config:get DATABASE_URL
```

### Resetear base de datos (¡CUIDADO! Borra todos los datos)
```bash
heroku pg:reset DATABASE_URL
```

---

## Seguridad

**IMPORTANTE**: Las credenciales de esta base de datos son sensibles. 

- No compartas públicamente la URL completa
- Si el proyecto es público en GitHub, usa variables de entorno
- Considera rotar las credenciales periódicamente

### Rotar credenciales en Heroku
```bash
heroku pg:credentials:rotate DATABASE_URL
```

---

## Troubleshooting

### Error: "Connection refused"
- Verifica que tu IP tenga acceso (AWS RDS puede tener restricciones)
- En Heroku esto no es problema, está preconfigurado

### Error: "SSL connection required"
Agrega a la URL: `?sslmode=require`

```properties
spring.datasource.url=jdbc:postgresql://host:5432/database?sslmode=require
```

### Error: "Too many connections"
```bash
# Ver conexiones activas
heroku pg:ps

# Matar conexión específica
heroku pg:kill <pid>
```

### Error: "Database does not exist"
La base de datos `d6dt2bekhdnmov` ya existe, Heroku la creó automáticamente.

---

## Alternar entre MySQL (Local) y PostgreSQL (Heroku)

### Desarrollo Local con MySQL
```bash
.\mvnw.cmd spring-boot:run
# Usa: application-mysql.properties
```

### Desarrollo/Pruebas con PostgreSQL de Heroku
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
# Usa: application-postgres.properties (esta configuración)
```

### Producción en Heroku
```bash
git push heroku main
# Usa automáticamente: application-postgres.properties
```

---

## Resumen

| Entorno | Base de Datos | Comando |
|---------|---------------|---------|
| **Local - Desarrollo** | MySQL local | `.\mvnw.cmd spring-boot:run` |
| **Local - Pruebas** | PostgreSQL Heroku | `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres` |
| **Producción** | PostgreSQL Heroku | `git push heroku main` |

---

**Configuración actualizada:** Noviembre 2025  
**Base de datos:** AWS RDS (via Heroku PostgreSQL)
