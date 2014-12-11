package com.eightpsman.funnyds.socket;

import java.io.Serializable;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class SocketMessage implements Serializable{

    public int message;
    public int param;
    public Object object;

    public SocketMessage(int message){
        this.message =  message;
        this.object = null;
    }

    public SocketMessage(int message, Object object){
        this.message = message;
        this.object = object;
    }

    public SocketMessage(int message, int param, Object object){
        this.message = message;
        this.param = param;
        this.object = object;
    }

    public static final int MSG_KICKOUT       = 0;
    public static final int MSG_REMOVE_DEVICE = 1;
    public static final int MSG_REMOVE_ACTOR  = 2;
    public static final int MSG_UPDATE_DEVICE = 3;
    public static final int MSG_UPDATE_ACTOR  = 4;
    public static final int MSG_NEW_ACTOR     = 5;
    public static final int MSG_SYNC_ACTOR    = 6;
    public static final int MSG_DISCONNECT    = 7;
    public static final int MSG_JOIN          = 8;
    public static final int MSG_DO_SOME_STUFF = 100;
}
