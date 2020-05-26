package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.ShapeGraphRoot;
import fi.utu.tech.visualnotes.graphics.ShapeGraphView;
import fi.utu.tech.visualnotes.graphics.shapes.Oval;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import scalafx.scene.input.KeyCode;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class VisualNotesController {


    @FXML
    private Canvas canvas;

    @FXML
    private Rectangle selectedColor;

    @FXML
    private ChoiceBox<String> colorbox;

    private enum Muoto {
        LINE,
        CIRCLE,
        SQUARE
    }

    Muoto muoto;

    //g.setFill(colorPicker.getValue());


    public static Boolean fill;
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

    public static boolean getFill(){
        return fill;
    }

    ObservableList<String> colorList = FXCollections.observableArrayList(Color.names());


    //TODO tärkeys 1: skrollaus - älä unohda spatiaalisen rakenteen näkymän vieritystä

    //TODO tärkeys 1: värin valinta

    //POPUP? listaan palloja missä värit?

    //TODO tärkeys 1: asynkroonisuus tallennus ja lataus

    //TODO tärkeys 2: lisää kuvakkeille tooltip tekstit!

    // https://stackoverflow.com/questions/41459107/how-to-show-a-title-for-image-when-i-hover-over-it-in-javafx

    @FXML
    public void initialize(){

        System.out.println("initialize");
        //color = Color.Black;
        //colorbox.setValue("Black");
        colorbox.setItems(colorList);
        //colorbox.getItems().setAll(Color.values());

        colorbox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            System.out.println(newValue);
            //valitaan väri
            color = Color.valueOf(newValue);
            selectedColor.setFill(color.toFx());
        });

        view = new ShapeGraphView(root);
        view.setView(0,0, canvas.getWidth(), canvas.getHeight());

        shapes = view.visibleShapes();

        graphicsContext = canvas.getGraphicsContext2D();


    } //init

    public void handleColor(ContextMenuEvent contextMenuEvent) {
        System.out.println("handle color");

        System.out.println(contextMenuEvent.getPickResult());
        System.out.println("color name " + color.name());
        System.out.println("colorbox value " + colorbox.getValue());
        //valitaan väri
        color = Color.valueOf(colorbox.getValue());
        //näytetään valittu väri
        selectedColor.setFill(color.toFx());
    }

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
        if (muoto != null) {
            System.out.println("shape: " + shapeType + " color: " + color + " start: " + pointStart + " end: " + pointEnd);
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
                graphicsContext.clearRect(0,0, canvas.getHeight(), canvas.getWidth());
                drawAll();

                //a.render(graphicsContext, view.offset());
                //System.out.println("lisätty a " + shapes.toArray());

                System.out.println("lisätty a, shapes pituus " + shapes.size());

            }

        }

    }//released

    //TODO tärkeys:3 - muoto ei katoa kun siirretään
    //muodon ei tarvitse mennä roottiin ennen kuin hiiri pysähtyy!
    //Selvitä paras ratkaisu: säikeet? animaatio? mikä muu?
    @FXML
    public void mouseDragged(MouseEvent me) {
        //System.out.println("mouse drag event");


    }

    public void drawAll () {
        for (Shape s: shapes){
            s.render(graphicsContext, view.offset());
        }
    }

    //vaihtaa kursorin move-muotoon ja muodon nulliksi jottei muotoja voi piirtää
    public void handleMoveClicked(MouseEvent mouseEvent) {
        //System.out.println("drag" +muoto);
        muoto = null;
        //System.out.println("drag" +muoto);
        canvas.setCursor(Cursor.MOVE);
    }

    public void handleLoadClicked(ActionEvent actionEvent) {

        //TODO tärkeys: 1 - lataaminen: tiedoston valinta
        //file chooser!
        System.out.println("load clicked");
        try {
            root.load(Paths.get("test.vin"));
            shapes = view.visibleShapes();
            drawAll();
        }
        catch (Exception e) {
            System.out.println("load failed " + e.getMessage());
        }


    }

    public void handleSaveClicked(ActionEvent actionEvent) {

        //TODO tärkeys: 1 - tallennus: tiedoston valinta
        //filechooser
        System.out.println("savessa");

        try {
            /*
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "vin", new File("paint.png"));
            System.out.println("saved");
             */

            root.save(Paths.get("test.vin"));

        }catch (Exception e){
            System.out.println("save failed");
        }


    }//handle save

    public void handleExit () {
        Platform.exit();
    }


    public void handleReset(ActionEvent actionEvent) {
        System.out.println("handle reset");
        root.clear();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void handleAbout(ActionEvent actionEvent) {
        System.out.println("Harjoitustyö D - Mirva Tapola");
    }

    public void handleLine(MouseEvent mouseEvent) {
        //System.out.println("line");
        fill = true;
        muoto = Muoto.LINE;
        shapeType = Shape.ShapeType.Line;
        //System.out.println("line" + fill);
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleSquareFilled(MouseEvent mouseEvent) {
        //System.out.println("squera filled before " + fill);
        fill = true;
        muoto = Muoto.SQUARE;
        shapeType = Shape.ShapeType.Rectangle;
        //System.out.println("squera filled after " + fill);
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleSquareStroke(MouseEvent mouseEvent) {
        //System.out.println("square stroke before " + fill);
        fill = false;
        muoto = Muoto.SQUARE;
        shapeType = Shape.ShapeType.Rectangle;
        //System.out.println("square stroke after " + fill);
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleCircleFilled(MouseEvent mouseEvent) {
        fill = true;
        muoto = Muoto.CIRCLE;
        shapeType = Shape.ShapeType.Oval;
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleCircleStroke(MouseEvent mouseEvent) {
        fill = false;
        muoto = Muoto.CIRCLE;
        shapeType = Shape.ShapeType.Oval;
        canvas.setCursor(Cursor.DEFAULT);
    }

}
