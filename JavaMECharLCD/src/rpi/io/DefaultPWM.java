
package rpi.io;

/**
 * https://pypi.python.org/pypi/RPi.GPIO
 * http://sourceforge.net/p/raspberry-gpio-python/code/ci/default/tree/source/
 * http://sourceforge.net/p/raspberry-gpio-python/code/ci/default/tree/source/py_pwm.c
 * 
 * @author Andres Velez Perez
 */
public class DefaultPWM implements PWM {
 
    private SoftPWM softPWM;
    private int gpio;
    private float freq;
    private float dutycycle;
    
    public DefaultPWM(int gpio,float frequency){
        
        if(frequency <= 0.0f){
            throw new RuntimeException("frequency must be greater than 0.0");
        }
        this.gpio = gpio;
        this.freq = frequency;
        softPWM = new SoftPWM(gpio);
        softPWM.pwm_set_frequency(gpio, frequency);
    }
    
    @Override
    public void start(float dutycycle){
        
        if (dutycycle < 0.0 || dutycycle > 100.0){
            throw new RuntimeException("dutycycle must have a value from 0.0 to 100.0");
        }
        
        this.dutycycle = dutycycle;
        softPWM.pwm_set_duty_cycle(gpio, dutycycle);
        softPWM.pwm_start(gpio);
    }
    
    @Override
    public void ChangeDutyCycle(float dutycycle){
        
        if (dutycycle < 0.0 || dutycycle > 100.0){
            throw new RuntimeException("dutycycle must have a value from 0.0 to 100.0");
        }
        
        this.dutycycle = dutycycle;
        softPWM.pwm_set_duty_cycle(gpio, dutycycle);
    }
    
    @Override
    public void ChangeFrequency(float frequency){
        
        if (frequency <= 0.0){
            throw new RuntimeException("frequency must be greater than 0.0");
        }
        
        this.freq = frequency;
        softPWM.pwm_set_frequency(gpio, freq);
    }
    
    @Override
    public void stop(){
        softPWM.pwm_stop(gpio);
    }
}
