-- Script pour mettre à jour la structure de la base de données
-- Ajouter les colonnes cv_path et lettre_motivation_path à la table entretiens

-- Vérifier si les colonnes existent déjà
SET @dbname = 'groupe';
SET @tablename = 'entretiens';
SET @columnname = 'cv_path';
SET @columnname2 = 'lettre_motivation_path';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname) AND
      (TABLE_NAME = @tablename) AND
      (COLUMN_NAME = @columnname)
  ) > 0,
  "SELECT 'Column cv_path already exists'",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname, " VARCHAR(255) NULL")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname) AND
      (TABLE_NAME = @tablename) AND
      (COLUMN_NAME = @columnname2)
  ) > 0,
  "SELECT 'Column lettre_motivation_path already exists'",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname2, " VARCHAR(255) NULL")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Afficher la structure de la table après les modifications
DESCRIBE entretiens;
