package com.eightpsman.funnyds.core;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class Constants {

    public static final String TAG = "FunnyDS";

    public static final String GREETING_MESSAGE = "HELLO_FUNNY_DS_SERVER";

    public static final int SERVER_GREETER_SOCKET = 6868;
    public static final int SERVER_SOCKET_PORT = 6869;

    public static final float DELTA_TIME = 1/60f;

    public static final int SYN_ACTOR_PERIOD = 5000;

    public static final int MODE_EXHI = 0;
    public static final int MODE_PRES = 1;
    public static final int MODE_GAME = 2;

    public static final int ACTOR_MAX_HEIGHT = 120;

    /** some custom message server send to client*/
    public static final int HACK_MSG_SET_LOCATION_ON_SCREEN = 110;

    public enum ConnectingMethod{
        RMI,
        Socket
    }
}
