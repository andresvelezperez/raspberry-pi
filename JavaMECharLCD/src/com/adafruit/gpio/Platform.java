//# Copyright (c) 2014 Adafruit Industries
//# Author: Tony DiCola
//# Permission is hereby granted, free of charge, to any person obtaining a copy
//# of this software and associated documentation files (the "Software"), to deal
//# in the Software without restriction, including without limitation the rights
//# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//# copies of the Software, and to permit persons to whom the Software is
//# furnished to do so, subject to the following conditions:
//# The above copyright notice and this permission notice shall be included in all
//# copies or substantial portions of the Software.
//# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//# SOFTWARE.

package com.adafruit.gpio;

/**
 * 
 * @see https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/Platform.py
 * 
 * @author Andres Velez Perez
 */
public class Platform {
    
    //# Platform identification constants.
    public static final int UNKNOWN = 0;
    public static final int RASPBERRY_PI = 1;
    public static final int BEAGLEBONE_BLACK = 2;
    
    /**
     * """Detect if running on the Raspberry Pi or Beaglebone Black and return the
     *  platform type. Will return RASPBERRY_PI, BEAGLEBONE_BLACK, or UNKNOWN."""
     * 
     * @return 
     */
    public static int platform_detect(){
        return RASPBERRY_PI;
    }
    
    /**
     * """Detect the revision number of a Raspberry Pi, useful for changing
     *  functionality like default I2C bus based on revision."""
     * 
     * @return 
     */
    public static int pi_revision(){
        return 2;
    }
    
    public static PWM_Adapter get_platform_pwm() {
        int plat = Platform.platform_detect();
        if(plat == RASPBERRY_PI){
            return new RPi_PWM_Adapter();
        }else{
            throw new RuntimeException("Could not determine platform.");
        }
    }
    
    public static BaseGPIO get_platform_gpio(){
        int plat = Platform.platform_detect();
        if(plat == RASPBERRY_PI){
            return new RPiGPIOAdapter();
        }else{
            throw new RuntimeException("Could not determine platform.");
        }
    }
}
