package com.rudy.rompeasteroides;


import com.rudy.rompeasteroides.gameObjects.Constants;
import com.rudy.rompeasteroides.graphics.Assets;
import com.rudy.rompeasteroides.imput.KeyBoard;
import com.rudy.rompeasteroides.imput.MouseInput;
import com.rudy.rompeasteroides.states.LoadingState;
import com.rudy.rompeasteroides.states.State;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;



public class Window extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;

    private Canvas canvas;
    private Thread thread;
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private final int FPS = 60;
    private double TARGETTIME = 1000000000 / FPS;
    private double delta = 0;
    private int AVERAGEFPS = FPS;

    private KeyBoard keyBoard;
    private MouseInput mouseInput;

    public Window() {
        // Configurar ventana en modo pantalla completa
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        setUndecorated(true); // Quitar bordes
        gd.setFullScreenWindow(this); // Configurar pantalla completa

        setTitle("Space Ship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        canvas = new Canvas();
        keyBoard = new KeyBoard();
        mouseInput = new MouseInput();

        // Ajustar el canvas al tamaño completo de la pantalla
        canvas.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMaximumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMinimumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setFocusable(true);

        add(canvas);
        canvas.addKeyListener(keyBoard);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        setVisible(true);
    }

    public static void main(String[] args) {
        
        ejecutarJar("src//main//resources//jar//Reproductor1-1.0-SNAPSHOT.jar");
        
        new Window().start();
    }
    
    public static void ejecutarJar(String jarPath) {
        try {
            // Verificar si el archivo JAR existe
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                System.err.println("El archivo JAR no existe en la ruta especificada.");
                return;
            }

            // Crear el comando para ejecutar el JAR con java -jar
            String command = "java -jar " + jarPath;

            // Usar ProcessBuilder para ejecutar el comando
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.inheritIO(); // Esto redirige la salida estándar y el error estándar al terminal

            // Ejecutar el proceso
            Process process = processBuilder.start();

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("El proceso terminó con código de salida: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Ocurrió un error al intentar ejecutar el JAR: " + e.getMessage());
        }
    }

    private void update(float dt) {
        keyBoard.update();
        State.getCurrentState().update(dt);
    }

    private void draw() {
        bs = canvas.getBufferStrategy();

        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();

        //-----------------------

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        State.getCurrentState().draw(g);

        g.setColor(Color.WHITE);
        g.drawString("" + AVERAGEFPS, 10, 20);

        //---------------------

        g.dispose();
        bs.show();
    }

    private void init() {

        Thread loadingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Assets.init();
            }
        });

        State.changeState(new LoadingState(loadingThread));
    }

    @Override
    public void run() {

        long now = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        init();

        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / TARGETTIME;
            time += (now - lastTime);
            lastTime = now;

            if (delta >= 1) {
                update((float) (delta * TARGETTIME * 0.000001f));
                draw();
                delta--;
                frames++;
            }
            if (time >= 1000000000) {

                AVERAGEFPS = frames;
                frames = 0;
                time = 0;

            }
        }

        stop();
    }

    private void start() {

        thread = new Thread(this);
        thread.start();
        running = true;

    }

    private void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
