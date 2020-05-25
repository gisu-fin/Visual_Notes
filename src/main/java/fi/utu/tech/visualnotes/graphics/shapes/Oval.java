package fi.utu.tech.visualnotes.graphics.shapes;

import controllers.VisualNotesController;
import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import javafx.scene.canvas.GraphicsContext;

public class Oval extends Shape {
    public Oval(Color color, Boolean colorFill, Point2D p1, Point2D p2) {
        super(color, colorFill, p1, p2);
    }

    @Override
    public Shape move(Point2D offset) {
        return new Oval(color, colorFill, topLeft().copy().add(offset), bottomRight().copy().add(offset));
    }

    @Override
    public void render(GraphicsContext context, Point2D offset) {
        super.render(context, offset);
        if (this.colorFill) {
            context.fillOval(topLeft().x - offset.x, topLeft().y - offset.y, bottomRight().x - topLeft().x, bottomRight().y - topLeft().y);
        }
        context.strokeOval(topLeft().x - offset.x, topLeft().y - offset.y, bottomRight().x - topLeft().x, bottomRight().y - topLeft().y);
    }
}
