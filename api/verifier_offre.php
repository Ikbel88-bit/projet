<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Fonction pour vérifier si une description est significative
function estDescriptionSignificative($description) {
    // Vérifier la longueur maximale (255 caractères)
    if (strlen($description) > 255) {
        return false;
    }

    // Vérifier la longueur minimale (30 caractères)
    if (strlen($description) < 30) {
        return false;
    }

    // Vérifier le nombre de mots (minimum 5)
    $mots = str_word_count($description);
    if ($mots < 5) {
        return false;
    }

    // Vérifier la présence de mots-clés importants
    $motsCles = [
        // Mots-clés principaux
        'responsabilités',
        'compétences',
        'expérience',
        'mission',
        'profil',
        'tâches',
        'qualifications',
        
        // Termes liés au poste
        'poste',
        'emploi',
        'recrutement',
        'candidat',
        'candidature',
        'contrat',
        'cdi',
        'cdd',
        'stage',
        'freelance',
        
        // Termes liés aux compétences
        'formation',
        'diplôme',
        'études',
        'connaissances',
        'maîtrise',
        'expertise',
        'savoir-faire',
        
        // Termes liés aux conditions
        'salaire',
        'rémunération',
        'avantages',
        'bénéfices',
        'horaires',
        'localisation',
        'télétravail',
        
        // Termes liés aux soft skills
        'autonomie',
        'rigueur',
        'organisation',
        'communication',
        'travail d\'équipe',
        'adaptabilité',
        'créativité'
    ];
    $descriptionLower = strtolower($description);
    $motsClesTrouves = 0;

    foreach ($motsCles as $mot) {
        if (strpos($descriptionLower, $mot) !== false) {
            $motsClesTrouves++;
        }
    }

    // La description doit contenir au moins 1 mot-clé
    if ($motsClesTrouves < 1) {
        return false;
    }

    // Vérifier la présence d'au moins une phrase complète (avec point)
    if (substr_count($description, '.') < 1) {
        return false;
    }

    return true;
}

// Récupérer les données POST
$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['description'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Description manquante']);
    exit;
}

$description = $data['description'];
$estValide = estDescriptionSignificative($description);

echo json_encode([
    'valid' => $estValide,
    'message' => $estValide ? 'Description valide' : 'La description doit contenir entre 30 et 255 caractères, au moins 5 mots, un mot-clé important et une phrase complète'
]);
?> 