package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.ShapeGraphRoot;
import fi.utu.tech.visualnotes.graphics.ShapeGraphView;
import fi.utu.tech.visualnotes.graphics.shapes.Line;
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

    //@FXML
    //protected Line line;
    protected Oval oval;
    protected Rectangle rectangle;
    public static Boolean fill;
    protected double startX;
    protected double startY;
    public GraphicsContext graphicsContext;
    public Point2D pointStart;
    public Point2D pointEnd;
    public Point2D offset = new Point2D(1,1);
    public Point2D drag;
    public Shape.ShapeType shapeType;
    public Shape shape;
    public ShapeGraphRoot root = new ShapeGraphRoot();
    public ShapeGraphView view;
    public Optional<Shape> moving;
    protected Collection<Shape> shapes = view.visibleShapes();

    public static boolean getFill(){
        return fill;
    }

    @FXML
    public void initialize(){

        System.out.println("initialize");

        view = new ShapeGraphView(root);
        view.setView(10,10, canvas.getWidth(), canvas.getHeight());

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

        System.out.println("if pressed " + me.getX() + me.getY() + " tai  " + me.getSceneX() + me.getSceneY());
        pointStart = new Point2D(me.getX(), me.getY());
    }

    void mouseReleased (MouseEvent me) {

        System.out.println("if released" + me.getX() + " " + me.getY());
        //gc.strokeLine(startX, startY, me.getSceneX(), me.getSceneY());
        pointEnd = new Point2D(me.getX(), me.getY());
        if (muoto != null) {
            System.out.println("shape: " + shapeType + " color: " + color + " start: " + pointStart + " end: " + pointEnd);
            Shape a = view.createShape(shapeType, color, pointStart, pointEnd);
            root.add(a);
            a.render(graphicsContext, offset);
            System.out.println("lisätty a " + shapes.size());

        }else {
            /*
            double offX = pointEnd.x - pointStart.x;
            double offY = pointEnd.y - pointStart.y;
            drag = new Point2D(offX, offY);
             */
            //TODO tärkeys:1 - palikat jää näkyviin
            System.out.println("mouse release else" + pointStart);
            moving = view.extractShape(pointStart);
            System.out.println("drag " + moving.isPresent());
            if (moving.isPresent()){
                Shape s = moving.get();
                System.out.println(s.toString());
                drag = new Point2D(pointStart.sub(pointEnd));
                s.move(drag);
                root.add(s);
                s.render(graphicsContext, drag);
                Collection<Shape> shapes = view.visibleShapes();
                System.out.println("lisätty kopio " + shapes.size());
                System.out.println("moving if drag on " + drag);
            }
        }

    }//released

    //TODO pohdi tarvitaanko? tuleeko jokaiseen fxml muotoon?
    public void handleDrag(MouseEvent mouseEvent) {
        System.out.println("drag" +muoto);
        muoto = null;
        System.out.println("drag" +muoto);
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

        System.out.println("load clicked");
    }

    public void handleSaveClicked(MouseEvent mouseEvent) {
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
        System.out.println("line");
        fill = true;
        muoto = Muoto.LINE;
        shapeType = Shape.ShapeType.Line;
        System.out.println("line" + fill);
        canvas.setCursor(Cursor.DEFAULT);
    }

    public void handleSquareFilled(MouseEvent mouseEvent) {
        System.out.println("squera filled before " + fill);
        fill = true;
        muoto = Muoto.SQUARE;
        shapeType = Shape.ShapeType.Rectangle;
        System.out.println("squera filled after " + fill);
        canvas.setCursor(Cursor.DEFAULT);
    }
/*
    public void handleSquareMove(MouseEvent mouseEvent) {
        System.out.println(drag);
        rectangle.move(drag);
        System.out.println("handle square move");
    }

 */

    public void handleSquareStroke(MouseEvent mouseEvent) {
        System.out.println("square stroke before " + fill);
        fill = false;
        muoto = Muoto.SQUARE;
        shapeType = Shape.ShapeType.Rectangle;
        System.out.println("square stroke after " + fill);
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
