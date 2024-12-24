package com.games.Main;

import com.games.GameState.GameStateManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static final int WIDTH = 320;
    public static final int HEIGHT = 240;
    public static final int SCALE = 2;

    private Thread thread;
    private boolean running;
    private int FPS = 60;
    private long targetTime = 1000 / FPS;

    private BufferedImage image;
    private Graphics2D graphics;

    private GameStateManager gameStateManager;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            addKeyListener(this);
            thread.start();
        }
    }

    private void init() {
        image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();

        running = true;

        gameStateManager = new GameStateManager();


    }

    public void run() {

        init();

        long start;
        long elapsed;
        long wait;

        while (running) {
            start = System.nanoTime();

            update();
            draw();
            drawToScreen();

            elapsed = System.nanoTime() - start;
            wait = targetTime - elapsed / 1000000;
            if (wait < 0) wait = 1;

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        gameStateManager.update();
    }
    private void draw() {
        gameStateManager.draw(graphics);
    }
    private void drawToScreen() {
        Graphics graphics1 = getGraphics();
        graphics1.drawImage(image, 0, 0,WIDTH * SCALE, HEIGHT * SCALE,null);
        graphics1.dispose();
    }

    public void keyTyped(KeyEvent key) {}
    public void keyPressed(KeyEvent key) {
        gameStateManager.keyPressed(key.getKeyCode());
    }
    public void keyReleased(KeyEvent key) {
        gameStateManager.keyReleased(key.getKeyCode());
    }

}
