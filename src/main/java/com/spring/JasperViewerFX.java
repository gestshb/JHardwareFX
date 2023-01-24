/*
 * Copyright (C) 2017 Gustavo Fragoso
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.spring;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * An simple approach to JasperViewer in JavaFX. Based on Michael  approach.
 *
 * @author Gustavo Fragoso
 * @date Aug 09, 2017
 */


public class JasperViewerFX {


    private Stage dialog;
    private Button print, save, backPage, firstPage, nextPage, lastPage, zoomIn, zoomOut;
    private Label bottomLabel;
    private ImageView reportImage;
    private TextField txtPage;

    // JasperReports variables
    private JasperReport jReport;
    private JasperPrint jPrint;

    private int imageHeight = 0, imageWidth = 0, reportPages = 0;

    // Property
    private IntegerProperty currentPage;
    private float zoom = 1.3f;

    public JasperViewerFX init(String jasper, Map params, Connection source) {
        return init(jasper, params, null, source);

    }

    public JasperViewerFX init(String jasper, Map params, JRDataSource source) {
        return init(jasper, params, source, null);
    }

    public JasperViewerFX init(String jasper, Map params, JRDataSource dataSource, Connection connection) {

        // Initializing window
        dialog = new Stage();
        dialog.setMaximized(true);
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setScene(createScene());
        dialog.setTitle("ورقة طباعة");


        try {
            InputStream in = getClass().getResourceAsStream(jasper);
             //jReport = JasperCompileManager.compileReport(in);
           jReport = (JasperReport) JRLoader.loadObject(in);
            if (dataSource != null)
                jPrint = JasperFillManager.fillReport(jReport, params, dataSource);
            else if (connection != null)
                jPrint = JasperFillManager.fillReport(jReport, params, connection);
            imageHeight = jPrint.getPageHeight() + 284;
            imageWidth = jPrint.getPageWidth() + 201;
            reportPages = jPrint.getPages().size();

        } catch (JRException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    private int getCurrentPage() {
        return currentPage.get();
    }

    // ***********************************************
    // Property
    // ***********************************************
    private void setCurrentPage(int page) {
        currentPage.set(page);
        viewPage(page);
    }

    // ***********************************************
    // Methods
    // ***********************************************
    private Scene createScene() {
        HBox menu = new HBox(5);
        menu.setAlignment(Pos.CENTER);
        menu.setPrefHeight(50.0);

        // Menu's buttons
        print = new Button(null, new ImageView(getClass().getResource("/icons/printer.png").toExternalForm()));
        save = new Button(null, new ImageView(getClass().getResource("/icons/save.png").toExternalForm()));
        backPage = new Button(null, new ImageView(getClass().getResource("/icons/backing.png").toExternalForm()));
        firstPage = new Button(null, new ImageView(getClass().getResource("/icons/firstImg.png").toExternalForm()));
        nextPage = new Button(null, new ImageView(getClass().getResource("/icons/nextImg.png").toExternalForm()));
        lastPage = new Button(null, new ImageView(getClass().getResource("/icons/lastImg.png").toExternalForm()));
        zoomIn = new Button(null, new ImageView(getClass().getResource("/icons/zoomIn.png").toExternalForm()));
        zoomOut = new Button(null, new ImageView(getClass().getResource("/icons/zoomOut.png").toExternalForm()));

        // Pref sizes
        print.setPrefSize(30, 30);
        save.setPrefSize(30, 30);
        backPage.setPrefSize(30, 30);
        firstPage.setPrefSize(30, 30);
        nextPage.setPrefSize(30, 30);
        lastPage.setPrefSize(30, 30);
        zoomIn.setPrefSize(30, 30);
        zoomOut.setPrefSize(30, 30);

        txtPage = new TextField("1");
        txtPage.setPrefSize(75, 30);

        menu.getChildren().addAll(print, save, firstPage, backPage, txtPage, nextPage, lastPage, zoomIn, zoomOut);

        // This imageView will preview the pdf inside scrollpane
        reportImage = new ImageView();
        reportImage.setFitHeight(imageHeight);
        reportImage.setFitWidth(imageWidth);

        Group contentGroup = new Group();
        contentGroup.getChildren().add(reportImage);

        StackPane stack = new StackPane(contentGroup);
        stack.setAlignment(Pos.CENTER);
        stack.setStyle("-fx-background-color: #cacaca");

        ScrollPane scroll = new ScrollPane(stack);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        // Bottom label
        bottomLabel = new Label();

        BorderPane root = new BorderPane();


        root.setCenter(scroll);

        root.setTop(menu);

        root.setBottom(bottomLabel);


        return new Scene(root, 1024, 768);
    }

    private void start() {
        currentPage = new SimpleIntegerProperty(this, "currentPage");
        setCurrentPage(1);

        // Bottom label
        bottomLabel.setText("Page 1 of " + reportPages);

        // Visual feedback of reading progress
        currentPage.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            bottomLabel.setText("Page " + newValue + " of " + reportPages);
            txtPage.setText(newValue.toString());

            if (newValue.intValue() == 1) {
                backPage.setDisable(true);
                firstPage.setDisable(true);
            }

            if (newValue.intValue() == reportPages) {
                nextPage.setDisable(true);
                lastPage.setDisable(true);
            }
        });

        // Those buttons must start disabled
        backPage.setDisable(true);
        firstPage.setDisable(true);

        // With only one page those buttons are unnecessary
        if (reportPages == 1) {
            nextPage.setDisable(true);
            lastPage.setDisable(true);
        }

        backPage.setOnAction((ActionEvent event) -> {
            backAction();
        });

        firstPage.setOnAction((ActionEvent event) -> {
            firstPageAction();
        });

        nextPage.setOnAction((ActionEvent event) -> {
            nextAction();
        });

        lastPage.setOnAction((ActionEvent event) -> {
            lastPageAction();
        });

        print.setOnAction((ActionEvent event) -> {
            printAction();
        });

        save.setOnAction((ActionEvent event) -> {
            saveAction();
        });
        zoomIn.setOnAction((ActionEvent event) -> {
            zoomInAction();
        });

        zoomOut.setOnAction((ActionEvent event) -> {
            zoomOutAction();
        });

        txtPage.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int p = Integer.parseInt(txtPage.getText());
                    if (p > reportPages) {
                        setCurrentPage(reportPages);
                    } else {
                        if (p > 0) {
                            setCurrentPage(p);
                        } else {
                            setCurrentPage(1);
                        }
                    }
                } catch (NumberFormatException e) {
                    Alert dialog1 = new Alert(Alert.AlertType.WARNING, "Invalid number", ButtonType.OK);
                    dialog1.show();
                }
            }
        });
    }

    private void backAction() {
        int newValue = getCurrentPage() - 1;
        setCurrentPage(newValue);

        // Turn forward buttons on again
        if (nextPage.isDisabled()) {
            nextPage.setDisable(false);
            lastPage.setDisable(false);
        }
    }

    private void firstPageAction() {
        setCurrentPage(1);

        // Turn forward buttons on again
        if (nextPage.isDisabled()) {
            nextPage.setDisable(false);
            lastPage.setDisable(false);
        }
    }

    private void nextAction() {
        int newValue = getCurrentPage() + 1;
        setCurrentPage(newValue);

        // Turn previous button on again
        if (backPage.isDisabled()) {
            backPage.setDisable(false);
            firstPage.setDisable(false);
        }
    }

    private void lastPageAction() {
        setCurrentPage(reportPages);

        // Turn previous button on again
        if (backPage.isDisabled()) {
            backPage.setDisable(false);
            firstPage.setDisable(false);
        }
    }

    // Calls default printer action
    private void printAction() {
        try {
            JasperPrintManager.printReport(jPrint, true);
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void saveAction() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("Portable DocumentType Format (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter html = new FileChooser.ExtensionFilter("HyperText Markup Language", "*.html");
        FileChooser.ExtensionFilter xml = new FileChooser.ExtensionFilter("eXtensible Markup Language", "*.xml");
        FileChooser.ExtensionFilter xls = new FileChooser.ExtensionFilter("Microsoft Excel 2007", "*.xls");
        FileChooser.ExtensionFilter xlsx = new FileChooser.ExtensionFilter("Microsoft Excel 2016", "*.xlsx");
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("Comma-separeted Values", "*.csv");
        chooser.getExtensionFilters().addAll(pdf, html, xml, xls, xlsx, csv);

        chooser.setTitle("اخترالملف");
        chooser.setSelectedExtensionFilter(pdf);
        File file = chooser.showSaveDialog(dialog);

        if (file != null) {
            List<String> box = chooser.getSelectedExtensionFilter().getExtensions();
            switch (box.get(0)) {
                case "*.pdf":
                    try {
                        JasperExportManager.exportReportToPdfFile(jPrint, file.getPath());
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "*.html":
                    try {
                        JasperExportManager.exportReportToHtmlFile(jPrint, file.getPath());
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "*.xml":
                    try {
                        JasperExportManager.exportReportToXmlFile(jPrint, file.getPath(), false);
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "*.xls":
                    try {
                        JRXlsExporter exporter = new JRXlsExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "*.xlsx":
                    try {
                        JRXlsxExporter exporter = new JRXlsxExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "*.csv":
                    try {
                        JRCsvExporter exporter = new JRCsvExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jPrint));
                        exporter.setExporterOutput(new SimpleWriterExporterOutput(file));
                        exporter.exportReport();
                    } catch (JRException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void show() {
        if (reportPages > 0) {
            start();
            dialog.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "لا يوجد شيء للطباعة", ButtonType.CLOSE);
            alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            alert.setHeaderText("آسف");
            alert.show();
        }
    }

    public JasperViewerFX zoom(float value) {
        this.zoom = value;
        return this;
    }

    private void viewPage(int page) {
        try {


            BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jPrint, page - 1, zoom);
            WritableImage fxImage = new WritableImage(imageHeight, imageWidth);
            reportImage.setImage(SwingFXUtils.toFXImage(image, fxImage));
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void zoomInAction() {
        reportImage.setScaleX(reportImage.getScaleX() + 0.15);
        reportImage.setScaleY(reportImage.getScaleY() + 0.15);
        reportImage.setFitHeight(imageHeight + 0.15);
        reportImage.setFitWidth(imageWidth + 0.15);
    }

    private void zoomOutAction() {
        reportImage.setScaleX(reportImage.getScaleX() - 0.15);
        reportImage.setScaleY(reportImage.getScaleY() - 0.15);
        reportImage.setFitHeight(imageHeight - 0.15);
        reportImage.setFitWidth(imageWidth - 0.15);
    }

}
