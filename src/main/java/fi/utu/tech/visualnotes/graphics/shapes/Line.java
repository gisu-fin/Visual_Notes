package fi.utu.tech.visualnotes.graphics.shapes;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import javafx.scene.canvas.GraphicsContext;

public class Line extends Shape {
    protected final Point2D p1, p2;

    public Line(Color color, Point2D p1, Point2D p2) {
        super(color, p1, p2);
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Shape move(Point2D offset) {
        return new Line(color, p1.copy().add(offset), p2.copy().add(offset));
    }

    @Override
    public void render(GraphicsContext context, Point2D offset) {
        super.render(context, offset);
        context.strokeLine(p1.x - offset.x, p1.y - offset.y, p2.x - offset.x, p2.y - offset.y);
    }
}