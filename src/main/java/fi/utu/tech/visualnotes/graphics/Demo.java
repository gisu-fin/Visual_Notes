package fi.utu.tech.visualnotes.graphics;

import fi.utu.tech.graphics.Point2D;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

public class Demo {
    static void test() throws IOException {
        // container for shapes
        ShapeGraphRoot root = new ShapeGraphRoot();

        // an active view of the shape graph
        ShapeGraphView view = new ShapeGraphView(root);

        // 500x500 pixel view (10,10) - (510,510)
        view.setView(10, 10, 500, 500);

        // create a new blue rectangle @ (20,30) - (50,50)
        // keep in mind that the shape is created relative
        // to the view's top left corner, so it is actually
        // located at (30,40) - (60,60)
        Shape rect = view.createShape(
                Shape.ShapeType.Rectangle,
                Color.Blue,
                new Point2D(20, 30),
                new Point2D(50, 50));

        // add the rectangle to the shape graph
        root.add(rect);

        // list all visible shapes inside the view
        // that is, (10,10) - (510,510)
        Collection<Shape> shapes = view.visibleShapes();

        // the view should contain our rectangle
        assert(shapes.size() == 1);

        // if we had a graphics context, we could draw the rectangle

        // GraphicsContext context = null;
        // rect.render(context, view.offset());

        // store the graph to a file "test.vin"
        root.save(Paths.get("test.vin"));

        // restore the graph from the file "test.vin"
        root.load(Paths.get("test.vin"));
    }
}
