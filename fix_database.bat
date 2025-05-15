@echo off
echo Correction de la structure de la base de données...
set /p MYSQL_PASSWORD="Entrez votre mot de passe MySQL root (laissez vide si pas de mot de passe): "

echo Exécution du script SQL pour corriger la structure...
if "%MYSQL_PASSWORD%"=="" (
    mysql -u root groupe < src\main\resources\fix_tache_table.sql
) else (
    mysql -u root -p%MYSQL_PASSWORD% groupe < src\main\resources\fix_tache_table.sql
)

if %ERRORLEVEL% EQU 0 (
    echo Structure de la base de données corrigée avec succès !
) else (
    echo Erreur lors de la correction de la structure de la base de données.
)

pause