package controller;

import entities.User;
import entities.Tache;
import entities.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.Region;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Separator;
import javafx.scene.control.DialogPane;
import javafx.application.Platform;
import services.ServiceTache;
import services.ServiceUser;
import services.ServiceNotification;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Contrôleur pour le tableau de bord Employé.
 */
public class EmployeeDashboardController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDashboardController.class.getName());

    @FXML private ScrollPane tasksScrollPane;
    @FXML private GridPane tasksEnCoursGrid;
    @FXML private GridPane tasksEnRetardGrid;
    @FXML private GridPane tasksTermineesGrid;
    @FXML private VBox urgentTasksContainer;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    @FXML private Label welcomeLabel;
    @FXML private Label tachesAFaireLabel;
    @FXML private Label tachesTermineesLabel;
    @FXML private Label tachesEnRetardLabel;
    @FXML private Label tauxCompletionLabel;
    @FXML private Button retourButton;
    @FXML private PieChart taskDistributionChart;
    @FXML private BarChart<String, Number> deadlineChart;

    private ServiceTache serviceTache;
    private MainController mainController;
    private User currentUser;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        try {
            LOGGER.info("Initialisation du tableau de bord employé");
            serviceTache = new ServiceTache();
            setupGridPanes();
            setupCharts();

            // Ajouter des écouteurs pour redimensionner les composants
            if (tasksScrollPane != null) {
                tasksScrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                    adjustGridPaneWidth();
                });
            }

            // Charger explicitement le CSS
            String cssPath = getClass().getResource("/styles/dashboard.css").toExternalForm();
            LOGGER.info("Chargement du CSS: " + cssPath);

            // Appliquer le CSS à la scène lorsqu'elle est disponible
            Platform.runLater(() -> {
                try {
                    if (tasksScrollPane != null && tasksScrollPane.getScene() != null) {
                        if (!tasksScrollPane.getScene().getStylesheets().contains(cssPath)) {
                            tasksScrollPane.getScene().getStylesheets().add(cssPath);
                            LOGGER.info("CSS appliqué à la scène");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Impossible d'appliquer le CSS à la scène", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation du tableau de bord", e);
        }
    }

    private void adjustGridPaneWidth() {
        try {
            if (tasksEnCoursGrid != null && tasksScrollPane != null) {
                double width = tasksScrollPane.getWidth() - 30; // Ajuster pour les marges
                tasksEnCoursGrid.setPrefWidth(width);
                if (tasksEnRetardGrid != null) tasksEnRetardGrid.setPrefWidth(width);
                if (tasksTermineesGrid != null) tasksTermineesGrid.setPrefWidth(width);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de l'ajustement de la largeur des grilles", e);
        }
    }

    private void setupCharts() {
        // Configuration initiale des graphiques
        if (taskDistributionChart != null) {
            taskDistributionChart.setAnimated(true);
            taskDistributionChart.setLabelLineLength(20);
            taskDistributionChart.setLabelsVisible(true);
            taskDistributionChart.setLegendVisible(true);
        }

        if (deadlineChart != null) {
            deadlineChart.setAnimated(true);
            deadlineChart.setLegendVisible(false);
            deadlineChart.setVerticalGridLinesVisible(false);
        }
    }

    private void setupGridPanes() {
        // Configuration des grilles de tâches
        for (GridPane grid : new GridPane[]{tasksEnCoursGrid, tasksEnRetardGrid, tasksTermineesGrid}) {
            if (grid != null) {
                grid.setHgap(20);
                grid.setVgap(20);
                grid.setPadding(new Insets(20));
            }
        }
    }

    @FXML
    private void handleRefresh() {
        loadDashboard();
    }

    private void loadDashboard() {
        try {
            LOGGER.info("Chargement du tableau de bord");
            loadTaches();
            updateStatistics();
            updateCharts();
            loadUrgentTasks();
            adjustGridPaneWidth();
            LOGGER.info("Tableau de bord chargé avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du tableau de bord", e);
            showAlert("Erreur", "Une erreur est survenue lors du chargement des données: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            if (mainController == null || mainController.getCurrentUser() == null) {
                LOGGER.warning("MainController ou CurrentUser est null");
                return;
            }

            int employeId = mainController.getCurrentUser().getId();
            List<Tache> allTaches = serviceTache.findByEmployeId(employeId);

            // Compter les tâches par état
            long tachesEnCours = allTaches.stream().filter(t -> "EN_COURS".equals(t.getEtat())).count();
            long tachesTerminees = allTaches.stream().filter(t -> "TERMINE".equals(t.getEtat())).count();
            long tachesEnRetard = allTaches.stream().filter(t -> "EN_RETARD".equals(t.getEtat())).count();

            // Calculer le taux de complétion
            double tauxCompletion = allTaches.isEmpty() ? 0 :
                (double) tachesTerminees / allTaches.size() * 100;

            // Mettre à jour les labels
            tachesAFaireLabel.setText(String.valueOf(tachesEnCours));
            tachesTermineesLabel.setText(String.valueOf(tachesTerminees));
            tachesEnRetardLabel.setText(String.valueOf(tachesEnRetard));
            tauxCompletionLabel.setText(String.format("%.1f%%", tauxCompletion));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des statistiques", e);
            showAlert("Erreur", "Erreur lors de la mise à jour des statistiques : " + e.getMessage());
        }
    }

    private void updateCharts() {
        try {
            if (mainController == null || mainController.getCurrentUser() == null) {
                return;
            }

            int employeId = mainController.getCurrentUser().getId();
            List<Tache> allTaches = serviceTache.findByEmployeId(employeId);

            // Mise à jour du graphique en camembert
            updatePieChart(allTaches);

            // Mise à jour du graphique des échéances
            updateDeadlineChart(allTaches);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des graphiques", e);
        }
    }

    private void updatePieChart(List<Tache> taches) {
        try {
            if (taskDistributionChart == null) {
                LOGGER.warning("Le graphique en camembert n'est pas initialisé");
                return;
            }

            // Compter les tâches par état
            long tachesEnCours = taches.stream().filter(t -> "EN_COURS".equals(t.getEtat())).count();
            long tachesTerminees = taches.stream().filter(t -> "TERMINE".equals(t.getEtat())).count();
            long tachesEnRetard = taches.stream().filter(t -> "EN_RETARD".equals(t.getEtat())).count();

            // Créer les données pour le graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // N'ajouter que les états qui ont des tâches
            if (tachesEnCours > 0) {
                pieChartData.add(new PieChart.Data("En cours ("+tachesEnCours+")", tachesEnCours));
            }
            if (tachesTerminees > 0) {
                pieChartData.add(new PieChart.Data("Terminées ("+tachesTerminees+")", tachesTerminees));
            }
            if (tachesEnRetard > 0) {
                pieChartData.add(new PieChart.Data("En retard ("+tachesEnRetard+")", tachesEnRetard));
            }

            // Si aucune tâche, ajouter une donnée par défaut
            if (pieChartData.isEmpty()) {
                pieChartData.add(new PieChart.Data("Aucune tâche", 1));
            }

            // Mettre à jour le graphique
            taskDistributionChart.setData(pieChartData);

            // Ajouter des couleurs personnalisées après un court délai pour s'assurer que les nœuds sont créés
            Platform.runLater(() -> {
                try {
                    for (PieChart.Data data : pieChartData) {
                        String color = "#95a5a6"; // Gris par défaut

                        if (data.getName().contains("En cours")) {
                            color = "#3498db"; // Bleu pour en cours
                        } else if (data.getName().contains("Terminées")) {
                            color = "#2ecc71"; // Vert pour terminées
                        } else if (data.getName().contains("En retard")) {
                            color = "#e74c3c"; // Rouge pour en retard
                        }

                        // Appliquer la couleur après le rendu du graphique
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-pie-color: " + color + ";");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors de l'application des couleurs au graphique", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du graphique en camembert", e);
        }
    }

    private void updateDeadlineChart(List<Tache> taches) {
        try {
            if (deadlineChart == null) {
                LOGGER.warning("Le graphique des échéances n'est pas initialisé");
                return;
            }

            // Filtrer les tâches non terminées
            List<Tache> tachesNonTerminees = taches.stream()
                .filter(t -> !"TERMINE".equals(t.getEtat()))
                .filter(t -> t.getDate_fin() != null) // S'assurer que la date d'échéance existe
                .collect(Collectors.toList());

            // Trier par date d'échéance (les plus proches en premier)
            tachesNonTerminees.sort(Comparator.comparing(Tache::getDate_fin));

            // Limiter à 5 tâches pour la lisibilité
            List<Tache> tachesAffichees = tachesNonTerminees.stream()
                .limit(5)
                .collect(Collectors.toList());

            // Créer la série de données
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Jours restants");

            // Ajouter les données
            LocalDate today = LocalDate.now();
            for (Tache tache : tachesAffichees) {
                if (tache.getDate_fin() != null) {
                    long daysUntilDeadline = ChronoUnit.DAYS.between(today, tache.getDate_fin());
                    // Tronquer le nom de la tâche s'il est trop long
                    String taskName = tache.getNom_tache();
                    if (taskName != null && taskName.length() > 15) {
                        taskName = taskName.substring(0, 12) + "...";
                    } else if (taskName == null) {
                        taskName = "Tâche sans nom";
                    }
                    series.getData().add(new XYChart.Data<>(taskName, daysUntilDeadline));
                }
            }

            // Mettre à jour le graphique
            deadlineChart.getData().clear();

            // Afficher un message si aucune tâche à afficher
            if (series.getData().isEmpty()) {
                // Créer une série vide avec un message
                XYChart.Series<String, Number> emptySeries = new XYChart.Series<>();
                emptySeries.setName("Aucune échéance à venir");
                emptySeries.getData().add(new XYChart.Data<>("Aucune tâche", 0));
                deadlineChart.getData().add(emptySeries);
            } else {
                deadlineChart.getData().add(series);

                // Personnaliser les barres après un court délai pour s'assurer que les nœuds sont créés
                Platform.runLater(() -> {
                    try {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            if (data.getNode() != null) {
                                long daysRemaining = (long) data.getYValue();
                                if (daysRemaining < 0) {
                                    // En retard
                                    data.getNode().setStyle("-fx-bar-fill: #e74c3c;"); // Rouge
                                } else if (daysRemaining <= 3) {
                                    // Urgent
                                    data.getNode().setStyle("-fx-bar-fill: #f39c12;"); // Orange
                                } else {
                                    // Normal
                                    data.getNode().setStyle("-fx-bar-fill: #3498db;"); // Bleu
                                }

                                // Ajouter un tooltip pour afficher le nom complet de la tâche
                                Tooltip tooltip = new Tooltip("Jours restants: " + daysRemaining);
                                Tooltip.install(data.getNode(), tooltip);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Erreur lors de l'application des styles aux barres du graphique", e);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du graphique des échéances", e);
        }
    }

    private void loadUrgentTasks() {
        try {
            if (mainController == null || mainController.getCurrentUser() == null) {
                return;
            }

            int employeId = mainController.getCurrentUser().getId();
            List<Tache> allTaches = serviceTache.findByEmployeId(employeId);

            // Filtrer les tâches urgentes (non terminées et avec échéance < 3 jours)
            LocalDate today = LocalDate.now();
            List<Tache> urgentTasks = allTaches.stream()
                .filter(t -> !"TERMINE".equals(t.getEtat()))
                .filter(t -> t.getDate_fin() != null)
                .filter(t -> ChronoUnit.DAYS.between(today, t.getDate_fin()) <= 3)
                .sorted(Comparator.comparing(Tache::getDate_fin))
                .collect(Collectors.toList());

            // Mettre à jour le conteneur
            urgentTasksContainer.getChildren().clear();

            if (urgentTasks.isEmpty()) {
                Label emptyLabel = new Label("Aucune tâche urgente pour le moment");
                emptyLabel.getStyleClass().add("empty-message");
                urgentTasksContainer.getChildren().add(emptyLabel);
            } else {
                for (Tache tache : urgentTasks) {
                    HBox taskRow = createUrgentTaskRow(tache);
                    urgentTasksContainer.getChildren().add(taskRow);
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des tâches urgentes", e);
        }
    }

    private HBox createUrgentTaskRow(Tache tache) {
        HBox row = new HBox(10);
        row.getStyleClass().add("urgent-task-row");
        row.setPadding(new Insets(10));

        // Ajouter un effet d'ombre léger
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3.0);
        shadow.setOffsetX(1.0);
        shadow.setOffsetY(1.0);
        shadow.setColor(Color.color(0.4, 0.4, 0.4, 0.2));
        row.setEffect(shadow);

        // Nom de la tâche
        Label nameLabel = new Label(tache.getNom_tache());
        nameLabel.getStyleClass().add("task-name");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Date d'échéance
        Label dateLabel = new Label(tache.getDate_fin().format(DATE_FORMATTER));
        dateLabel.getStyleClass().add("task-date");

        // Jours restants
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), tache.getDate_fin());
        String daysText = daysRemaining < 0 ? Math.abs(daysRemaining) + " jour(s) de retard" : daysRemaining + " jour(s) restant(s)";
        Label daysLabel = new Label(daysText);
        daysLabel.getStyleClass().add("days-remaining");

        if (daysRemaining < 0) {
            daysLabel.getStyleClass().add("overdue");
        } else if (daysRemaining <= 1) {
            daysLabel.getStyleClass().add("critical");
        } else {
            daysLabel.getStyleClass().add("urgent");
        }

        // Bouton pour marquer comme terminé
        Button completeBtn = new Button("Terminer");
        completeBtn.getStyleClass().add("complete-button");
        completeBtn.setOnAction(e -> updateTaskState(tache, "TERMINE"));

        row.getChildren().addAll(nameLabel, dateLabel, daysLabel, completeBtn);

        // Ajouter un tooltip avec la description complète
        Tooltip tooltip = new Tooltip(tache.getDescription());
        Tooltip.install(row, tooltip);

        return row;
    }

    /**
     * Charge les tâches de l'employé et les affiche dans les grilles correspondantes.
     */
    private void loadTaches() {
        try {
            if (mainController == null || mainController.getCurrentUser() == null) {
                LOGGER.warning("MainController ou CurrentUser est null");
                return;
            }

            int employeId = mainController.getCurrentUser().getId();
            List<Tache> allTaches = serviceTache.findByEmployeId(employeId);

            // Filtrer les tâches par état
            List<Tache> tachesEnCours = allTaches.stream()
                .filter(t -> "EN_COURS".equals(t.getEtat()))
                .collect(Collectors.toList());

            List<Tache> tachesEnRetard = allTaches.stream()
                .filter(t -> "EN_RETARD".equals(t.getEtat()))
                .collect(Collectors.toList());

            List<Tache> tachesTerminees = allTaches.stream()
                .filter(t -> "TERMINE".equals(t.getEtat()))
                .collect(Collectors.toList());

            // Afficher les tâches dans les grilles correspondantes
            displayTachesInGrid(tachesEnCours, tasksEnCoursGrid);
            displayTachesInGrid(tachesEnRetard, tasksEnRetardGrid);
            displayTachesInGrid(tachesTerminees, tasksTermineesGrid);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des tâches", e);
            showAlert("Erreur", "Erreur lors du chargement des tâches : " + e.getMessage());
        }
    }

    /**
     * Affiche les tâches dans une grille spécifique.
     * @param taches Liste des tâches à afficher
     * @param grid Grille dans laquelle afficher les tâches
     */
    private void displayTachesInGrid(List<Tache> taches, GridPane grid) {
        try {
            if (grid == null) {
                LOGGER.warning("La grille est null");
                return;
            }

            // Vider la grille
            grid.getChildren().clear();

            if (taches == null || taches.isEmpty()) {
                Label emptyLabel = new Label("Aucune tâche dans cette catégorie");
                emptyLabel.getStyleClass().add("empty-message");
                grid.add(emptyLabel, 0, 0);
                return;
            }

            // Nombre de colonnes dans la grille
            int colCount = 2;

            // Ajouter les cartes de tâches à la grille
            for (int i = 0; i < taches.size(); i++) {
                try {
                    int col = i % colCount;
                    int row = i / colCount;

                    Tache tache = taches.get(i);
                    if (tache != null) {
                        VBox taskCard = createTaskCard(tache);
                        grid.add(taskCard, col, row);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Erreur lors de l'affichage de la tâche à l'index " + i, e);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage des tâches dans la grille", e);
        }
    }

    /**
     * Crée une carte visuelle pour une tâche.
     * @param tache La tâche à afficher
     * @return Une VBox contenant les informations de la tâche
     */
    private VBox createTaskCard(Tache tache) {
        VBox card = new VBox(10);
        card.getStyleClass().add("task-card");
        card.setPadding(new Insets(15));
        card.setMinWidth(300);
        card.setMaxWidth(350);

        // Appliquer une couleur de fond selon l'état
        switch (tache.getEtat()) {
            case "TERMINE":
                card.setStyle("-fx-background-color: linear-gradient(to bottom right, #d5f5e3, #abebc6);"); // Vert clair
                break;
            case "EN_RETARD":
                card.setStyle("-fx-background-color: linear-gradient(to bottom right, #fadbd8, #f5b7b1);"); // Rouge clair
                break;
            default: // EN_COURS
                card.setStyle("-fx-background-color: linear-gradient(to bottom right, #ebf5fb, #d6eaf8);"); // Bleu clair
                break;
        }

        // Titre de la tâche
        Label titleLabel = new Label(tache.getNom_tache());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Description
        Label descLabel = new Label(tache.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 14px;");

        // Dates
        HBox datesBox = new HBox(10);

        Label dateDebutLabel = new Label("Début: " +
            (tache.getDate_debut() != null ? tache.getDate_debut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dateDebutLabel.setStyle("-fx-font-size: 12px;");

        Label dateFinLabel = new Label("Fin: " +
            (tache.getDate_fin() != null ? tache.getDate_fin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dateFinLabel.setStyle("-fx-font-size: 12px;");

        datesBox.getChildren().addAll(dateDebutLabel, dateFinLabel);

        // État actuel
        Label etatLabel = new Label("État: " + tache.getEtat());
        etatLabel.getStyleClass().add("state-badge");
        etatLabel.getStyleClass().add(tache.getEtat().toLowerCase().replace("_", "-"));
        etatLabel.setPadding(new Insets(5, 10, 5, 10));
        etatLabel.setStyle("-fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 12px;");

        // Projet
        Label projetLabel = new Label("Projet: " + (tache.getNomProjet() != null ? tache.getNomProjet() : "N/A"));
        projetLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        // Boutons pour changer l'état
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button enCoursBtn = new Button("En cours");
        enCoursBtn.getStyleClass().add("btn-info");
        enCoursBtn.setDisable("EN_COURS".equals(tache.getEtat()));
        enCoursBtn.setOnAction(e -> updateTaskState(tache, "EN_COURS"));

        Button termineBtn = new Button("Terminé");
        termineBtn.getStyleClass().add("btn-success");
        termineBtn.setDisable("TERMINE".equals(tache.getEtat()));
        termineBtn.setOnAction(e -> updateTaskState(tache, "TERMINE"));

        // Bouton pour ajouter une excuse si la tâche est en retard
        if ("EN_RETARD".equals(tache.getEtat())) {
            Button excuseBtn = new Button("Ajouter excuse");
            excuseBtn.getStyleClass().add("btn-warning");
            excuseBtn.setOnAction(e -> showExcuseDialog(tache));
            buttonsBox.getChildren().addAll(enCoursBtn, termineBtn, excuseBtn);
        } else {
            buttonsBox.getChildren().addAll(enCoursBtn, termineBtn);
        }

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(titleLabel, descLabel, datesBox, etatLabel, projetLabel, buttonsBox);

        // Ajouter un effet d'ombre
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8.0);
        shadow.setOffsetX(2.0);
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.color(0.4, 0.4, 0.4, 0.3));
        card.setEffect(shadow);

        // Ajouter un tooltip avec la description complète
        Tooltip tooltip = new Tooltip(tache.getDescription());
        Tooltip.install(card, tooltip);

        return card;
    }

    /**
     * Affiche une boîte de dialogue pour saisir une excuse pour une tâche en retard.
     * @param tache La tâche concernée
     */
    private void showExcuseDialog(Tache tache) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Excuse pour retard");
        dialog.setHeaderText("Tâche: " + tache.getNom_tache());
        dialog.setContentText("Veuillez entrer votre excuse pour le retard:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(excuse -> {
            try {
                // Vérifier si la classe Tache a une méthode pour définir un commentaire
                // Si non, nous pouvons stocker l'excuse dans un autre champ existant
                // Par exemple, nous pouvons utiliser la description pour ajouter l'excuse
                String updatedDescription = tache.getDescription() + "\n\nExcuse: " + excuse;
                tache.setDescription(updatedDescription);

                // Mettre à jour la tâche dans la base de données
                serviceTache.modifier(tache);

                // Envoyer une notification à l'administrateur
                if (mainController != null && mainController.getCurrentUser() != null) {
                    User currentUser = mainController.getCurrentUser();
                    String message = "L'employé " + currentUser.getPrenom() + " " + currentUser.getNom() +
                                    " a fourni une excuse pour la tâche en retard: " + tache.getNom_tache();

                    // Récupérer les administrateurs pour leur envoyer la notification
                    ServiceUser serviceUser = new ServiceUser();
                    List<User> admins = serviceUser.findByRole("ADMIN");

                    ServiceNotification serviceNotification = new ServiceNotification();
                    for (User admin : admins) {
                        Notification notification = new Notification(
                            message,
                            admin.getId(),
                            "EXCUSE_RETARD"
                        );
                        serviceNotification.ajouter(notification);
                    }
                }

                showAlert("Succès", "Votre excuse a été enregistrée et envoyée à l'administrateur.");

                // Recharger les tâches pour mettre à jour l'affichage
                loadTaches();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement de l'excuse", e);
                showAlert("Erreur", "Impossible d'enregistrer l'excuse: " + e.getMessage());
            }
        });
    }

    private Label createDateLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        return label;
    }

    private String getStateStyleEmploye(String etatAffiche) {
        String baseStyle = "-fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;";
        if ("occupée".equals(etatAffiche)) {
            return baseStyle + "-fx-background-color: #f1c40f; -fx-text-fill: #2c3e50;";
        } else {
            return baseStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;";
        }
    }

    private void updateTaskState(Tache tache, String newEtat) {
        try {
            String oldEtat = tache.getEtat();
            tache.setEtat(newEtat);
            serviceTache.modifier(tache);

            // Recharger les tâches
            loadTaches();

            // Envoyer une notification si la tâche est terminée
            if ("TERMINE".equals(newEtat) && !"TERMINE".equals(oldEtat)) {
                User currentUser = mainController.getCurrentUser();
                String message = "L'employé " + currentUser.getPrenom() + " " + currentUser.getNom() +
                                " a terminé la tâche: " + tache.getNom_tache();

                // Récupérer les administrateurs pour leur envoyer la notification
                ServiceUser serviceUser = new ServiceUser();
                List<User> admins = serviceUser.findByRole("ADMIN");

                ServiceNotification serviceNotification = new ServiceNotification();
                for (User admin : admins) {
                    Notification notification = new Notification(
                        message,
                        admin.getId(),
                        "TACHE_TERMINEE"
                    );
                    serviceNotification.ajouter(notification);
                }

                showAlert("Succès", "La tâche a été marquée comme terminée et les administrateurs ont été notifiés.");
            } else {
                showAlert("Succès", "L'état de la tâche a été mis à jour.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'état de la tâche", e);
            showAlert("Erreur", "Impossible de mettre à jour l'état de la tâche: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            LOGGER.info("Déconnexion de l'utilisateur");
            if (mainController != null) {
                mainController.handleLogout();
            } else {
                LOGGER.warning("mainController est null, impossible de se déconnecter");
                showAlert("Erreur", "Impossible de se déconnecter. Veuillez réessayer.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la déconnexion", e);
            showAlert("Erreur", "Une erreur est survenue lors de la déconnexion: " + e.getMessage());
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;

        // Charger les tâches et appliquer le CSS
        try {
            loadTaches();

            // Appliquer le CSS à la scène principale
            if (mainController != null) {
                Platform.runLater(() -> {
                    try {
                        // Récupérer la scène depuis le mainController
                        Parent mainPane = mainController.getMainPane();
                        if (mainPane != null && mainPane.getScene() != null) {
                            String cssPath = getClass().getResource("/styles/dashboard.css").toExternalForm();
                            if (!mainPane.getScene().getStylesheets().contains(cssPath)) {
                                mainPane.getScene().getStylesheets().add(cssPath);
                                LOGGER.info("CSS appliqué à la scène principale");
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Impossible d'appliquer le CSS à la scène principale", e);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation avec le mainController", e);
        }
    }

    private void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Appliquer un style personnalisé à l'alerte
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            dialogPane.getStyleClass().add("alert-dialog");

            alert.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'affichage de l'alerte: " + message, e);
        }
    }

    /**
     * Initialise les données du tableau de bord employé.
     * Cette méthode est appelée après que le contrôleur principal ait été défini.
     */
    public void initData() {
        try {
            LOGGER.info("Initialisation des données du tableau de bord");
            if (mainController != null && mainController.getCurrentUser() != null) {
                User currentUser = mainController.getCurrentUser();
                if (welcomeLabel != null) {
                    welcomeLabel.setText("Bienvenue, " + currentUser.getNom() + " " + currentUser.getPrenom());
                }
                loadDashboard(); // Charger toutes les données du tableau de bord

                // Appliquer à nouveau le CSS après le chargement des données
                Platform.runLater(() -> {
                    try {
                        // Appliquer le CSS à la scène
                        if (welcomeLabel != null && welcomeLabel.getScene() != null) {
                            String cssPath = getClass().getResource("/styles/dashboard.css").toExternalForm();
                            if (!welcomeLabel.getScene().getStylesheets().contains(cssPath)) {
                                welcomeLabel.getScene().getStylesheets().add(cssPath);
                                LOGGER.info("CSS appliqué à la scène depuis initData");
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Impossible d'appliquer le CSS depuis initData", e);
                    }
                });
            } else {
                LOGGER.warning("Impossible d'initialiser les données : mainController ou currentUser est null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation des données", e);
            showAlert("Erreur", "Une erreur est survenue lors de l'initialisation des données: " + e.getMessage());
        }
    }

    /**
     * Définit l'utilisateur actuel
     * @param user l'utilisateur actuel
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Mettre à jour l'interface utilisateur avec les informations de l'utilisateur
        if (welcomeLabel != null && user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom());
        }
    }

    /**
     * Gère le clic sur le bouton Retour.
     */
    @FXML
    private void handleRetour() {
        if (mainController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                Parent view = loader.load();

                AdminDashboardController controller = loader.getController();
                controller.setMainController(mainController);
                if (mainController.getCurrentUser() != null) {
                    controller.setCurrentUser(mainController.getCurrentUser());
                }
                controller.initData();

                mainController.setCenter(view);
                LOGGER.info("Retour au tableau de bord admin réussi");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors du chargement du dashboard admin", e);
                showAlert("Erreur", "Impossible de charger le dashboard admin: " + e.getMessage());
            }
        } else {
            LOGGER.warning("mainController est null, impossible de naviguer vers le tableau de bord admin");
        }
    }
}
