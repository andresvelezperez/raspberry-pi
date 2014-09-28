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
import com.adafruit.gpio.PWM_Adapter;
import com.adafruit.gpio.Platform;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * https://github.com/adafruit/Adafruit_Python_CharLCD/blob/master/Adafruit_CharLCD/Adafruit_CharLCD.py
 * 
 * @author Andres Velez Perez
 */
public class Adafruit_CharLCD {

    // Commands
    public static int LCD_CLEARDISPLAY = 0x01;
    public static int LCD_RETURNHOME = 0x02;
    public static int LCD_ENTRYMODESET = 0x04;
    public static int LCD_DISPLAYCONTROL = 0x08;
    public static int LCD_CURSORSHIFT = 0x10;
    public static int LCD_FUNCTIONSET = 0x20;
    public static int LCD_SETCGRAMADDR = 0x40;
    public static int LCD_SETDDRAMADDR = 0x80;
    // Entry flags
    public static int LCD_ENTRYRIGHT = 0x00;
    public static int LCD_ENTRYLEFT = 0x02;
    public static int LCD_ENTRYSHIFTINCREMENT = 0x01;
    public static int LCD_ENTRYSHIFTDECREMENT = 0x00;
    // Control flags
    public static int LCD_DISPLAYON = 0x04;
    public static int LCD_DISPLAYOFF = 0x00;
    public static int LCD_CURSORON = 0x02;
    public static int LCD_CURSOROFF = 0x00;
    public static int LCD_BLINKON = 0x01;
    public static int LCD_BLINKOFF = 0x00;
    // Move flags
    public static int LCD_DISPLAYMOVE = 0x08;
    public static int LCD_CURSORMOVE = 0x00;
    public static int LCD_MOVERIGHT = 0x04;
    public static int LCD_MOVELEFT = 0x00;
    // Function set flags
    public static int LCD_8BITMODE = 0x10;
    public static int LCD_4BITMODE = 0x00;
    public static int LCD_2LINE = 0x08;
    public static int LCD_1LINE = 0x00;
    public static int LCD_5x10DOTS = 0x04;
    public static int LCD_5x8DOTS = 0x00;
    // Offset for up to 4 rows.
    public static int LCD_ROW_OFFSETS[] = {0x00, 0x40, 0x14, 0x54};

    // var class    
    public boolean _blpol;
    int _en, _rs, _d4, _d5, _d6, _d7, _backlight;
    boolean _pwm_enabled = false;
    private int _cols, _lines;
    private int displaymode;
    private int displaycontrol;
    private int displayfunction;
    
    protected BaseGPIO _gpio;
    protected PWM_Adapter _pwm;
    
    public Adafruit_CharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines,int backlight) throws IOException {
        /*int backlight = -1;
         boolean invert_polarity = true;
         boolean enable_pwm = false;
         BaseGPIO gpio = Platform.get_platform_gpio();
         PWM_Adapter pwm = Platform.get_platform_pwm();
         float initial_backlight = 1.0f;*/
        this(rs, en, d4, d5, d6, d7, cols, lines,
                backlight, true, false, Platform.get_platform_gpio(), Platform.get_platform_pwm(), 1.0f);
    }

    public Adafruit_CharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines) throws IOException {
        /*int backlight = -1;
         boolean invert_polarity = true;
         boolean enable_pwm = false;
         BaseGPIO gpio = Platform.get_platform_gpio();
         PWM_Adapter pwm = Platform.get_platform_pwm();
         float initial_backlight = 1.0f;*/
        this(rs, en, d4, d5, d6, d7, cols, lines,
                -1, true, false, Platform.get_platform_gpio(), Platform.get_platform_pwm(), 1.0f);
    }

    public Adafruit_CharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines, int backlight,
            boolean invert_polarity, boolean enable_pwm, BaseGPIO gpio, PWM_Adapter pwm, float initial_backlight) throws IOException {

        //# Save column and line state.
        this._cols = cols;
        this._lines = lines;
        //# Save GPIO state and pin numbers.
        this._gpio = gpio;
        this._rs = rs;
        this._en = en;
        this._d4 = d4;
        this._d5 = d5;
        this._d6 = d6;
        this._d7 = d7;
        //# Save backlight state.
        this._backlight = backlight;
        this._pwm_enabled = enable_pwm;
        this._pwm = pwm;
        this._blpol = !invert_polarity;
        //# Setup all pins as outputs.
        for(int pin : new int[]{rs, en, d4, d5, d6, d7}){
            _gpio.setup(pin, BaseGPIO.OUT);
        }
        //# Setup backlight.
        if(backlight != -1){
            if(enable_pwm){
                _pwm.start(_backlight, _pwm_duty_cycle(initial_backlight));
            }else{
                _gpio.setup(_backlight, BaseGPIO.OUT);
                _gpio.output(_backlight, initial_backlight == 1.0f ? _blpol : !_blpol);
            }
        }
        //# Initialize the display.
        write8(0x33);
        write8(0x32);
        //# Initialize display control, function, and mode registers.
        displaycontrol = LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF;
        displayfunction = LCD_4BITMODE | LCD_1LINE | LCD_2LINE | LCD_5x8DOTS;
        displaymode = LCD_ENTRYLEFT | LCD_ENTRYSHIFTDECREMENT;
        //# Write registers.
        write8(LCD_DISPLAYCONTROL | displaycontrol);
        write8(LCD_FUNCTIONSET | displayfunction);
        write8(LCD_ENTRYMODESET | displaymode); //# set the entry mode
        clear();
    }

/*
         //custom char
         write8(LCD_SETCGRAMADDR);

         int[] customGlyphs_0 = {
         Integer.valueOf("01110", 2), // 0
         Integer.valueOf("11011", 2), // 1
         Integer.valueOf("10001", 2), // 2
         Integer.valueOf("10001", 2), // 3
         Integer.valueOf("10001", 2), // 4
         Integer.valueOf("10001", 2), // 5
         Integer.valueOf("10001", 2), // 6
         Integer.valueOf("11111", 2) }; // 7

         for(int cg : customGlyphs_0){
         write8(cg,true);
         }*/

    private void home() throws IOException {
        /*"""Move the cursor back to its home (first line and first column)."""*/
        write8(LCD_RETURNHOME); // set cursor position to zero
        _delay_microseconds(3000); // this command takes a long time!
    }

    public void clear() throws IOException {
        /*"""Clear the LCD."""*/
        this.write8(LCD_CLEARDISPLAY); // command to clear display
        this._delay_microseconds(3000); // 3000 microsecond sleep, clearing the display takes a long time
    }

    public void set_cursor(int col, int row) throws IOException {
        /*"""Move the cursor to an explicit column and row position."""*/
        // Clamp row to the last row of the display.
        if (row > _lines) {
            row = _lines - 1;
        }
        //# Set location.
        this.write8(LCD_SETDDRAMADDR | (col + LCD_ROW_OFFSETS[row]));
    }

    public void enable_display(boolean enable) throws IOException {
        /*"""Enable or disable the display. Set enable to True to enable."""*/
        if (enable) {
            displaycontrol |= LCD_DISPLAYON;
        } else {
            displaycontrol &= ~LCD_DISPLAYON;
        }
        write8(LCD_DISPLAYCONTROL | displaycontrol);
    }

    public void show_cursor(boolean show) throws IOException {
        /*"""Show or hide the cursor. Cursor is shown if show is True."""*/
        if (show) {
            displaycontrol |= LCD_CURSORON;
        } else {
            displaycontrol &= ~LCD_CURSORON;
        }
        write8(LCD_DISPLAYCONTROL | displaycontrol);
    }

    public void blink(boolean blink) throws IOException {
        /*"""Turn on or off cursor blinking. Set blink to True to enable blinking."""*/
        if (blink) {
            displaycontrol |= LCD_BLINKON;
        } else {
            displaycontrol &= ~LCD_BLINKON;
        }
        write8(LCD_DISPLAYCONTROL | displaycontrol);
    }

    public void move_left() throws IOException {
        /*"""Move display left one position."""*/
        write8(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVELEFT);
    }

    public void move_right() throws IOException {
        /*"""Move display right one position."""*/
        write8(LCD_CURSORSHIFT | LCD_DISPLAYMOVE | LCD_MOVERIGHT);
    }

    public void set_left_to_right() throws IOException {
        /*"""Set text direction left to right."""*/
        displaymode |= LCD_ENTRYLEFT;
        write8(LCD_ENTRYMODESET | displaymode);
    }

    public void set_right_to_left() throws IOException {
        /*"""Set text direction right to left."""*/
        displaymode &= ~LCD_ENTRYLEFT;
        write8(LCD_ENTRYMODESET | displaymode);
    }

    public void autoscroll(boolean autoscroll) throws IOException {
        /*"""Autoscroll will 'right justify' text from the cursor if set True,
         otherwise it will 'left justify' the text.
         ""*/
        if (autoscroll) {
            displaymode |= LCD_ENTRYSHIFTINCREMENT;
        } else {
            displaymode &= ~LCD_ENTRYSHIFTINCREMENT;
            write8(LCD_ENTRYMODESET | displaymode);
        }
    }

    public void message(String text, int line) throws IOException {
        /*"""Write text to display. Note that text can include newlines."""*/
        int col = (displaymode & LCD_ENTRYLEFT) > 0 ? 0 : _cols - 1;
        this.set_cursor(col, line);
        // Iterate through each character.
        for (int i = 0; i < text.length(); i++) {
            // Advance to next line if character is a new line.

            this.write8(text.charAt(i), true);

        }
    }

    public void message(String text) throws IOException {
        /*"""Write text to display. Note that text can include newlines."""*/
        int line = 0;
        // Iterate through each character.
        for (int i = 0; i < text.length(); i++) {
            // Advance to next line if character is a new line.
            if (text.charAt(i) == '\n') {
                line += 1;
                // Move to left or right side depending on text direction.
                int col = (displaymode & LCD_ENTRYLEFT) > 0 ? 0 : _cols - 1;
                this.set_cursor(col, line);
                // Write the character to the display.
            } else {
                this.write8(text.charAt(i), true);
            }
        }
    }

    public void set_backlight(float backlight) throws IOException {
        /*"""Enable or disable the backlight. If PWM is not enabled (default), a
         non-zero backlight value will turn on the backlight and a zero value will
         turn it off. If PWM is enabled, backlight can be any value from 0.0 to
         1.0, with 1.0 being full intensity backlight.
         """*/
        if (_backlight != -1) {
            if (_pwm_enabled) {
                _pwm.set_duty_cycle(_backlight, _pwm_duty_cycle(backlight));
            } else {
                _gpio.output(_backlight, backlight == 1.0f ? _blpol : !_blpol);
            }
        }
    }
   
    /**
     * Custom glyphs into character lcd
     * 
     * @param characterCode
     * @param customGlyphs
     * @throws IOException 
     */
    public void customGlyphs(int characterCode,int[] customGlyphs) throws IOException{
        
        int cgramaddr = LCD_SETCGRAMADDR;
        if( (displayfunction & LCD_5x8DOTS) == LCD_5x8DOTS){
            cgramaddr |= characterCode * 8;
            if(characterCode > 8){
                throw new RuntimeException("Character code invalid, only [0,7] not"+characterCode);
            }
        }else if((displayfunction & LCD_5x10DOTS) == LCD_5x10DOTS){
            cgramaddr |= characterCode * 10;
            if(characterCode > 4){
                throw new RuntimeException("Character code invalid, only [0,4] not"+characterCode);
            }
        }
        write8(cgramaddr);
        for(int cg : customGlyphs){
            write8(cg,true);
        }
    }

    private void write8(int value) throws IOException {
        this.write8(value, false);
    }

    private void write8(int value, boolean char_mode) throws IOException {
         //char_mode=False
         /*
         """Write 8-bit value in character or data mode. Value should be an int
         value from 0-255, and char_mode is True if character data or False if
         non-character data (default).
         """*/
         //# One millisecond delay to prevent writing too quickly.
        _delay_microseconds(1000);
        //# Set character / data bit.
        _gpio.output(_rs, char_mode);
        //# Write upper 4 bits.
        Map<Integer,Boolean> upper = new HashMap<>();
        upper.put(_d4, ((value >> 4) & 1) > 0);
        upper.put(_d5, ((value >> 5) & 1) > 0);
        upper.put(_d6, ((value >> 6) & 1) > 0);
        upper.put(_d7, ((value >> 7) & 1) > 0);
        _gpio.output_pins(upper);
        _pulse_enable();
        //# Write lower 4 bits.
        Map<Integer,Boolean> lower = new HashMap<>();
        lower.put(_d4, (value & 1) > 0);
        lower.put(_d5, ((value >> 1) & 1) > 0);
        lower.put(_d6, ((value >> 2) & 1) > 0);
        lower.put(_d7, ((value >> 3) & 1) > 0);
        _gpio.output_pins(lower);
        _pulse_enable();
    }

    private void _delay_microseconds(int microseconds) {
        try {
            // Busy wait in loop because delays are generally very short (few microseconds).
            /*
            end = time.time() + (microseconds/1000000.0)
            while time.time() < end:
                pass
            */
            Thread.sleep(microseconds / 1000000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Adafruit_CharLCD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void _pulse_enable() throws IOException {
         //# Pulse the clock enable line off, on, off to send command.
        _gpio.output(_en, false);
        _delay_microseconds(1); //# 1 microsecond pause - enable pulse must be > 450ns
        _gpio.output(_en, true);
        _delay_microseconds(1); //# 1 microsecond pause - enable pulse must be > 450ns
        _gpio.output(_en, false);
        _delay_microseconds(1); //# commands need > 37us to settle
    }

    protected float _pwm_duty_cycle(float intensity) {
        // Convert intensity value of 0.0 to 1.0 to a duty cycle of 0.0 to 100.0
        intensity = 100.0f * intensity;
        // Invert polarity if required.
        if (!this._blpol) {
            intensity = 100.0f - intensity;
        }
        return intensity;
    }

}
