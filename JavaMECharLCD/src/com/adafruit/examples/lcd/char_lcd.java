
package com.adafruit.examples.lcd;

import com.adafruit.lcd.Adafruit_CharLCD;
import java.io.IOException;

/**
 * https://github.com/adafruit/Adafruit_Python_CharLCD/blob/master/examples/char_lcd.py
 * 
 * @author Andres Velez Perez
 */
public class char_lcd implements ExampleCharLCD {

    //# Raspberry Pi pin configuration:
    int lcd_rs = 27; //# Note this might need to be changed to 21 for older revision Pi's.
    int lcd_en = 22;
    int lcd_d4 = 25;
    int lcd_d5 = 24;
    int lcd_d6 = 23;
    int lcd_d7 = 18;
    int lcd_backlight = 4;
    //# BeagleBone Black configuration:
    //# lcd_rs = 'P8_8'
    //# lcd_en = 'P8_10'
    //# lcd_d4 = 'P8_18'
    //# lcd_d5 = 'P8_16'
    //# lcd_d6 = 'P8_14'
    //# lcd_d7 = 'P8_12'
    //# lcd_backlight = 'P8_7'
    //# Define LCD column and row size for 16x2 LCD.
    int lcd_columns = 16;
    int lcd_rows = 2;
    //# Alternatively specify a 20x4 LCD.
    //# lcd_columns = 20
    //# lcd_rows = 4

    private Adafruit_CharLCD lcd = null;
    
    @Override
    public void init() throws IOException, InterruptedException {
        //# Initialize the LCD using the pins above.
        lcd = new Adafruit_CharLCD(lcd_rs, lcd_en, lcd_d4, lcd_d5, lcd_d6, lcd_d7, lcd_columns, lcd_rows, lcd_backlight);
        //# Print a two line message
        lcd.message("Hello\nworld!");
        //# Wait 5 seconds
        Thread.sleep(5000);
        //# Demo showing the cursor.
        lcd.clear();
        lcd.show_cursor(true);
        lcd.message("Show cursor");
        Thread.sleep(5000);
        //# Demo showing the blinking cursor.
        lcd.clear();
        lcd.blink(true);
        lcd.message("Blink cursor");
        Thread.sleep(5000);
        //# Stop blinking and showing cursor.
        lcd.show_cursor(false);
        lcd.blink(false);
        //# Demo scrolling message right/left.
        lcd.clear();
        String message = "Scroll";
        lcd.message(message);
        for (int i = 0; i < (lcd_columns - message.length()); i++) {
            Thread.sleep(500);
            lcd.move_right();
        }
        for (int i = 0; i < (lcd_columns - message.length()); i++) {
            Thread.sleep(500);
            lcd.move_left();
        }
        //# Demo turning backlight off and on.
        lcd.clear();
        lcd.message("Flash backlight\nin 5 seconds...");
        Thread.sleep(5000);
        //# Turn backlight off.
        lcd.set_backlight(0);
        Thread.sleep(2000);
        //# Change message.
        lcd.clear();
        lcd.message("Goodbye!");
        //# Turn backlight on.
        lcd.set_backlight(1);
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
