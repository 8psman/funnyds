package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/27/2014.
 * Email: 8psman@gmail.com
 */
public interface GameEventCallback {

    void startGame();
    void gameOver(boolean isWin);
}
