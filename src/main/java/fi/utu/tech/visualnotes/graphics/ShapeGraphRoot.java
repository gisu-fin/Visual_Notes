package fi.utu.tech.visualnotes.graphics;

import fi.utu.tech.graphics.spatial.rtree.Node;
import fi.utu.tech.graphics.spatial.rtree.Root;
import fi.utu.tech.visualnotes.graphics.shapes.Shape;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ShapeGraphRoot extends Root<Shape> {
    public void save(Path path) throws IOException, InterruptedException {

        Optional<byte[]> data = ObjectHelper.toBytes(tree);
        if (data.isEmpty()) return;

        Files.write(path, data.get());
        //jäätymistestiä varten.
        //Thread.sleep(10000);
        System.out.println("Saved to file.");
    }

    public void load(Path path) throws IOException, InterruptedException {

        Optional<byte[]> data = ObjectHelper.toBytes(tree);
        if (data.isEmpty()) return;

        tree = ObjectHelper.<Node<Shape>>fromBytes(Files.readAllBytes(path)).get();
        //jäätymistestiä varten.
        //Thread.sleep(5000);
        System.out.println("Loaded from file.");

    }
}