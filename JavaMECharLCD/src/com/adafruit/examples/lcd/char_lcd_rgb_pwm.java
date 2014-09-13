
package com.adafruit.examples.lcd;

import com.adafruit.lcd.Adafruit_RGBCharLCD;
import java.io.IOException;
import static java.lang.Math.floor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * https://github.com/adafruit/Adafruit_Python_CharLCD/blob/master/examples/char_lcd_rgb_pwm.py
 * 
 * @author Andres Velez Perez
 */
public class char_lcd_rgb_pwm implements ExampleCharLCD, Runnable{

    private float[] hsv_to_rgb(float h, float s, float v) {
        //"""Converts a tuple of hue, saturation, value to a tuple of red, green blue.
        //Hue should be an angle from 0.0 to 359.0. Saturation and value should be a
        //value from 0.0 to 1.0, where saturation controls the intensity of the hue and
        //value controls the brightness.
        //"""
        //# Algorithm adapted from http://www.cs.rit.edu/~ncs/color/t_convert.html

        if (s == 0) {
            return new float[]{v, v, v};
        }
        h /= 60.0;
        float i = (float) floor(h);
        float f = h - i;
        float p = v * (1.0f - s);
        float q = v * (1.0f - s * f);
        float t = v * (1.0f - s * (1.0f - f));
        if (i == 0) {
            return new float[]{v, t, p};
        } else if (i == 1) {
            return new float[]{q, v, p};
        } else if (i == 2) {
            return new float[]{p, v, t};
        } else if (i == 3) {
            return new float[]{p, q, v};
        } else if (i == 4) {
            return new float[]{t, p, v};
        } else {
            return new float[]{v, p, q};
        }
    }

    //# Raspberry Pi configuration:
    int lcd_rs = 27; //# Change this to pin 21 on older revision Raspberry Pi's
    int lcd_en = 22;
    int lcd_d4 = 25;
    int lcd_d5 = 24;
    int lcd_d6 = 23;
    int lcd_d7 = 18;
    int lcd_red = 4;
    int lcd_green = 17;
    int lcd_blue = 7; //# Pin 7 is CE1
    //# BeagleBone Black configuration:
    //# lcd_rs = 'P8_8'
    //# lcd_en = 'P8_10'
    //# lcd_d4 = 'P8_18'
    //# lcd_d5 = 'P8_16'
    //# lcd_d6 = 'P8_14'
    //# lcd_d7 = 'P8_12'
    //# lcd_red = 'P8_7'
    //# lcd_green = 'P8_9'
    //# lcd_blue = 'P8_11'
    //# Define LCD column and row size for 16x2 LCD.
    int lcd_columns = 16;
    int lcd_rows = 2;
    //# Alternatively specify a 20x4 LCD.
    //# lcd_columns = 20
    //# lcd_rows = 4

    private volatile boolean running = false;
    private Adafruit_RGBCharLCD lcd;
    private Thread thread= null;
    
    @Override
    public void init() throws IOException, InterruptedException {
        boolean enable_pwm = true;
        //# Initialize the LCD using the pins above.
        lcd = new Adafruit_RGBCharLCD(lcd_rs, lcd_en, lcd_d4, lcd_d5, lcd_d6, lcd_d7,
                lcd_columns, lcd_rows, lcd_red, lcd_green, lcd_blue, enable_pwm);
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

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        this.running = true;
        //# Use HSV color space so the hue can be adjusted to see a nice gradient of colors.
        //# Hue ranges from 0.0 to 359.0, saturation from 0.0 to 1.0, and value from 0.0 to 1.0.
        float hue = 0.0f;
        float saturation = 1.0f;
        float value = 1.0f;
        try {
            //# Loop through all RGB colors.
            lcd.clear();
            //print 'Press Ctrl-C to quit.'
        } catch (IOException ex) {
            Logger.getLogger(char_lcd_rgb_pwm.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (this.running) {
            try {
                //# Convert HSV to RGB colors.
                float[] rgb = hsv_to_rgb(hue, saturation, value);
                //# Set backlight color.
                lcd.set_color(rgb[0], rgb[1], rgb[2]);
                //# Print message with RGB values to display.
                lcd.set_cursor(0, 0);
                lcd.message(String.format("RED GREEN BLUE\n%.2f %.2f %.2f", rgb[0], rgb[1], rgb[2]));
                //# Increment hue (wrapping around at 360 degrees).
                hue += 1.0f;
                if (hue > 359.0) {
                    hue = 0.0f;
                }
            } catch (IOException ex) {
                Logger.getLogger(char_lcd_rgb_pwm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            lcd.clear();
            lcd.set_backlight(0.0f);
        } catch (IOException ex) {
            Logger.getLogger(char_lcd_rgb_pwm.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public void stop() throws IOException, InterruptedException
    {    
        lcd.stopPWM();
        this.running = false;
        
        Thread.sleep(300);
        if(this.lcd != null){
            lcd.clear();
            lcd.set_backlight(0.0f);
        }
    }
}
