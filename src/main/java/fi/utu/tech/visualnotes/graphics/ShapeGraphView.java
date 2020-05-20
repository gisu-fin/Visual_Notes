package fi.utu.tech.visualnotes.graphics;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.graphics.Rect;
import fi.utu.tech.graphics.Region;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class ShapeGraphView {
    private final ShapeGraphRoot root;

    private final Region view = new Rect(new Point2D(0, 0), new Point2D(0, 0));

    public ShapeGraphView(ShapeGraphRoot root) {
        this.root = root;
    }

    public Collection<Shape> visibleShapes() {
        return root.findIntersections(view);
    }

    public Point2D offset() {
        return view.topLeft();
    }

    public void setView(double x, double y, double w, double h) {
        // calculate a new view top left
        view.topLeft().set(x, y);
        setView(w, h);
    }

    public void setView(double w, double h) {
        // calculate a new view bottom right
        view.bottomRight().set(view.topLeft()).add(w, h);
    }

    private final Rect tmp = new Rect(new Point2D(0, 0), new Point2D(0, 0));
    private Shape ret = null;

    private final Consumer<Shape> selector = s -> {
        ret = s;
    };

    public Optional<Shape> extractShape(Point2D p) {
        int dist = 3;

        tmp.topLeft.set(view.topLeft()).add(p).sub(dist,dist);
        tmp.bottomRight.set(view.topLeft()).add(p).add(dist,dist);

        ret = null;
        root.handleIntersections(tmp, selector);
        if (ret == null) {
            return Optional.empty();
        }
        System.out.println("SGV:n extractShapessa ennen removea ret: " + ret.toString());
        root.remove(ret);
        //System.out.println("SGV:n extractShapessa jälkeen removen: " + root.toString());
        return Optional.of(ret);
    }

    public Shape createShape(Shape.ShapeType type, Color color, Point2D topLeft, Point2D bottomRight) {
        return Shape.createShape(type, color, view.topLeft().copy().add(topLeft), view.topLeft().copy().add(bottomRight));
    }
}