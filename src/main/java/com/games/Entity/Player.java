package com.games.Entity;

import com.games.TileMap.*;
import com.games.Audio.AudioPlayer;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

public class Player extends MapObject {

    private int health;
    private int maxHealth;
    private int fire;
    private int maxFire;
    private boolean dead;
    private boolean flinching;
    private long flinchTimer;

    private boolean firing;
    private int fireCost;
    private int fireBallDamage;
    private ArrayList<Fireball> fireballs;

    private boolean scratching;
    private int scratchDamage;
    private int scratchRange;

    private boolean gliding;

    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            2, 8, 1, 2, 4, 2, 5
    };

    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int GLIDING = 4;
    private static final int FIREBALL = 5;
    private static final int SCRATCHING = 6;

    private HashMap<String, AudioPlayer> sfx;

    public Player(TileMap tm) {

        super(tm);

        width = 30;
        height = 30;
        cwidth = 20;
        cheight = 20;

        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;

        facingRight = true;

        health = maxHealth = 5;
        fire = maxFire = 2500;

        fireCost = 200;
        fireBallDamage = 5;
        fireballs = new ArrayList<Fireball>();

        scratchDamage = 8;
        scratchRange = 40;

        try {

            BufferedImage spritesheet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Sprites/Player/playersprite.gif")));

            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0; i < 7; i++) {

                BufferedImage[] bi =
                        new BufferedImage[numFrames[i]];

                for(int j = 0; j < numFrames[i]; j++) {

                    if(i != SCRATCHING) {
                        bi[j] = spritesheet.getSubimage(
                                j * width,
                                i * height,
                                width,
                                height
                        );
                    }
                    else {
                        bi[j] = spritesheet.getSubimage(
                                j * width * 2,
                                i * height,
                                width * 2,
                                height
                        );
                    }

                }

                sprites.add(bi);

            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);

        sfx = new HashMap<String, AudioPlayer>();
        sfx.put("jump", new AudioPlayer("/SFX/jump.wav"));
        sfx.put("scratch", new AudioPlayer("/SFX/scratch.wav"));

    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getFire() { return fire; }
    public int getMaxFire() { return maxFire; }

    public void setFiring() {
        firing = true;
    }
    public void setScratching() {
        scratching = true;
    }
    public void setGliding(boolean b) {
        gliding = b;
    }

    public void checkAttack(ArrayList<Enemy> enemies) {

        for(int i = 0; i < enemies.size(); i++) {

            Enemy e = enemies.get(i);

            if(scratching) {
                if(facingRight) {
                    if(
                            e.getx() > x &&
                                    e.getx() < x + scratchRange &&
                                    e.gety() > y - height / 2 &&
                                    e.gety() < y + height / 2
                    ) {
                        e.hit(scratchDamage);
                    }
                }
                else {
                    if(
                            e.getx() < x &&
                                    e.getx() > x - scratchRange &&
                                    e.gety() > y - height / 2 &&
                                    e.gety() < y + height / 2
                    ) {
                        e.hit(scratchDamage);
                    }
                }
            }

            for(int j = 0; j < fireballs.size(); j++) {
                if(fireballs.get(j).intersects(e)) {
                    e.hit(fireBallDamage);
                    fireballs.get(j).setHit();
                    break;
                }
            }

            if(intersects(e)) {
                hit(e.getDamage());
            }

        }

    }

    public void hit(int damage) {
        if(flinching) return;
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();
    }

    private void getNextPosition() {

        if(left) {
            dx -= moveSpeed;
            if(dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        }
        else if(right) {
            dx += moveSpeed;
            if(dx > maxSpeed) {
                dx = maxSpeed;
            }
        }
        else {
            if(dx > 0) {
                dx -= stopSpeed;
                if(dx < 0) {
                    dx = 0;
                }
            }
            else if(dx < 0) {
                dx += stopSpeed;
                if(dx > 0) {
                    dx = 0;
                }
            }
        }

        if(
                (currentAction == SCRATCHING || currentAction == FIREBALL) &&
                        !(jumping || falling)) {
            dx = 0;
        }

        if(jumping && !falling) {
            sfx.get("jump").play();
            dy = jumpStart;
            falling = true;
        }

        if(falling) {

            if(dy > 0 && gliding) dy += fallSpeed * 0.1;
            else dy += fallSpeed;

            if(dy > 0) jumping = false;
            if(dy < 0 && !jumping) dy += stopJumpSpeed;

            if(dy > maxFallSpeed) dy = maxFallSpeed;

        }

    }

    public void update() {

        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        if(currentAction == SCRATCHING) {
            if(animation.hasPlayedOnce()) scratching = false;
        }
        if(currentAction == FIREBALL) {
            if(animation.hasPlayedOnce()) firing = false;
        }

        fire += 1;
        if(fire > maxFire) fire = maxFire;
        if(firing && currentAction != FIREBALL) {
            if(fire > fireCost) {
                fire -= fireCost;
                Fireball fb = new Fireball(tileMap, facingRight);
                fb.setPosition(x, y);
                fireballs.add(fb);
            }
        }

        for(int i = 0; i < fireballs.size(); i++) {
            fireballs.get(i).update();
            if(fireballs.get(i).shouldRemove()) {
                fireballs.remove(i);
                i--;
            }
        }

        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 1000) {
                flinching = false;
            }
        }

        if(scratching) {
            if(currentAction != SCRATCHING) {
                sfx.get("scratch").play();
                currentAction = SCRATCHING;
                animation.setFrames(sprites.get(SCRATCHING));
                animation.setDelay(50);
                width = 60;
            }
        }
        else if(firing) {
            if(currentAction != FIREBALL) {
                currentAction = FIREBALL;
                animation.setFrames(sprites.get(FIREBALL));
                animation.setDelay(100);
                width = 30;
            }
        }
        else if(dy > 0) {
            if(gliding) {
                if(currentAction != GLIDING) {
                    currentAction = GLIDING;
                    animation.setFrames(sprites.get(GLIDING));
                    animation.setDelay(100);
                    width = 30;
                }
            }
            else if(currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 30;
            }
        }
        else if(dy < 0) {
            if(currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        }
        else if(left || right) {
            if(currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 30;
            }
        }
        else {
            if(currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 30;
            }
        }

        animation.update();

        if(currentAction != SCRATCHING && currentAction != FIREBALL) {
            if(right) facingRight = true;
            if(left) facingRight = false;
        }

    }

    public void draw(Graphics2D g) {

        setMapPosition();

        for(int i = 0; i < fireballs.size(); i++) {
            fireballs.get(i).draw(g);
        }

        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed / 100 % 2 == 0) {
                return;
            }
        }

        super.draw(g);

    }

}
