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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

public class VisualNotesController {

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
    public Point2D offset = new Point2D(1,1);
    public Point2D drag;
    public Shape.ShapeType shapeType;
    public ShapeGraphRoot root = new ShapeGraphRoot();
    public ShapeGraphView view;
    public Optional<Shape> moving;
    public Shape shape;
    protected Collection<Shape> shapes;

    public static boolean getFill(){
        return fill;
    }

    @FXML
    public void initialize(){

        System.out.println("initialize");

        view = new ShapeGraphView(root);
        view.setView(0,0, canvas.getWidth(), canvas.getHeight());

        shapes = view.visibleShapes();

        graphicsContext = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(this::mousePressed);
        canvas.setOnMouseReleased(this::mouseReleased);
        //canvas.setOnMouseDragged(this::mouseDragged);

    } //init
    /*
    piirrä nelio tms, anna sille tommonen jos vaikka voisi raahata
    circle_Blue.setOnMouseDragged(circleOnMouseDraggedEventHandler);

    	canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseReleased(this::mouseReleased);

     */

    void mousePressed (MouseEvent me) {

        //System.out.println("if pressed " + me.getX() + me.getY() + " tai  " + me.getSceneX() + me.getSceneY());
        pointStart = new Point2D(me.getX(), me.getY());
    }

    void mouseReleased (MouseEvent me) {

        //System.out.println("if released" + me.getX() + " " + me.getY());
        //gc.strokeLine(startX, startY, me.getSceneX(), me.getSceneY());
        pointEnd = new Point2D(me.getX(), me.getY());
        if (muoto != null) {
            System.out.println("shape: " + shapeType + " color: " + color + " start: " + pointStart + " end: " + pointEnd);
            shape = view.createShape(shapeType, color, pointStart, pointEnd);
            root.add(shape);
            shape.render(graphicsContext, view.offset());
            shapes = view.visibleShapes();
            System.out.println("lisätty a " + shapes.size());

        }else {
            /*
            double offX = pointEnd.x - pointStart.x;
            double offY = pointEnd.y - pointStart.y;
            drag = new Point2D(offX, offY);
             */
            //TODO tärkeys:1 - move kopioi ja siirtää
            //TODO tärkeys:3 - keksi keino hakea liikutettavan muodon fill tai ota pois :(
            /*
                pitänee muokata kaikki luokat siten että fill on asetettuna itse kuvioon.
             */
            System.out.println("mouse release else " + pointStart);
            moving = view.extractShape(pointStart);
            //jos on muoto kohdassa
            if (moving.isPresent()){
                //tallennetaan extractShapen palauttama muoto:
                System.out.println("drag, onko kohdassa muoto? " + moving.isPresent());
                //tehdään uusi shape joka hakee muodon
                Shape s = moving.get();
                //poistetaan rootista öööö vaikkei ole siellä vielä -- TÄÄ POIS?
                root.remove(s);
                //tsekki s
                System.out.println(s.toString());
                System.out.println("moving ennen lis " + root.contents());
                //muuttuja drag saa arvon
                drag = new Point2D(pointStart.sub(pointEnd));
                //kutsutaan shape move, annetaan drag - jonka pitäisi palauttaa uusi muoto -- TÄSSÄ VIRHE
                s.move(drag);
                //lisätään s roottiin -- JOKA EI OO SIIRTYNYT -- TÄSSÄ VIRHE
                root.add(s);
                //renderöidään s JOKA EI OO MUUTTUNUT
                s.render(graphicsContext, drag);
                /*
                Shape a = s.move(drag);
                System.out.println("onko a hyva? " + a.toString());
                root.add(a);
                a.render(graphicsContext, drag);
                 */
                //shapes = view.visibleShapes();
                //view.setView(0,0, canvas.getWidth(), canvas.getHeight());
                //Collection<Shape> shapes = view.visibleShapes();
                System.out.println("lisätty kopio " + root.contents());

            }
        }

    }//released

    //vaihtaa kursorin move-muotoon ja muodon nulliksi jottei muotoja voi piirtää
    public void handleDrag(MouseEvent mouseEvent) {
        //System.out.println("drag" +muoto);
        muoto = null;
        //System.out.println("drag" +muoto);
        canvas.setCursor(Cursor.MOVE);
    }
/*
    public void mouseDragged(MouseEvent me) {
        System.out.println("mouse drag event");
        /*
        double offX = me.getSceneX() - pointStart.x;
        double offY = me.getSceneY() - pointStart.y;
        drag = new Point2D(offX, offY);

    }
    */

    public void handleLoadClicked(MouseEvent mouseEvent) {

        //TODO tärkeys: 1 - lataaminen
        System.out.println("load clicked");
    }

    public void handleSaveClicked(MouseEvent mouseEvent) {

        //TODO tärkeys: 1 - tallennus
        try {
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "vin", new File("paint.png"));
            System.out.println("saved");
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
