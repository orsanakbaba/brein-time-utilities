package com.brein.time.utils;

import java.awt.*;
import java.awt.color.*;

import javax.swing.*;
import javax.swing.border.Border;

public class WhenToWriteTestsSource {
    private int age;
    private String name;
    private boolean gender;


    //Getter/Setters.
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTextAreaText(JTextArea area, String text) {
       if(text != null || text != "") {
           area.setVisible(true);
           area.setText(text);
       }
    }

    public JButton constructButtonType1(JButton button) {
        button.setBackground(Color.CYAN);
        button.setFont(Font.getFont(Font.SANS_SERIF));
        button.setSize(400, 500);
        return button;
    }

}
