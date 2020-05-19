package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.shapes.Line;
import fi.utu.tech.visualnotes.graphics.shapes.Oval;
import fi.utu.tech.visualnotes.graphics.shapes.Rectangle;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.io.File;

public class VisualnotesController {

    /*
    tutki demoa ja koita lykätä tänne sen osia.
     */

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

    @FXML
    protected Line line;
    protected Oval oval;
    protected Rectangle rectangle;
    public static Boolean fill;
    protected double startX;
    protected double startY;
    protected GraphicsContext gc;
    protected Point2D pointStart;
    protected Point2D pointEnd;
    protected Point2D offset = new Point2D(1,1);
    protected Point2D drag;
    protected Shape.ShapeType shapeType;
    protected Shape shape;

    public static boolean getFill(){
        return fill;
    }

    @FXML
    public void initialize(){

        //TODO Tärkeys: 1 - vaihda canvas shapegraphview:n

        System.out.println("initialize");


        //piirto toimii
        gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(this::mousePressed);
        canvas.setOnMouseReleased(this::mouseReleased);
        //canvas.setOnMouseDragged(this::mouseDragged);

        /*
        canvas.setOnMouseDragged(e -> {
            double size = 10.5;
            double x = e.getX() - size/2;
            double y = e.getY() - size/2;

            g.setFill(colorPicker.getValue());
            g.fillRect(x, y, size, size);

        });

         */

    } //init
    /*
    piirrä nelio tms, anna sille tommonen jos vaikka voisi raahata
    circle_Blue.setOnMouseDragged(circleOnMouseDraggedEventHandler);

    	canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseReleased(this::mouseReleased);

     */

    void mousePressed (MouseEvent me) {
        //line.setStartX(me.getSceneX());
        //line.setStartY(me.getSceneY());
        /*
        ---------------------------Auttaako ongelmaan getSceneX() - työkalupalkin leveys? Voiko tulla null juttui?-----------
         */
        System.out.println("if pressed " + me.getSceneX() + " " + me.getSceneY());
        pointStart = new Point2D(me.getSceneX(), me.getSceneY());
    }

    void mouseReleased (MouseEvent me) {
        //line.setEndX(me.getSceneX());
        //line.setEndY(me.getSceneY());
        System.out.println("if released" + me.getSceneX() + " " + me.getSceneY());
        //gc.strokeLine(startX, startY, me.getSceneX(), me.getSceneY());
        pointEnd = new Point2D(me.getSceneX(), me.getSceneY());
        if (muoto != null) {
            /*
            System.out.println("shape: " + shapeType + " color: " + color + " start: " + pointStart + " end: " + pointEnd);
            // TODO !tämä rivi aiheuttaa nullpointerin, kokeile miten käy kun canvas vaihdettu pois
            // [info] shape: Rectangle color: Blue start: (317,186) end: (375,274)
            shape.createShape(shapeType, color, pointStart, pointEnd);
            System.out.println("offset: " + offset);
            shape.render(gc, offset);
            */
            switch (muoto) {
                case LINE:
                    line = new Line(color, pointStart, pointEnd);
                    line.render(gc, offset);
                    break;
                case CIRCLE:
                    oval = new Oval(color, pointStart, pointEnd);
                    oval.render(gc, offset);
                    break;
                case SQUARE:
                    rectangle = new Rectangle(color, pointStart, pointEnd);
                    rectangle.render(gc, offset);
                    break;
            }//switch


        }else {
            /*
            double offX = me.getSceneX() - pointStart.x;
            double offY = me.getSceneY() - pointStart.y;
            drag = new Point2D(offX, offY);
            System.out.println("mouse release else" + drag);
             */
            System.out.println("released else");
        }

    }//released

    //TODO pohdi tarvitaanko? tuleeko jokaiseen fxml muotoon?
    public void handleDrag(MouseEvent mouseEvent) {
        System.out.println("drag" +muoto);
        muoto = null;
        System.out.println("drag" +muoto);
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
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
    }
/*
    public void lineMove(MouseEvent mouseEvent) {
        line.move(drag);
    }


 */
    public void handleSquareFilled(MouseEvent mouseEvent) {
        System.out.println("squera filled before " + fill);
        fill = true;
        muoto = Muoto.SQUARE;
        shapeType = Shape.ShapeType.Rectangle;
        System.out.println("squera filled after " + fill);
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
    }

    public void handleCircleFilled(MouseEvent mouseEvent) {
        fill = true;
        muoto = Muoto.CIRCLE;
        shapeType = Shape.ShapeType.Oval;
    }

    public void handleCircleStroke(MouseEvent mouseEvent) {
        fill = false;
        muoto = Muoto.CIRCLE;
        shapeType = Shape.ShapeType.Oval;
    }

}
