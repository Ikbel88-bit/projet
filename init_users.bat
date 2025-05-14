@echo off
echo Initialisation de la table users...
set /p MYSQL_PASSWORD="Entrez votre mot de passe MySQL root (laissez vide si pas de mot de passe): "

if "%MYSQL_PASSWORD%"=="" (
    mysql -u root groupe < src\main\resources\database.sql
) else (
    mysql -u root -p%MYSQL_PASSWORD% groupe < src\main\resources\database.sql
)

if %ERRORLEVEL% EQU 0 (
    echo Table users initialisée avec succès !
    echo.
    echo Identifiants par défaut :
    echo Admin : admin / admin123
    echo Employé : employe / emp123
) else (
    echo Erreur lors de l'initialisation de la table users.
)

pause 