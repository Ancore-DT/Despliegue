@echo off
REM ====================================================
REM  Script de Inicio - Sistema de Gestion de Empresa
REM  API REST con Spring Boot
REM ====================================================

color 0A
cls

echo.
echo ========================================
echo   SISTEMA DE GESTION DE EMPRESA
echo   API REST v1.0.0
echo ========================================
echo.
echo Iniciando aplicacion Spring Boot...
echo.
echo Puerto: 8080
echo Base de datos: MySQL + MongoDB
echo Endpoints: 22 APIs REST
echo.
echo Espere mientras se inicia...
echo ========================================
echo.

mvnw.cmd spring-boot:run

pause
