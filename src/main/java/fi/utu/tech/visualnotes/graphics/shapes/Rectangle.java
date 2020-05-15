package fi.utu.tech.visualnotes.graphics.shapes;

import controllers.VisualnotesController;
import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.Color;
import javafx.scene.canvas.GraphicsContext;

public class Rectangle extends Shape {
    public Rectangle(Color color, Point2D p1, Point2D p2) {
        super(color, p1, p2);
    }

    @Override
    public Shape move(Point2D offset) {
        return new Rectangle(color, topLeft().copy().add(offset), bottomRight().copy().add(offset));
    }

    @Override
    public void render(GraphicsContext context, Point2D offset) {
        super.render(context, offset);
        if (VisualnotesController.getFill()) {
            context.fillRect(topLeft().x - offset.x, topLeft().y - offset.y, bottomRight().x - topLeft().x, bottomRight().y - topLeft().y);
        }
        context.strokeRect(topLeft().x - offset.x, topLeft().y - offset.y, bottomRight().x - topLeft().x, bottomRight().y - topLeft().y);
    }
}
