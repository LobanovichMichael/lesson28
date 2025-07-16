package org.example.lesson28;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    AnchorPane root = new AnchorPane();

    private GridPane grid = new GridPane();
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
        setupGridConstraints(grid);
        grid.setLayoutX(50);
        grid.setLayoutY(50);
        root.getChildren().add(grid);
        createPolygon();
        initializeField();
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

    private void createPolygon() {
        Polygon polygon = new Polygon();
        currentPolygonIndex = rand.nextInt(polygons.length);
//        currentPolygonIndex = 1;
        polygon.getPoints().addAll(polygons[currentPolygonIndex]);
        polygon.setFill(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        polygon.setLayoutX(600);
        polygon.setLayoutY(300);
        setupDragHandlers(polygon);
        root.getChildren().add(polygon);
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
            if (currentX < 550 && currentX > 42 && currentY < 550 && currentY > 42 && canBePlaced(currentI, currentJ)) {

                fillField(currentI, currentJ, polygon.getFill());
                polygon.setOnMouseDragged(null);
                polygon.setOnMousePressed(null);
                polygon.setOnMouseReleased(null);
//                polygon.setTranslateX(0);
//                polygon.setTranslateY(0);
//                polygon.setLayoutX(50 + (currentI * 50) + currentI * (0.5));
//                polygon.setLayoutY(50 + (currentJ * 50) + currentJ * (0.5));



                polygon.setTranslateX(0);
                polygon.setTranslateY(0);
                polygon.setLayoutX(0);
                polygon.setLayoutY(0);
                root.getChildren().remove(polygon);
//                grid.add(polygon, currentI, currentJ);
//                GridPane.setHalignment(polygon, HPos.LEFT);
//                GridPane.setValignment(polygon, VPos.TOP);
                createPolygon();
            } else {
                polygon.setTranslateX(initialTranslateX[0]);
                polygon.setTranslateY(initialTranslateY[0]);
            }
        });
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
    }

    private void checkRowsAndColumns() {

    }

    private boolean canBePlaced(int i, int j) {
        if (field[i][j]) {
            return false;
        }
        int[] currentShift = polygonsShift[currentPolygonIndex];
        for (int k = 0; k < currentShift.length; k = k + 2) {
            if (i + currentShift[k] > 9 || j + currentShift[k + 1] > 9) {
                return false;
            }
            if (field[i + currentShift[k]][j + currentShift[k + 1]]) {
                return false;
            }
        }
        return true;
    }

    private void setupGridConstraints(GridPane gridPane) {
        // Настройка столбцов
        for (int i = 0; i < 10; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(50);
            colConst.setMaxWidth(50);
            colConst.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(colConst);
        }

        // Настройка строк
        for (int i = 0; i < 10; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(50);
            rowConst.setMaxHeight(50);
            rowConst.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(rowConst);
        }

        gridPane.setGridLinesVisible(true);
//        // Дополнительные настройки внешнего вида
//        gridPane.setHgap(3);
//        gridPane.setVgap(3);
    }
}