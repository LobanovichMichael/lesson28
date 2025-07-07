package org.example.lesson28;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    AnchorPane root = new AnchorPane();

    GridPane grid = new GridPane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGridConstraints(grid);
        grid.setLayoutX(50);
        grid.setLayoutY(50);
        root.getChildren().add(grid);
        createPolygon();
    }

    private void createPolygon() {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[] {
           0.0, 0.0,
           50.0, 0.0,
           50.0, 50.0,
           0.0, 50.0
        });
        polygon.setFill(Color.BLUE);
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

            if (currentX < 550 && currentX > 42 && currentY < 550 && currentY > 42) {
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
                grid.add(polygon, currentI, currentJ);
                createPolygon();
            } else {
                polygon.setTranslateX(initialTranslateX[0]);
                polygon.setTranslateY(initialTranslateY[0]);
            }
        });
    }

    private void setupGridConstraints(GridPane gridPane) {
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