//# Copyright (c) 2014 Adafruit Industries
//# Author: Tony DiCola
//#
//# Permission is hereby granted, free of charge, to any person obtaining a copy
//# of this software and associated documentation files (the "Software"), to deal
//# in the Software without restriction, including without limitation the rights
//# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//# copies of the Software, and to permit persons to whom the Software is
//# furnished to do so, subject to the following conditions:
//#
//# The above copyright notice and this permission notice shall be included in
//# all copies or substantial portions of the Software.
//#
//# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//# THE SOFTWARE.

package com.adafruit.lcd;

import com.adafruit.gpio.BaseGPIO;
import com.adafruit.i2c.I2C;
import com.adafruit.i2c.MCP23017;
import com.adafruit.i2c.MCP230xxBase;
import java.io.IOException;

/**
 *
 * @author Andres Velez Perez
 */
public class Adafruit_CharLCDPlate extends Adafruit_RGBCharLCD {
//    """Class to represent and interact with an Adafruit Raspberry Pi character
//LCD plate."""
    
    // Char LCD plate GPIO numbers.
    public static int LCD_PLATE_RS = 15;
    public static int LCD_PLATE_RW = 14;
    public static int LCD_PLATE_EN = 13;
    public static int LCD_PLATE_D4 = 12;
    public static int LCD_PLATE_D5 = 11;
    public static int LCD_PLATE_D6 = 10;
    public static int LCD_PLATE_D7 = 9;
    public static int LCD_PLATE_RED = 6;
    public static int LCD_PLATE_GREEN = 7;
    public static int LCD_PLATE_BLUE = 8;
    // Char LCD plate button names.
    public static int SELECT = 0;
    public static int RIGHT = 1;
    public static int DOWN = 2;
    public static int UP = 3;
    public static int LEFT = 4;
    
    private MCP230xxBase baseGPIO = null;

    public Adafruit_CharLCDPlate() throws IOException {
        this(0x20, I2C.get_default_bus(), 16, 2);
    }

    public Adafruit_CharLCDPlate(int adress, int busnumm, int cols, int lines) throws IOException {
        //# Configure MCP23017 device.
        this(new MCP23017(adress, busnumm), cols, lines);
    }

    public Adafruit_CharLCDPlate(MCP230xxBase gpio, int cols, int lines) throws IOException {
        //        """Initialize the character LCD plate. Can optionally specify a separate
        //I2C address or bus number, but the defaults should suffice for most needs.
        //Can also optionally specify the number of columns and lines on the LCD
        //(default is 16x2).
        //"""
        //# Initialize LCD (with no PWM support).
        super(LCD_PLATE_RS, LCD_PLATE_EN,
                LCD_PLATE_D4, LCD_PLATE_D5, LCD_PLATE_D6, LCD_PLATE_D7, cols, lines,
                LCD_PLATE_RED, LCD_PLATE_GREEN, LCD_PLATE_BLUE, gpio);
        this.baseGPIO = gpio;
        //# Set LCD R/W pin to low for writing only.
        this.baseGPIO.setup(LCD_PLATE_RW, BaseGPIO.OUT);
        this.baseGPIO.output(LCD_PLATE_RW, BaseGPIO.LOW);
        //# Set buttons as inputs with pull-ups enabled.
        int[] buttons = new int[]{SELECT, RIGHT, DOWN, UP, LEFT};
        for (int button : buttons) {
            this.baseGPIO.setup(button, BaseGPIO.IN);
            this.baseGPIO.pullup(button, true);
        }
    }

    public boolean is_pressed(int button) {
        //"""Return True if the provided button is pressed, False otherwise."""
        if (button != SELECT && button != RIGHT && button != DOWN && button != UP && button != LEFT) {
            throw new RuntimeException("Unknown button, must be SELECT, RIGHT, DOWN, UP, or LEFT.");
        }
        return this.baseGPIO.input(button) == BaseGPIO.LOW;
    }

}
