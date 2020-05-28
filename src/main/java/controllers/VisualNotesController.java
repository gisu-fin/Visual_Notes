package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.ShapeGraphRoot;
import fi.utu.tech.visualnotes.graphics.ShapeGraphView;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public class VisualNotesController {


    @FXML
    private Canvas canvas;

    @FXML
    private Rectangle selectedColor;

    @FXML
    private ChoiceBox<String> colorbox;

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

    //TODO tärkeys 1: skrollaus - älä unohda spatiaalisen rakenteen näkymän vieritystä

    //TODO tärkeys 3: värin valinta - kauniimmaksi

    //POPUP? listaan palloja missä värit?

    //TODO tärkeys 1: asynkroonisuus tallennus ja lataus

    //TODO tärkeys 2: lisää kuvakkeille tooltip tekstit!

    // https://stackoverflow.com/questions/41459107/how-to-show-a-title-for-image-when-i-hover-over-it-in-javafx

    @FXML
    public void initialize(){

        System.out.println("initialize");
        // lisätään värit
        colorbox.setItems(colorList);
        // asetetaan musta oletukseksi näkyviin jotta käytettävyys paranee
        colorbox.setValue("Black");
        //asetetaan musta väriksi jotta vältytään nullin tuomalta värin muutos ongelmalta
        setColor("Black");

        // laitetaan colorbox kuuntelemaan valintoja
        colorBoxListener();

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
        if (shapeType != null) {
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


    //vaihtaa kursorin move-muotoon ja muodon nulliksi jottei muotoja voi piirtää
    public void handleMoveClicked(MouseEvent mouseEvent) {
        shapeType = null;
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
        selectedColor.setFill(color.toFx());
    }

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

    //piirtää kaikki
    private void drawAll () {
        for (Shape s: shapes){
            s.render(graphicsContext, view.offset());
        }
    }

}
