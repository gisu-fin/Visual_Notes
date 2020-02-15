package fi.utu.tech.visualnotes.graphics;

import java.io.*;
import java.util.Optional;

/**
 * Helpers for serializing to/from byte arrays / base64 strings.
 */
public class ObjectHelper {
    public static Optional<byte[]> toBytes(Serializable object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Optional.of(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Optional<T> fromBytes(byte[] data) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return Optional.of((T) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}