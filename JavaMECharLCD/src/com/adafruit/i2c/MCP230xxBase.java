//# Copyright (c) 2014 Adafruit Industries
//# Author: Tony DiCola
//# Based on Adafruit_I2C.py created by Kevin Townsend.
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

package com.adafruit.i2c;

import com.adafruit.gpio.BaseGPIO;
import java.io.IOException;
import static java.lang.Math.ceil;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/MCP230xx.py
 * 
 * @author Andres Velez Perez
 */
public class MCP230xxBase extends BaseGPIO {

    private Device _i2c;
    private int NUM_GPIO;
    private int gpio_bytes;
    private byte[] iodir;
    private byte[] gppu;
    private byte[] gpio;
    private int GPPU;
    private int IODIR;
    private int GPIO;

    public MCP230xxBase(int address,int num_gpio,int gppu,int iodir,int gpio) throws IOException {
        this(address, I2C.get_default_bus(),num_gpio,gppu,iodir, gpio);
    }

    public MCP230xxBase(int address, int busnum,int num_gpio,int gppu,int iodir,int gpio) throws IOException {
        //"""Initialize MCP230xx at specified I2C address and bus number. If bus
        //is not specified it will default to the appropriate platform detected bus.
        //"""
        this.NUM_GPIO = num_gpio;
        this.GPPU = gppu;
        this.IODIR = iodir;
        this.GPIO = gpio;
        
        this._i2c = new Device(address, busnum);
        //# Assume starting in ICON.BANK = 0 mode (sequential access).
        //# Compute how many bytes are needed to store count of GPIO.
        this.gpio_bytes = (int) ceil(this.NUM_GPIO / 8.0f);
        //# Buffer register values so they can be changed without reading.
        this.iodir = new byte[this.gpio_bytes]; //# Default direction to all inputs.
        this.gppu = new byte[this.gpio_bytes]; //# Default to pullups disabled.
        this.gpio = new byte[this.gpio_bytes];
        //defaul value 0x00

        //# Write current direction and pullup buffer state.
        this.write_iodir();
        this.write_gppu();
    }

    public void _validate_pin(int pin) {
        //# Raise an exception if pin is outside the range of allowed values.
        if (pin < 0 || pin >= this.NUM_GPIO) {
            throw new RuntimeException("Invalid GPIO value, must be between 0 and " + this.NUM_GPIO);
        }
    }

    @Override
    public void setup(int pin, int value) {
        //"""Set the input or output mode for a specified pin. Mode should be
        //either GPIO.OUT or GPIO.IN.
        //"""
        this._validate_pin(pin);
        //# Set bit to 1 for input or 0 for output.
        if (value == BaseGPIO.IN) {
            this.iodir[(int) (pin / 8)] |= 1 << (int) (pin % 8);
        } else if (value == BaseGPIO.OUT) {
            this.iodir[(int) (pin / 8)] &= ~(1 << (int) (pin % 8));
        } else {
            throw new RuntimeException("Unexpected value. Must be GPIO.IN or GPIO.OUT.");
        }
        try {
            this.write_iodir();
        } catch (IOException ex) {
            Logger.getLogger(MCP230xxBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void output(int pin, boolean value) {
        //"""Set the specified pin the provided high/low value. Value should be
        //either GPIO.HIGH/GPIO.LOW or a boolean (True = high).
        //"""
        this._validate_pin(pin);
        //# Set bit on or off.
        if (value) {
            this.gpio[(int) (pin / 8)] |= 1 << (int) (pin % 8);
        } else {
            this.gpio[(int) (pin / 8)] &= ~(1 << (int) (pin % 8));
        }
        try {
            //# Write GPIO state.
            this.write_gpio();
        } catch (IOException ex) {
            Logger.getLogger(MCP230xxBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void output_pins(Map<Integer, Boolean> pins) {
        //"""Set multiple pins high or low at once. Pins should be a dict of pin
        //name to pin value (HIGH/True for 1, LOW/False for 0). All provided pins
        //will be set to the given values.
        //"""
        //# Set each changed pin's bit.
        for (Map.Entry<Integer, Boolean> pin : pins.entrySet()) {
            if (pin.getValue()) {
                this.gpio[(int) (pin.getKey() / 8)] |= 1 << (int) (pin.getKey() % 8);
            } else {
                this.gpio[(int) (pin.getKey() / 8)] &= ~(1 << (int) (pin.getKey() % 8));
            }
        }
        try {
            //# Write GPIO state.
            this.write_gpio();
        } catch (IOException ex) {
            Logger.getLogger(MCP230xxBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean input(int pin) {
        //"""Read the specified pin and return GPIO.HIGH/True if the pin is pulled
        //high, or GPIO.LOW/False if pulled low.
        //"""
        this._validate_pin(pin);
        try {
            //# Get GPIO state.
            gpio = this._i2c.readList(this.GPIO, this.gpio_bytes);
        } catch (IOException ex) {
            Logger.getLogger(MCP230xxBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        //# Return True if pin's bit is set.
        return (gpio[(int) (pin / 8)] & 1 << (int) (pin % 8)) > 0;
    }

    public void pullup(int pin, boolean enabled) throws IOException {
        //"""Turn on the pull-up resistor for the specified pin if enabled is True,
        //otherwise turn off the pull-up resistor.
        //"""
        this._validate_pin(pin);
        if (enabled) {
            this.gppu[(int) (pin / 8)] |= 1 << (int) (pin % 8);
        } else {
            this.gppu[(int) (pin / 8)] &= ~(1 << (int) (pin % 8));
        }
        this.write_gppu();
    }

    public void write_gpio() throws IOException {
        write_gpio(null);
    }

    public void write_gpio(byte[] gpio) throws IOException {
        //"""Write the specified byte value to the GPIO registor. If no value
        //specified the current buffered value will be written.
        //"""
        if (gpio != null) {
            this.gpio = gpio;
        }
        this._i2c.writeList(this.GPIO, this.gpio);
    }

    public void write_iodir() throws IOException {
        write_iodir(null);
    }

    public void write_iodir(byte[] iodir) throws IOException {
        //"""Write the specified byte value to the IODIR registor. If no value
        //specified the current buffered value will be written.
        //"""
        if (iodir != null) {
            this.iodir = iodir;
        }
        this._i2c.writeList(this.IODIR, this.iodir);
    }

    public void write_gppu() throws IOException {
        write_gppu(null);
    }

    public void write_gppu(byte[] gppu) throws IOException {
        //"""Write the specified byte value to the GPPU registor. If no value
        //specified the current buffered value will be written.
        //"""
        if (gppu != null) {
            this.gppu = gppu;
        }
        this._i2c.writeList(this.GPPU, this.gppu);
    }

}
