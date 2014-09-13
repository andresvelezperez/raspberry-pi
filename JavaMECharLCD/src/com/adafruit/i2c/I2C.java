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

import com.adafruit.gpio.Platform;

/**
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/I2C.py
 * 
 * @author Andres Velez Perez
 */
public class I2C {

    public static int reverseByteOrder(int data) {
        //"""Reverses the byte order of an int (16-bit) or long (32-bit) value."""
        //# Courtesy Vishal Sapre
        //int byteCount = len(hex(data)[2:].replace('L','')[::2])
        int byteCount = Integer.toBinaryString(data).length();
        int val = 0;
        for (int i = 0; i < byteCount; i++) {
            val = (val << 8) | (data & 0xff);
            data >>= 8;
        }
        return val;
    }

    public static int get_default_bus() {
        /*"""Return the default bus number based on the device platform. For a
         Raspberry Pi either bus 0 or 1 (based on the Pi revision) will be returned.
         For a Beaglebone Black the first user accessible bus, 1, will be returned.
         """*/
        int plat = Platform.platform_detect();
        if (plat == Platform.RASPBERRY_PI) {
            if (Platform.pi_revision() == 1) {
                //# Revision 1 Pi uses I2C bus 0.
                return 0;
            } else {
                //# Revision 2 Pi uses I2C bus 1.
                return 1;
            }
        } else if (plat == Platform.BEAGLEBONE_BLACK) {
            //# Beaglebone Black has multiple I2C buses, default to 1 (P9_19 and P9_20).
            return 1;
        } else {
            throw new RuntimeException("Could not determine default I2C bus for platform.");
        }
    }
}
