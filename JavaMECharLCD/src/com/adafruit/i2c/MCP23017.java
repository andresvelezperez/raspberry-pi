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

import java.io.IOException;

/**
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/MCP230xx.py
 *
 * @author Andres Velez Perez
 */
public class MCP23017 extends MCP230xxBase {

    private static final int _NUM_GPIO = 16;
    private static final int _IODIR = 0x00;
    private static final int _GPIO = 0x12;
    private static final int _GPPU = 0x0C;

    public MCP23017() throws IOException {
        this(0x20);
    }

    public MCP23017(int adress) throws IOException {
        super(adress, _NUM_GPIO, _GPPU, _IODIR, _GPIO);
    }

    public MCP23017(int adress, int busnumm) throws IOException {
        super(adress, busnumm, _NUM_GPIO, _GPPU, _IODIR, _GPIO);
    }
}
