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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * """GPIO implementation for the Raspberry Pi using the RPi.GPIO library."""
 * 
 * @see https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/GPIO.py
 * 
 * @author Andres Velez Perez
 */
public class RPiGPIOAdapter extends BaseGPIO{
    
    private Map<Integer,GPIOPin> pins = new HashMap<Integer,GPIOPin>();
    
    public RPiGPIOAdapter(){        
    }

    /**
     * """Set the input or output mode for a specified pin. Mode should be
     *  either OUTPUT or INPUT. 
     * """
     * @param pin
     * @param mode 
     */
    @Override
    public void setup(int pin, int mode) {
        
        try {
            GPIOPinConfig gPIOPinConfig = new GPIOPinConfig(0,
                    pin,
                    mode == IN ? GPIOPinConfig.DIR_INPUT_ONLY : GPIOPinConfig.DIR_OUTPUT_ONLY ,
                    mode == IN ? GPIOPinConfig.MODE_INPUT_PULL_UP : GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                    mode == IN ? GPIOPinConfig.TRIGGER_BOTH_EDGES : GPIOPinConfig.TRIGGER_BOTH_EDGES ,
                    false);
            
            GPIOPin gPIOPin = DeviceManager.open(gPIOPinConfig);
            gPIOPin.setDirection(mode == IN ? GPIOPin.INPUT : GPIOPin.OUTPUT);
            pins.put(pin, gPIOPin);
        } catch (IOException ex) {
            Logger.getLogger(RPiGPIOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * """Set the specified pin the provided high/low value. Value should be
     *  either HIGH/LOW or a boolean (true = high).
     * """
     * @param pin
     * @param value 
     */
    @Override
    public void output(int pin, boolean value) {
        try {
            this.pins.get(pin).setValue(value);
        } catch (IOException ex) {
            Logger.getLogger(RPiGPIOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * """Read the specified pin and return HIGH/true if the pin is pulled high,
     *  or LOW/false if pulled low.
     *  """
     * @param pin
     * @return 
     */
    @Override
    public boolean input(int pin) {
        try {
            return this.pins.get(pin).getValue();
        } catch (IOException ex) {
            Logger.getLogger(RPiGPIOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
