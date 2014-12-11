package com.eightpsman.funnyds.core;

import java.io.Serializable;

/**
 * FunnyDS
 * Created by 8psman on 11/26/2014.
 * Email: 8psman@gmail.com
 */
public class IndependenceDeviceActor implements Serializable{

    /**
     * Coordination
     * (0, 0)----------------------*
     * ----------------------------*
     * ----------------------------*
     * ----------------------------*
     * --------------(max_x, max_y)*
     */



    /** physical size */
    public float pw; // physical width (inch)
    public float ph; // physical height (inch)

    public float px;
    public float py;

    /** screen resolution */
    public int dpi; // dots per inch

    /** bound to draw */
    transient public float left ;   // left position to draw
    transient public float right;   // top position to draw
    transient public float bottom;  // bottom position to draw
    transient public float top;     // right position to draw

    /** position */
    transient public float x; // position y of the center point
    transient public float y; // position x of the center point

    /** width and height to draw */
    transient public float width;   // width to draw
    transient public float height;  // height to draw

    public IndependenceDeviceActor(float px, float py, float pw, float ph){
        this.px = px;
        this.py = py;
        this.pw = pw;
        this.ph = ph;
    }

    public IndependenceDeviceActor(int dpi, float x, float y, float width, float height){
        this.dpi = dpi;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        calcPhysicalSize();
    }

    /** calculate all for specific screen width dpi */
    public void calc(int dpi){
        x       = px * dpi;
        y       = py * dpi;
        width   = pw * dpi;
        height  = ph * dpi;
        left    = x - width/2;
        top     = y - height/2;
        right   = x + width/2;
        bottom  = y + height/2;
    }

    /** calculate physical size from screen size width it's dpi*/
    public void calcPhysicalSize(){
        pw = width / dpi;
        ph = height / dpi;
        px = x / dpi;
        py = y / dpi;
    }

    public float pLeft()    { return px - pw/2; }
    public float pRight()   { return px + pw/2; }
    public float pTop()     { return py - ph/2; }
    public float pBottom()  { return py + ph/2; }
}
