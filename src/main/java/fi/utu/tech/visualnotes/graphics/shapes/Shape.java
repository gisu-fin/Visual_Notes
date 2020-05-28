package fi.utu.tech.visualnotes.graphics.shapes;

import controllers.VisualNotesController;
import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.graphics.Region;
import fi.utu.tech.visualnotes.graphics.Color;
import javafx.scene.canvas.GraphicsContext;

public abstract class Shape implements Region, Comparable<Shape> {
    public final Color color;
    public boolean focused = false;
    public boolean filled = false;
    private final Point2D topLeft, bottomRight;
    //miksi ZZ ja Z? compare palauttaa z- o.z
    private static int zz;
    public final int z = zz++;
    public boolean colorFill;

    public Shape(Color color, Boolean colorFill, Point2D p1, Point2D p2) {
        this.color = color == null ? Color.Black : color;
        if (color != null) filled = true;
        this.colorFill = colorFill;
        this.topLeft = new Point2D(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y));
        this.bottomRight = new Point2D(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
    }

    abstract public Shape move(Point2D offset);

    @Override
    public Point2D topLeft() {
        return topLeft;
    }

    @Override
    public Point2D bottomRight() {
        return bottomRight;
    }

    @Override
    public int compareTo(Shape o) {
        return z - o.z;
    }


    public void render(GraphicsContext context, Point2D offset) {
        javafx.scene.paint.Color c = focused ? color.toFx().invert() : color.toFx();
        //System.out.println(VisualNotesController.getFill());
        if (this.colorFill) {
            context.setFill(c);
        }
        context.setStroke(c);
    }

    public enum ShapeType {Line, Oval, Rectangle}

    public static Shape createShape(ShapeType type, Boolean colorFill, Color color, Point2D p1, Point2D p2) {
        switch (type) {
            case Line:
                return new Line(color, colorFill, p1, p2);
            case Rectangle:
                return new Rectangle(color, colorFill, p1, p2);
            case Oval:
                return new Oval(color, colorFill, p1, p2);
        }
        return null;
    }
}
