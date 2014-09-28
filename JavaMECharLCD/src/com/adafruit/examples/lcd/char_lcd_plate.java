package com.adafruit.examples.lcd;

import com.adafruit.lcd.Adafruit_CharLCDPlate;
import static com.adafruit.lcd.Adafruit_CharLCDPlate.DOWN;
import static com.adafruit.lcd.Adafruit_CharLCDPlate.LEFT;
import static com.adafruit.lcd.Adafruit_CharLCDPlate.RIGHT;
import static com.adafruit.lcd.Adafruit_CharLCDPlate.SELECT;
import static com.adafruit.lcd.Adafruit_CharLCDPlate.UP;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * # Example using a character LCD plate.
 * 
 * @author andres
 */
public class char_lcd_plate implements ExampleCharLCD, Runnable{
    
    Adafruit_CharLCDPlate lcd = null;
    Button[] buttons;
    private volatile boolean lcdPlate;

    @Override
    public void init() throws IOException, InterruptedException {
        
        //# Initialize the LCD using the pins 
        lcd = new Adafruit_CharLCDPlate();     
        
        //# Show some basic colors.
        lcd.set_color(1.0f, 0.0f, 0.0f);
        lcd.clear();
        lcd.message("RED");
        Thread.sleep(3000);
        lcd.set_color(0.0f, 1.0f, 0.0f);
        lcd.clear();
        lcd.message("GREEN");
        Thread.sleep(3000);
        lcd.set_color(0.0f, 0.0f, 1.0f);
        lcd.clear();
        lcd.message("BLUE");
        Thread.sleep(3000);
        lcd.set_color(1.0f, 1.0f, 0.0f);
        lcd.clear();
        lcd.message("YELLOW");
        Thread.sleep(3000);
        lcd.set_color(0.0f, 1.0f, 1.0f);
        lcd.clear();
        lcd.message("CYAN");
        Thread.sleep(3000);
        lcd.set_color(1.0f, 0.0f, 1.0f);
        lcd.clear();
        lcd.message("MAGENTA");
        Thread.sleep(3000);
        lcd.set_color(1.0f, 1.0f, 1.0f);
        lcd.clear();
        lcd.message("WHITE");
        Thread.sleep(3000);
        
        //# Show button state.
        lcd.clear();
        lcd.message("Press buttons...");
        
        buttons = new Button[]{
          new Button(SELECT, "Select", new int[]{1,1,1}),
          new Button(LEFT, "Left", new int[]{1,0,0}),
          new Button(UP, "Up", new int[]{0,0,1}),
          new Button(DOWN, "Down", new int[]{0,1,0}),
          new Button(RIGHT, "Right", new int[]{1,0,1})
        };
        
        lcdPlate = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        
        lcdPlate = false;
        Thread.sleep(1000);
        
        if(this.lcd != null){
            lcd.clear();
            lcd.set_backlight(0.0f);
        }
        
    }

    @Override
    public void run() {
        //# Loop through each button and check if it is pressed.
        while(lcdPlate){
            for(Button button : this.buttons){
                if(lcd.is_pressed(button.getCode())){
                    try {
                        //# Button is pressed, change the message and backlight.
                        lcd.clear();
                        lcd.message(button.getName());
                        lcd.set_color(button.getRgb()[0],button.getRgb()[1],button.getRgb()[2]);
                    } catch (IOException ex) {
                        Logger.getLogger(char_lcd_plate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(char_lcd_plate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private class Button {
        private int code;
        private String name;
        private int[] rgb;
        
        public Button(int code,String name,int[] rgb){
            this.code = code;
            this.name = name;
            this.rgb=rgb;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public int[] getRgb() {
            return rgb;
        }
        
    }
    
}
