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
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.HashMap;
import java.util.Map;

/**
 * https://github.com/adafruit/Adafruit_Python_CharLCD/blob/master/Adafruit_CharLCD/Adafruit_CharLCD.py
 * 
 * @author Andres Velez Perez
 */
public class Adafruit_RGBCharLCD extends Adafruit_CharLCD {

    private final int _red;
    private final int _green;
    private final int _blue;

       public Adafruit_RGBCharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines, int red, int green, int blue,boolean enable_pwm) throws IOException {
        this(rs, en, d4, d5, d6, d7, cols, lines, red, green, blue,
                Platform.get_platform_gpio(),
                true,
                enable_pwm,
                Platform.get_platform_pwm(),
                new float[]{1.0f, 1.0f, 1.0f});
    } 
    
    public Adafruit_RGBCharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines, int red, int green, int blue) throws IOException {
        this(rs, en, d4, d5, d6, d7, cols, lines, red, green, blue,
                Platform.get_platform_gpio(),
                true,
                false,
                Platform.get_platform_pwm(),
                new float[]{1.0f, 1.0f, 1.0f});
    }

    public Adafruit_RGBCharLCD(int rs, int en, int d4, int d5, int d6, int d7, int cols, int lines, int red, int green, int blue,
            BaseGPIO gpio, boolean invert_polarity, boolean enable_pwm, PWM_Adapter pwm, float[] initial_color) throws IOException {
        //super(rs, en, d4, d5, d6, d7, cols,lines,enable_pwm,-1,invert_polarity,gpio,pwm);
        super(rs, en, d4, d5, d6, d7, cols, lines, -1, invert_polarity, enable_pwm, gpio, pwm, 1.0f);

        this._red = red;
        this._green = green;
        this._blue = blue;

        //# Setup backlight pins.
        if (enable_pwm) {
            //# Determine initial backlight duty cycles.
            float[] rgb = _rgb_to_duty_cycle(initial_color);
            float rdc = rgb[0];
            float gdc = rgb[1];
            float bdc = rgb[2];
            pwm.start(red, rdc);
            pwm.start(green, gdc);
            pwm.start(blue, bdc);
        } else {
            gpio.setup(red, BaseGPIO.OUT);
            gpio.setup(green, BaseGPIO.OUT);
            gpio.setup(blue, BaseGPIO.OUT);
            _gpio.output_pins(_rgb_to_pins(initial_color));
        }
    }

    public float[] _rgb_to_duty_cycle(float[] rgb) {
        //# Convert tuple of RGB 0-1 values to tuple of duty cycles (0-100).
        float red = rgb[0];
        float green = rgb[1];
        float blue = rgb[2];
        //# Clamp colors between 0.0 and 1.0
        red = (float) max(0.0, min(1.0, red));
        green = (float) max(0.0, min(1.0, green));
        blue = (float) max(0.0, min(1.0, blue));

        return new float[]{_pwm_duty_cycle(red), _pwm_duty_cycle(green), _pwm_duty_cycle(blue)};
    }

    public Map<Integer, Boolean> _rgb_to_pins(float[] rgb) {
        //# Convert tuple of RGB 0-1 values to dict of pin values.
        float red = rgb[0];
        float green = rgb[1];
        float blue = rgb[2];

        Map<Integer, Boolean> pins = new HashMap<>();
        pins.put(_red, red == 1.0f ? _blpol : !_blpol);
        pins.put(_green, green == 1.0f ? _blpol : !_blpol);
        pins.put(_blue, blue == 1.0f ? _blpol : !_blpol);

        return pins;

    }

    public void set_color(float red, float green, float blue) throws IOException {
        /*"""Set backlight color to provided red, green, and blue values. If PWM
         is enabled then color components can be values from 0.0 to 1.0, otherwise
         components should be zero for off and non-zero for on.
         """*/
        if (_pwm_enabled) {
            //# Set duty cycle of PWM pins.
            float[] rgbdc = _rgb_to_duty_cycle(new float[]{red, green, blue});
            _pwm.set_duty_cycle(_red, rgbdc[0]);
            _pwm.set_duty_cycle(_green, rgbdc[1]);
            _pwm.set_duty_cycle(_blue, rgbdc[2]);
        } else {
            //# Set appropriate backlight pins based on polarity and enabled colors.
            Map<Integer, Boolean> pins = new HashMap<>();
            pins.put(_red, red == 1.0f ? _blpol : !_blpol);
            pins.put(_green, green == 1.0f ? _blpol : !_blpol);
            pins.put(_blue, blue == 1.0f ? _blpol : !_blpol);
            _gpio.output_pins(pins);
        }
    }

    @Override
    public void set_backlight(float backlight) throws IOException {
        /*"""Enable or disable the backlight. If PWM is not enabled (default), a
         non-zero backlight value will turn on the backlight and a zero value will
         turn it off. If PWM is enabled, backlight can be any value from 0.0 to
         1.0, with 1.0 being full intensity backlight. On an RGB display this
         function will set the backlight to all white.
         """*/
        set_color(backlight, backlight, backlight);
    }
    
    public void stopPWM(){
        _pwm.stop(_red);
        _pwm.stop(_green);
        _pwm.stop(_blue);
        _pwm_enabled = false;
    }
}
