package com.eightpsman.funnyds.java.customui;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * FunnyDS
 * Created by 8psman on 12/2/2014.
 * Email: 8psman@gmail.com
 */
public class CustomUI {

    public static SubView newSubView(){
        return new SubView();
    }

    public static HeaderView newHeaderView(String title, JComponent component){
        return new HeaderView(title, component);
    }

    public static EditOnClickTextField newTextField(int cols, ActionListener listener){
        return new EditOnClickTextField(cols, listener);
    }

    public static EditOnClickTextField newTextField(String content, int cols, ActionListener listener){
        return new EditOnClickTextField(content, cols, listener);
    }

    public static MultiLineLabelWrapper newMultiLineView(String content, int height){
        return new MultiLineLabelWrapper(new MultiLineLabel(content), height);
    }

    public static FlatButton newFlatButton(String title){
        return new FlatButton(title);
    }
}
