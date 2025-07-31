package com.rudy.rompeasteroides.graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Loader {

    // Método para cargar una imagen con manejo de errores y verificación de ruta
    public static BufferedImage ImageLoader(String path) {
        try {
            URL resource = Loader.class.getResource(path);
            if (resource == null) {
                System.err.println("Error: No se encontró la imagen en la ruta " + path);
                return null;
            }

            BufferedImage image = ImageIO.read(resource);
            if (image == null) {
                System.err.println("Error: No se pudo cargar la imagen en la ruta " + path);
            }
            return image;
        } catch (IOException e) {
            System.err.println("Error de IO: No se pudo cargar la imagen desde " + path);
            e.printStackTrace();
        }
        return null;
    }

    // Método para cargar una fuente con manejo de errores
    public static Font loadFont(String path, int size) {
        try {
            URL resource = Loader.class.getResource(path);
            if (resource == null) {
                System.err.println("Error: No se encontró la fuente en la ruta " + path);
                return null;
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, Loader.class.getResourceAsStream(path))
                    .deriveFont(Font.PLAIN, size);
            return font;
        } catch (FontFormatException | IOException e) {
            System.err.println("Error al cargar la fuente desde " + path);
            e.printStackTrace();
        }
        return null;
    }

    // Método para cargar un archivo de sonido con manejo de errores
    public static Clip loadSound(String path) {
        try {
            URL soundURL = Loader.class.getResource(path);
            if (soundURL == null) {
                System.err.println("Error: No se encontró el archivo de sonido en la ruta " + path);
                return null;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundURL));
            return clip;
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            System.err.println("Error al cargar el archivo de audio desde " + path);
            e.printStackTrace();
        }
        return null;
    }

   
}
