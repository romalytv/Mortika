package com.games.TileMap;

import com.games.Main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Background {

    private BufferedImage image;

    private double x;
    private double y;
    private double dx;
    private double dy;

    private double moveScale;

    public Background(String s, double moveScale) {

        try {
            image = ImageIO.read(getClass().getResourceAsStream(s));
            this.moveScale = moveScale;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setPosition(double x, double y) {
        this.x = x * moveScale % GamePanel.width;
        this.y = y * moveScale % GamePanel.height;
    }

    public void setVector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g) {

        g.drawImage(image, (int) x, (int) y, null);
        if(x < 0){
            g.drawImage(image, (int) x + GamePanel.width, (int) y, null);
        }

        if(x > 0){
            g.drawImage(image, (int) x - GamePanel.width, (int) y, null);
        }

    }

}
