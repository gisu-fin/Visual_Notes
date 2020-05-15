package controllers;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import fi.utu.tech.visualnotes.graphics.shapes.Line;
import fi.utu.tech.visualnotes.graphics.shapes.Oval;
import fi.utu.tech.visualnotes.graphics.shapes.Rectangle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    Color color;

    @FXML
    protected Line line;
    protected Oval oval;
    protected Rectangle rectangle;
    public static Boolean fill;
    protected double startX;
    protected double startY;
    protected GraphicsContext gc;
    protected Point2D p1;
    protected Point2D p2;
    protected Point2D offset = new Point2D(1,1);

    public static boolean getFill(){
        return fill;
    }

    @FXML
    public void initialize(){

        System.out.println("initialize");

        //piirto toimii
        gc = canvas.getGraphicsContext2D();
        //g.setFill(colorPicker.getValue());
        color = Color.Blue;

        canvas.setOnMousePressed(this::mousePressed);
        canvas.setOnMouseReleased(this::mouseReleased);

        /*
        canvas.setOnMouseDragged(e -> {
            double size = 10.5;
            double x = e.getX() - size/2;
            double y = e.getY() - size/2;


            g.setFill(colorPicker.getValue());

            g.fillRect(x, y, size, size);

            if (muoto == Muoto.LINE){

            }
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
        startX = me.getSceneX();
        startY = me.getSceneY();
        System.out.println("if pressed " + me.getSceneX() + " " + me.getSceneY());
        p1 = new Point2D(startX, startY);

    }

    void mouseReleased (MouseEvent me) {
        //line.setEndX(me.getSceneX());
        //line.setEndY(me.getSceneY());
        System.out.println("if released" + me.getSceneX() + " " + me.getSceneY());
        //gc.strokeLine(startX, startY, me.getSceneX(), me.getSceneY());
        p2 = new Point2D(me.getSceneX(), me.getSceneY());
        switch (muoto){
            case LINE:
                line = new Line (color, p1, p2);
                line.render(gc, offset);
                break;
            case CIRCLE:
                oval = new Oval(color, p1, p2);
                oval.render(gc, offset);
                break;
            case SQUARE:
                rectangle = new Rectangle(color, p1, p2);
                rectangle.render(gc, offset);
                break;
        }//switch

    }

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
        System.out.println("line" + fill);
    }

    public void handleSquareFilled(MouseEvent mouseEvent) {
        System.out.println("squera filled before " + fill);
        fill = true;
        muoto = Muoto.SQUARE;
        System.out.println("squera filled after " + fill);
    }

    public void handleSquareStroke(MouseEvent mouseEvent) {
        System.out.println("square stroke before " + fill);
        fill = false;
        muoto = Muoto.SQUARE;
        System.out.println("square stroke after " + fill);
    }

    public void handleCircleFilled(MouseEvent mouseEvent) {
        fill = true;
        muoto = Muoto.CIRCLE;
    }

    public void handleCircleStroke(MouseEvent mouseEvent) {
        fill = false;
        muoto = Muoto.CIRCLE;
    }

}
