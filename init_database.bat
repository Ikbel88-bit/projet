@echo off
echo Initialisation de la base de données...
set /p MYSQL_PASSWORD="Entrez votre mot de passe MySQL root (laissez vide si pas de mot de passe): "

echo Création de la base de données 'groupe' si elle n'existe pas...
if "%MYSQL_PASSWORD%"=="" (
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS groupe;"
) else (
    mysql -u root -p%MYSQL_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS groupe;"
)

echo Suppression de la table users si elle existe...
if "%MYSQL_PASSWORD%"=="" (
    mysql -u root groupe -e "DROP TABLE IF EXISTS users;"
) else (
    mysql -u root -p%MYSQL_PASSWORD% groupe -e "DROP TABLE IF EXISTS users;"
)

echo Exécution du script SQL pour créer les tables...
if "%MYSQL_PASSWORD%"=="" (
    mysql -u root groupe < src\main\resources\database.sql
) else (
    mysql -u root -p%MYSQL_PASSWORD% groupe < src\main\resources\database.sql
)

if %ERRORLEVEL% EQU 0 (
    echo Base de données initialisée avec succès !
    echo.
    echo Identifiants par défaut :
    echo Admin : admin@admin.com / admin123
    echo Employé : employe@employe.com / emp123
) else (
    echo Erreur lors de l'initialisation de la base de données.
)

pause