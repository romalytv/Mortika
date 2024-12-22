package com.games.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class GameStateManager {

    private ArrayList<GameState> gameStates;
    private int currentState;

    public static final int MENU_STATE = 0;
    public static final int LEVEL_1_STATE = 1;

    public GameStateManager() {
        gameStates = new ArrayList<GameState>();

        currentState = MENU_STATE;
        gameStates.add(new MenuState(this));
    }

    public void setState(int state) {
        currentState = state;
        gameStates.get(currentState).init();
    }

    public void update() {
        gameStates.get(currentState).update();
    }

    public void draw(java.awt.Graphics g) {
        gameStates.get(currentState).draw((Graphics2D) g);
    }

    public void keyPressed(int k) {
        gameStates.get(currentState).keyPressed(k);
    }
    public void keyReleased(int k) {
        gameStates.get(currentState).keyReleased(k);
    }

}
