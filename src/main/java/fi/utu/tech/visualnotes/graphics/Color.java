package fi.utu.tech.visualnotes.graphics;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum Color implements Serializable {
    Red, Blue, Green, Yellow, Orange, Black, White;

    public javafx.scene.paint.Color toFx() {
        switch (this) {
            case Red:
                return javafx.scene.paint.Color.RED;
            case Blue:
                return javafx.scene.paint.Color.BLUE;
            case Green:
                return javafx.scene.paint.Color.GREEN;
            case Yellow:
                return javafx.scene.paint.Color.YELLOW;
            case Orange:
                return javafx.scene.paint.Color.ORANGE;
            case Black:
                return javafx.scene.paint.Color.BLACK;
            case White:
                return javafx.scene.paint.Color.WHITE;
        }
        return null;
    }

    public static List<String> names() {
        return Arrays.stream(Color.values()).map(Enum::name).collect(Collectors.toList());
    }

    public static Color random() {
        return Color.values()[new Random().nextInt(Color.values().length)];
    }
}