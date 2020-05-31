package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.ShapeGraphRoot;
import fi.utu.tech.visualnotes.graphics.ShapeGraphView;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public class VisualNotesController {


    public MenuItem save;
    public MenuBar menu;
    public Circle colorBall;
    public ScrollPane scrollPane;

    @FXML
    protected Canvas canvas;
/*
    Takas jos pallerot ei toimikaan
    @FXML
    private Rectangle selectedColor;
    @FXML
    private ChoiceBox<String> colorbox;

 */
    public Boolean fill;
    public GraphicsContext graphicsContext;
    public Point2D pointStart;
    public Point2D pointEnd;
    public Point2D drag;
    public Shape.ShapeType shapeType;
    public ShapeGraphRoot root = new ShapeGraphRoot();
    public ShapeGraphView view;
    public Optional<Shape> moving;
    protected Shape shape;
    protected Collection<Shape> shapes;
    protected double height;
    protected double width;
    protected Color color;
    protected ObservableList<String> colorList = FXCollections.observableArrayList(Color.names());
    private FileChooser fileChooser = new FileChooser();
    private File file = null;
    protected double viewX = 0;
    protected double viewY = 0;

    //TODO tärkeys 3: menubar style

    @FXML
    public void initialize(){

        System.out.println("initialize");

        //asetetaan musta väriksi jotta vältytään nullin tuomalta värin muutos ongelmalta
        setColor("Black");

        view = new ShapeGraphView(root);
        view.setView(viewX, viewY, canvas.getWidth(), canvas.getHeight());
        shapes = view.visibleShapes();
        graphicsContext = canvas.getGraphicsContext2D();

        scrollPane.setContent(canvas);

        //System.out.println("scrollpane " + scrollPane.vvalueProperty());

        //travitsee lisää säätöä, toimii jotenkin
        scrollPane.vvalueProperty().addListener((observableValue, oldValue, newValue) -> {
            //viewX + newValue;
            double apu = (Double)newValue - (Double)oldValue;
            //System.out.println("Apu " + apu);
            apu = apu * 10;
            //viewX = viewX + apu;
            viewY = viewY + apu;
            view.setView(viewX, viewY, canvas.getWidth(), canvas.getHeight());
            //System.out.println("scrollpane bounds " + scrollPane.getViewportBounds());
            //System.out.println("scroll v value " + scrollPane.vvalueProperty());
            shapes = view.visibleShapes();
            clearCanvas();
            drawAll();
        });


        //filechooser C:n juureen
        fileChooser.setInitialDirectory(new File("C:\\"));


    } //init


    @FXML
    void mousePressed (MouseEvent me) {

        //System.out.println("if pressed " + me.getX() + me.getY() + " tai  " + me.getSceneX() + me.getSceneY());
        pointStart = new Point2D(me.getX(), me.getY());
    }

    @FXML
    void mouseReleased (MouseEvent me) {

        //System.out.println("if released" + me.getX() + " " + me.getY());
        //gc.strokeLine(startX, startY, me.getSceneX(), me.getSceneY());
        pointEnd = new Point2D(me.getX(), me.getY());
        if (shapeType != null) {
            //System.out.println("shape: " + shapeType + " color: " + color + " start: " + pointStart + " end: " + pointEnd);
            shape = view.createShape(shapeType, fill, color, pointStart, pointEnd);
            root.add(shape);
            shape.render(graphicsContext, view.offset());
            shapes = view.visibleShapes();
            System.out.println("lisätty a " + shapes.size());

        }else {

            System.out.println("mouse release else " + pointStart);

            //tallennetaan extractShapen palauttama muoto:
            moving = view.extractShape(pointStart);
            //jos on muoto kohdassa
            if (moving.isPresent()){

                //System.out.println("drag, onko kohdassa muoto? " + moving.isPresent());
                //muuttuja drag saa arvon
                drag = new Point2D(pointEnd.sub(pointStart));

                //tehdään uusi shape joka hakee muodon
                Shape s = moving.get();

                height = s.bottomRight().y - s.topLeft().y;
                width = s.bottomRight().x - s.topLeft().x;

                //tallennetaan muotoon a moven palauttama muoto, lisätään ja renderöidään
                Shape a = s.move(drag);
                root.add(a);
                shapes = view.visibleShapes();

                //pyyhitään alue jolla muoto oli
                //graphicsContext.clearRect(s.topLeft().x, s.topLeft().y, width+1, height+1);

                //pyyhitään kaikki
                clearCanvas();

                drawAll();

                //System.out.println("lisätty a, shapes pituus " + shapes.size());

            }

        }

    }//released

    protected void clearCanvas () {
        graphicsContext.clearRect(0,0, canvas.getHeight(), canvas.getWidth());
    }

    //vaihtaa kursorin move-muotoon ja muodon nulliksi jottei muotoja voi piirtää
    public void handleMoveClicked(MouseEvent mouseEvent) {
        shapeType = null;
        canvas.setCursor(Cursor.MOVE);
    }

    public void handleLoadClicked(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Load alert!");
        alert.setHeaderText("Loading will clear the canvas, click cancel if you want to save your work before loading.");
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

        Window stage = this.menu.getScene().getWindow();
        fileChooser.setTitle("Load file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("vin files", "*.vin")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            root.clear();
            clearCanvas();
            File loadfile = file.getParentFile();
            fileChooser.setInitialDirectory(loadfile);
            String name = file.getName();
            String[] n = name.split("\\.");
            if (!n[0].isEmpty()) { //&& shapes.size() == 0
                new Thread(() -> {

                    try {
                        root.load(Paths.get(file.getAbsolutePath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("load failed " + e.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                        Platform.runLater(() -> {
                            shapes = view.visibleShapes();
                            drawAll();
                        });

                        System.out.println("load run try");

                }).start();
            }
            }
        } else {
            // ... user chose CANCEL or closed the dialog
            System.out.println("cancelled");
        }

    }

    Task <Void> saves = new Task<>() {
        @Override
        protected Void call() throws Exception {
            root.save(Paths.get(file.getAbsolutePath()));
            System.out.println("save task");
            return null;
        }
    };

    public void handleSaveClicked(ActionEvent actionEvent) {

        System.out.println("savessa");
        Window stage = menu.getScene().getWindow(); //get a handle to the stage
        fileChooser.setTitle("Save File"); //set the title of the Dialog window
        String defaultSaveName = "mySave";
        fileChooser.setInitialFileName(defaultSaveName); //set the default name for file to be saved
        //create extension filters. The choice will be appended to the end of the file name
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("vin Files", "*.vin"));
        try {
            //Actually displays the save dialog
            file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                File dir = file.getParentFile();//gets the selected directory
                //update the file chooser directory to user selected so the choice is "remembered"
                fileChooser.setInitialDirectory(dir);
                //System.out.println("save: " + file.getAbsolutePath());
                //System.out.println("save paths.get: " + file.getName());

                Thread th = new Thread(saves);
                th.setDaemon(true);
                th.start();
                //jäätymistestiä varten
                //root.save(Paths.get(file.getAbsolutePath()));

            }
        } catch (Exception e) {
            System.out.println("save failed: " + e.getMessage());
        }

    } //handle save



    public void handleExit () {
        Platform.exit();
    }


    public void handleReset(ActionEvent actionEvent) {
        System.out.println("handle reset");
        root.clear();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void handleLine(MouseEvent mouseEvent) {
        fill = true;
        shapeType = Shape.ShapeType.Line;
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleSquareFilled(MouseEvent mouseEvent) {
        fill = true;
        shapeType = Shape.ShapeType.Rectangle;
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleSquareStroke(MouseEvent mouseEvent) {
        fill = false;
        shapeType = Shape.ShapeType.Rectangle;
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleCircleFilled(MouseEvent mouseEvent) {
        fill = true;
        shapeType = Shape.ShapeType.Oval;
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleCircleStroke(MouseEvent mouseEvent) {
        fill = false;
        shapeType = Shape.ShapeType.Oval;
        canvas.setCursor(Cursor.DEFAULT);
    }

    //vähentää rivejä
    protected void setColor (String newColor) {
        color = Color.valueOf(newColor);
        //selectedColor.setFill(color.toFx());
        colorBall.setFill(color.toFx());
    }

    /*
    // siirto initistä selkeyden vuoksi
    protected void colorBoxListener () {
        colorbox.getSelectionModel().selectedItemProperty().addListener((v, oldColor, newColor) -> {
            System.out.println(newColor);
            //valitaan väri
            setColor(newColor);
            //color = Color.valueOf(newValue);
            //selectedColor.setFill(color.toFx());
        });
    }
     */

    //piirtää kaikki
    //TODO selvitä miksi ladatessa "layer" järjestys muuttuu? säikeet avuksi?
    private void drawAll () {
        for (Shape s: shapes){
            s.render(graphicsContext, view.offset());
        }
    }

    public void handleColorChange(MouseEvent mouseEvent) {
        String colorId = mouseEvent.getPickResult().getIntersectedNode().getId();
        System.out.println(colorId);
        setColor(colorId);
    }

        /*
    Takas jos pallerot ei toimikaan
    public void handleColor(ContextMenuEvent contextMenuEvent) {
        System.out.println("handle color");

        System.out.println(contextMenuEvent.getPickResult());
        System.out.println("color name " + color.name());
        System.out.println("colorbox value " + colorbox.getValue());
        //valitaan väri
        color = Color.valueOf(colorbox.getValue());
        //näytetään valittu väri
        selectedColor.setFill(color.toFx());
        //colorball.setFill(color.toFx());
    }

     */

}
