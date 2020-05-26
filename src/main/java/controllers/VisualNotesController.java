package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.ShapeGraphRoot;
import fi.utu.tech.visualnotes.graphics.ShapeGraphView;
import fi.utu.tech.visualnotes.graphics.shapes.Oval;
import fi.utu.tech.visualnotes.graphics.shapes.Rectangle;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;
import scalafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

public class VisualNotesController {

    @FXML
    public Canvas animationCanvas;

    @FXML
    private Canvas canvas;



    @FXML
    private ColorPicker colorPicker;

    private enum Muoto {
        LINE,
        CIRCLE,
        SQUARE
    }

    Muoto muoto;

    //g.setFill(colorPicker.getValue());
    Color color = Color.Blue;

    public static Boolean fill;
    public GraphicsContext graphicsContext;
    public Point2D pointStart;
    public Point2D pointEnd;
    public Point2D pointNow;
    public Point2D offset = new Point2D(1,1);
    public Point2D drag;
    public Shape.ShapeType shapeType;
    public ShapeGraphRoot root = new ShapeGraphRoot();
    public ShapeGraphView view;
    public Optional<Shape> moving;
    public Shape shape;
    protected Collection<Shape> shapes;
    protected double height;
    protected double width;
    protected Popup popup = new Popup();

    public static boolean getFill(){
        return fill;
    }

    //TODO tärkeys 1: skrollaus - älä unohda spatiaalisen rakenteen näkymän vieritystä

    //TODO tärkeys 1: värin valinta

    //POPUP? listaan palloja missä värit?

    //TODO tärkeys 1: asynkroonisuus tallennus ja lataus


    //TODO tärkeys 2: lisää kuvakkeille tooltip tekstit!

    // https://stackoverflow.com/questions/41459107/how-to-show-a-title-for-image-when-i-hover-over-it-in-javafx

    @FXML
    public void initialize(){

        System.out.println("initialize");

        view = new ShapeGraphView(root);
        view.setView(0,0, canvas.getWidth(), canvas.getHeight());

        shapes = view.visibleShapes();

        graphicsContext = canvas.getGraphicsContext2D();

        //canvas.setOnMousePressed(this::mousePressed);
        //canvas.setOnMouseReleased(this::mouseReleased);
        //canvas.setOnMouseDragged(this::mouseDragged);

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
            //moving = view.extractShape(pointStart);
            //jos on muoto kohdassa
            if (moving.isPresent()){

                //System.out.println("drag, onko kohdassa muoto? " + moving.isPresent());

                //muuttuja drag saa arvon
                drag = new Point2D(pointEnd.sub(pointStart));

                //tehdään uusi shape joka hakee muodon
                Shape s = moving.get();

                height = s.bottomRight().y - s.topLeft().y;
                width = s.bottomRight().x - s.topLeft().x;

                //System.out.println("mitkä arvot s on? " + s.topLeft() + " bottom " + s.bottomRight());
                //System.out.println("mitkä ovat start arvot? " + pointStart);
                //System.out.println("korkeus ja leveys: " + height + " " + width);

                //pyyhitään alue jolla muoto oli
                graphicsContext.clearRect(s.topLeft().x, s.topLeft().y, width+1, height+1);

                //tallennetaan muotoon a moven palauttama muoto, lisätään ja renderöidään
                Shape a = s.move(drag);
                root.add(a);
                shapes = view.visibleShapes();
                drawAll();
                canvas.toFront();
                //a.render(graphicsContext, view.offset());
                //System.out.println("lisätty a " + shapes.toArray());

                //System.out.println("lisätty kopio " + root.contents());


            }


        }

    }//released

    //TODO jos tarkoitus että muoto siirtyy hiiren mukana niin selvitä tähän animaatio tms juttu jonka avulla siirto onnistuu.
    //muodon ei tarvitse mennä roottiin ennen kuin hiiri pysähtyy!
    //Selvitä paras ratkaisu
    @FXML
    public void mouseDragged(MouseEvent me) {
        //System.out.println("mouse drag event");

        pointNow = new Point2D(me.getX(), me.getY());
        moving = view.extractShape(pointStart);
        //jos on muoto kohdassa
        if (moving.isPresent()) {

            //System.out.println("drag, onko kohdassa muoto? " + moving.isPresent());

            //muuttuja drag saa arvon
            drag = new Point2D(pointNow.sub(pointStart));

            //tehdään uusi shape joka hakee muodon
            Shape s = moving.get();

            //lasketaan korkeus ja leveys
            height = s.bottomRight().y - s.topLeft().y;
            width = s.bottomRight().x - s.topLeft().x;

            //luodaan uusi canvas animointia varten
            animationCanvas = new Canvas(height, width);
            GraphicsContext animate = animationCanvas.getGraphicsContext2D();
            animationCanvas.toFront();

            animate.setFill(javafx.scene.paint.Color.BLUEVIOLET);
            animate.fillRect(50, 50, 50, 50);
            //s.render(animate,view.offset());
            //System.out.println("mitkä arvot s on? " + s.topLeft() + " bottom " + s.bottomRight());
            //System.out.println("mitkä ovat start arvot? " + pointStart);
            //System.out.println("korkeus ja leveys: " + height + " " + width);

            //pyyhitään alue jolla muoto oli
            graphicsContext.clearRect(s.topLeft().x, s.topLeft().y, width + 1, height + 1);

            //animaation pitäisi toimia.
            animationCanvas.setOnMouseDragged(e -> {
                double offsetX = me.getSceneX() - animationCanvas.getTranslateX() - animationCanvas.getWidth() / 2;
                animationCanvas.setTranslateX(animationCanvas.getTranslateX() + offsetX);
            });


            //tallennetaan muotoon a moven palauttama muoto, lisätään ja renderöidään
            Shape a = s.move(drag);
            root.add(a);
            shapes = view.visibleShapes();
            drawAll();
            //a.render(graphicsContext, view.offset());
            //System.out.println("lisätty a " + shapes.toArray());

            //System.out.println("lisätty kopio " + root.contents());


        }

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

    public void handleColor(MouseEvent mouseEvent) {
        System.out.println("handle color");
        createPopup();

    }

    public void createPopup () {
        System.out.println("create popup");
        Label label = new Label("Pick a color");
        popup.getContent().add(label);
        popup.setAutoHide(true);
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
