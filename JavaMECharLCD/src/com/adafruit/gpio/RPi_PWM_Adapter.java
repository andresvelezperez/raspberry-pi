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

import java.util.HashMap;
import java.util.Map;
import rpi.io.DefaultPWM;
import rpi.io.PWM;

/**
 * """PWM implementation for the Raspberry Pi using the RPi.GPIO PWM library."""
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/PWM.py
 * 
 * @author Andres Velez Perez
 */
public class RPi_PWM_Adapter implements PWM_Adapter{
    
    private Map<Integer,PWM> pwm = new HashMap<>();
    
    @Override
    public void start(int pin,float dutycycle){
         start( pin, dutycycle, 2000);
    }
    
    @Override
    public void start(int pin,float dutycycle,float frequency_hz){
        //"""Enable PWM output on specified pin. Set to intiial percent duty cycle
        //value (0.0 to 100.0) and frequency (in Hz).
        //"""
        if(dutycycle < 0.0f || dutycycle > 100.0f){
            throw new RuntimeException("Invalid duty cycle value, must be between 0.0 to 100.0 (inclusive).");
        }
        //# Create PWM instance and save a reference for later access.
        pwm.put(pin, new DefaultPWM(pin, frequency_hz));
        //# Start the PWM at the specified duty cycle.
        pwm.get(pin).start(dutycycle);
    }
    
    @Override
    public void set_duty_cycle(int pin,float dutycycle){
        //"""Set percent duty cycle of PWM output on specified pin. Duty cycle must
        //be a value 0.0 to 100.0 (inclusive).
        //"""        
        if(dutycycle < 0.0f || dutycycle > 100.0f){
                throw new RuntimeException("Invalid duty cycle value, must be between 0.0 to 100.0 (inclusive).");
        }
        if(!pwm.containsKey(pin)){
            throw new RuntimeException(String.format("Pin %s is not configured as a PWM. Make sure to first call start for the pin.", pin));
        }

        pwm.get(pin).ChangeDutyCycle(dutycycle);
    }
    
    @Override
    public void set_frequency(int pin,float frequency_hz){
        //"""Set frequency (in Hz) of PWM output on specified pin."""
        if(!pwm.containsKey(pin)){
            throw new RuntimeException(String.format("Pin %s is not configured as a PWM. Make sure to first call start for the pin.", pin));
        }
        pwm.get(pin).ChangeFrequency(frequency_hz);
    }
    
    @Override
    public void stop(int pin){
        //"""Stop PWM output on specified pin."""
        if(!pwm.containsKey(pin)){
            throw new RuntimeException(String.format("Pin %s is not configured as a PWM. Make sure to first call start for the pin.", pin));
        }
        pwm.get(pin).stop();
        pwm.remove(pin);
    }
}
