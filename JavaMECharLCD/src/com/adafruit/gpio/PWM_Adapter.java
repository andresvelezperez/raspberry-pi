

package com.adafruit.gpio;

/**
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/PWM.py
 * 
 * @author Andres Velez Perez
 */
public interface PWM_Adapter {

    void set_duty_cycle(int pin, float dutycycle);

    void set_frequency(int pin, float frequency_hz);

    void start(int pin, float dutycycle);

    void start(int pin, float dutycycle, float frequency_hz);

    void stop(int pin);
    
}
