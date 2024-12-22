package com.games.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class GameState {

    protected GameStateManager gameStateManager;

    public abstract void init();
    public abstract void update();
    public abstract void draw(Graphics2D g);
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);

}
