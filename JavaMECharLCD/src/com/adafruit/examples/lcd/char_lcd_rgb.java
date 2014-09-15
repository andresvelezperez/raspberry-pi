
package com.adafruit.examples.lcd;

import com.adafruit.lcd.Adafruit_RGBCharLCD;
import java.io.IOException;

/**
 * https://github.com/adafruit/Adafruit_Python_CharLCD/blob/master/examples/char_lcd_rgb.py
 * 
 * @author Andres Velez Perez
 */
public class char_lcd_rgb implements ExampleCharLCD{

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
    Adafruit_RGBCharLCD lcd = null;
    
    @Override
    public void init() throws IOException, InterruptedException {
        //# Initialize the LCD using the pins above.
        lcd = new Adafruit_RGBCharLCD(lcd_rs, lcd_en, lcd_d4, lcd_d5, lcd_d6, lcd_d7,
                lcd_columns, lcd_rows, lcd_red, lcd_green, lcd_blue);
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
        lcd.clear();
        lcd.set_backlight(0.0f);
    }
    
    @Override
    public void stop() throws IOException, InterruptedException
    {    
        if(this.lcd != null){
            lcd.clear();
            lcd.set_backlight(0.0f);
        }
    }

}
