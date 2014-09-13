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

package com.adafruit.gpio;

import java.util.Map;
import java.util.Map.Entry;

/**
 * """Base class for implementing simple digital IO for a platform.
 * Implementors are expected to subclass from this and provide an implementation
 * of the setup, output, and input functions."""
 * 
 * @see https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/GPIO.py
 * 
 * @author Andres Velez Perez
 */
public abstract class BaseGPIO {
    
    public static final int OUT = 0;
    public static final int IN = 1;
    public static final boolean HIGH = true ;
    public static final boolean LOW = false;
    
    /**
     * """Set the input or output mode for a specified pin. Mode should be
     *  either OUT or IN."""
     * @param pin
     * @param mode 
     */
    public abstract void setup(int pin, int mode);
    
    /**
     * """Set the specified pin the provided high/low value. Value should be
     *  either HIGH/LOW or a boolean (true = high)."""
     * @param pin
     * @param value 
     */
    public abstract void output(int pin, boolean value);
    
    /**
     * """Read the specified pin and return HIGH/true if the pin is pulled high,
     *  or LOW/false if pulled low."""
     * @param pin
     * @return 
     */
    public abstract boolean input(int pin);
    
    /**
     * """Set the specified pin HIGH."""
     * @param pin 
     */
    public void set_high(int pin){
        this.output(pin, HIGH);
    }
    
    /**
     * """Set the specified pin LOW."""
     * @param pin 
     */
    public void set_low(int pin){
        this.output(pin, LOW);
    }
    
    /**
     * """Return true if the specified pin is pulled high."""
     * @param pin
     * @return 
     */
    public boolean is_high(int pin){
        return this.input(pin) == HIGH;
    }
    
    /**
     * """Return true if the specified pin is pulled low."""
     * @param pin
     * @return 
     */
    public boolean is_low(int pin){
        return this.input(pin) == LOW;
    }
    
    /**
     * """Set multiple pins high or low at once. Pins should be a dict of pin
     *  name to pin value (HIGH/True for 1, LOW/False for 0). All provided pins
     *  will be set to the given values."""
     * @param pins 
     */
    public void output_pins(Map<Integer,Boolean> pins){
        //# General implementation just loops through pins and writes them out
        //# manually. This is not optimized, but subclasses can choose to implement
        //# a more optimal batch output implementation. See the MCP230xx class for
        //# example of optimized implementation.
        for (Entry<Integer,Boolean> pin : pins.entrySet()) {
            this.output(pin.getKey(), pin.getValue());
        }
    }
}
