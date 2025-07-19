package org.example.lesson28;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    @FXML
    AnchorPane root = new AnchorPane();

    @FXML
    Label score = new Label();
    int scoreValue;

    private GridPane grid;
    private boolean[][] field = new boolean[10][];
    private Double[][] polygons = {
            {0.0, 0.0, 50.0, 0.0, 50.0, 50.0, 0.0, 50.0},
            {0.0, 0.0, 100.0, 0.0, 100.0, 100.0, 0.0, 100.0},
            {0.0, 0.0, 150.0, 0.0, 150.0, 50.0, 0.0, 50.0},
            {0.0, 0.0, 50.0, 0.0, 50.0, 100.0, 150.0, 100.0, 150.0, 150.0, 0.0, 150.0},
            {0.0, 0.0, 50.0, 0.0, 50.0, 150.0, 0.0, 150.0},
            {0.0, 0.0, 50.0, 0.0, 50.0, -50.0, 100.0, -50.0, 100.0, 50.0, 50.0, 50.0, 50.0, 100.0, 0.0, 100.0}
    };

    private int[][] polygonsShift = {
            {},
            {1, 0, 1, 1, 0, 1},
            {1, 0, 2, 0},
            {0, 1, 0, 2, 1, 2, 2, 2},
            {0, 1, 0, 2},
            {0, 1, 1, 0, 1, -1}
    };
    int currentPolygonIndex;
    private Random rand = new Random();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startGame();
    }

    private void startGame() {
        root.getChildren().remove(grid);
        grid = new GridPane();
        setupGrid(grid);
        root.getChildren().add(grid);
        createPolygon();
        initializeField();
        scoreValue = 0;
        score.setText("Score: " + scoreValue);
    }


    private void initializeField() {
        for (int i = 0; i < 10; i++) {
            field[i] = new boolean[10];
            for(int j = 0; j < 10; j++ ) {
                field[i][j] = false;
            }
        }
        System.out.println(Arrays.deepToString(field));
    }

    private Polygon createPolygon() {
        Polygon polygon = new Polygon();
        currentPolygonIndex = rand.nextInt(polygons.length);
//        currentPolygonIndex = 1;
        polygon.getPoints().addAll(polygons[currentPolygonIndex]);
        polygon.setFill(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        polygon.setLayoutX(600);
        polygon.setLayoutY(300);
        setupDragHandlers(polygon);
        root.getChildren().add(polygon);
        return polygon;
    }

    private void setupDragHandlers(Polygon polygon) {
        final double[] anchorX = {0};
        final double[] anchorY = {0};
        final double[] initialTranslateX = {0};
        final double[] initialTranslateY = {0};

        polygon.setOnMousePressed(event -> {
            anchorX[0] = event.getSceneX();
            anchorY[0] = event.getSceneY();
            initialTranslateX[0] = polygon.getTranslateX();
            initialTranslateY[0] = polygon.getTranslateY();
        });

        polygon.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - anchorX[0];
            double offsetY = event.getSceneY() - anchorY[0];
            polygon.setTranslateX(initialTranslateX[0] + offsetX);
            polygon.setTranslateY(initialTranslateY[0] + offsetY);
        });

        polygon.setOnMouseReleased(event -> {

            // СДЕЛАТЬ ПОДСЧЕТ ИСХОДЯ ИЗ ЦЕНТРА ФИГУРЫ
            double currentX = polygon.getLayoutX() + polygon.getTranslateX();
            double currentY = polygon.getLayoutY() + polygon.getTranslateY();
            int currentI = (int) ((currentX - 42) / 50);
            int currentJ = (int) ((currentY - 42) / 50);
            System.out.println(currentI + " " + currentJ);
            if (canBePlaced(currentI, currentJ)) {
                fillField(currentI, currentJ, polygon.getFill());
                root.getChildren().remove(polygon);
                checkRowsAndColumns();
                score.setText("Score: " + scoreValue);
                Polygon newPolygon = createPolygon();
                if (isEnd()) {
                    root.getChildren().remove(newPolygon);
                    showEndGameMessage();
                }
            } else {
                polygon.setTranslateX(initialTranslateX[0]);
                polygon.setTranslateY(initialTranslateY[0]);
            }
        });
    }

    private void showEndGameMessage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Конец игры!");
        alert.setHeaderText(null);
        alert.setContentText("Игра окончена! Ваш счет: " + scoreValue + ". Желаете сыграть еще раз?");
        ButtonType playAgain = new ButtonType("Сыграть еще раз");
        ButtonType leave = new ButtonType("Выйти");
        alert.getButtonTypes().setAll(playAgain, leave);
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.isPresent() && answer.get() == playAgain) {
            startGame();
        } else {
            Platform.exit();
        }
    }

    private void fillField(int i, int j, Paint color) {
        field[i][j] = true;
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(polygons[0]);
        polygon.setFill(color);
        grid.add(polygon, i, j);
        int[] currentShift = polygonsShift[currentPolygonIndex];
        for (int k = 0; k < currentShift.length; k = k + 2) {
            field[i + currentShift[k]][j + currentShift[k + 1]] = true;
            polygon = new Polygon();
            polygon.getPoints().addAll(polygons[0]);
            polygon.setFill(color);
            grid.add(polygon, i + currentShift[k], j + currentShift[k + 1]);
        }
        scoreValue += currentShift.length / 2 + 1;
    }

    private void checkRowsAndColumns() {
        List<Integer> filledRows = new ArrayList<>();
        List<Integer> filledCols = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            boolean colFilled = true;
            for (int j = 0; j < 10; j++) {
                if (!field[i][j]) {
                    colFilled = false;
                    break;
                }
            }
            if (colFilled) filledCols.add(i);
        }

        for (int j = 0; j < 10; j++) {
            boolean rowFilled = true;
            for (int i = 0; i < 10; i++) {
                if (!field[i][j]) {
                    rowFilled = false;
                    break;
                }
            }
            if (rowFilled) filledRows.add(j);
        }

        if (!filledRows.isEmpty() || !filledCols.isEmpty()) {
            scoreValue += filledRows.size() * 10 + filledCols.size() * 10;
//            System.out.println("DELETING ROWS" + filledRows.toString());
//            System.out.println("DELETING COLS" + filledCols.toString());
//            System.out.println("Grid children: " + grid.getChildren().size());
//            System.out.println("Grid width: " + grid.getWidth() + ", height: " + grid.getHeight());
            for (int j : filledRows) {
                for (int i = 0; i < 10; i++) {
                    field[i][j] = false;
                }
            }
            for (int i : filledCols) {
                for (int j = 0; j < 10; j++) {
                    field[i][j] = false;
                }
            }

            List<Node> nodesToRemove = new ArrayList<>();
            for (Node node : grid.getChildren()) {
                Integer rowIdx = GridPane.getRowIndex(node);
                Integer colIdx = GridPane.getColumnIndex(node);

                if (rowIdx == null) rowIdx = -1;
                if (colIdx == null) colIdx = -1;

                if (filledRows.contains(rowIdx) || filledCols.contains(colIdx)) {
                    nodesToRemove.add(node);
                }
            }
            grid.getChildren().removeAll(nodesToRemove);
        }
    }

    private boolean isEnd() {
        boolean end = true;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (canBePlaced(i, j)) {
                    end = false;
                    break;
                }
            }
        }
        return end;
    }

    private boolean canBePlaced(int i, int j) {
        if (i >= 10 || j >= 10 || i < 0 || j < 0 || field[i][j]) {
            return false;
        }
        int[] currentShift = polygonsShift[currentPolygonIndex];
        for (int k = 0; k < currentShift.length; k = k + 2) {
            if (i + currentShift[k] > 9 || j + currentShift[k + 1] > 9 || i + currentShift[k] < 0 || j + currentShift[k + 1] < 0) {
                return false;
            }
            if (field[i + currentShift[k]][j + currentShift[k + 1]]) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param gridPane -
     */
    private void setupGrid(GridPane gridPane) {
        grid.setLayoutX(50);
        grid.setLayoutY(50);
        // Настройка столбцов
        for (int i = 0; i < 10; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(50);
            colConst.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(colConst);
        }

        // Настройка строк
        for (int i = 0; i < 10; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(50);
            rowConst.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(rowConst);
        }

        gridPane.setGridLinesVisible(true);
//        // Дополнительные настройки внешнего вида
//        gridPane.setHgap(3);
//        gridPane.setVgap(3);
    }
}