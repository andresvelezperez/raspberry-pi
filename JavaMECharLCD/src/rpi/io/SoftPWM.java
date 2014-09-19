/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi.io;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * https://pypi.python.org/pypi/RPi.GPIO
 * http://sourceforge.net/p/raspberry-gpio-python/code/ci/default/tree/source/
 * http://sourceforge.net/p/raspberry-gpio-python/code/ci/default/tree/source/soft_pwm.c
 *
 * @author Andres Velez Perez
 */
public class SoftPWM implements Runnable {
    
    private class StructPWM {
        public int gpio;
        public float freq;
        public float dutycycle;
        public float basetime;
        public float slicetime;
        public TimeSpec req_on, req_off;
        public volatile boolean running;
        public StructPWM next;
        public GPIOPin gPIOPin;
    }

    private class TimeSpec {
        public long tv_sec;
        public int tv_nsec;
    }

    private Thread pthread;
    private StructPWM structPWM;

    public SoftPWM(int gpio) {
        this.structPWM = newStructPWM(gpio);
    }

    private void calculate_times() {
        long usec;
        usec = (long) (structPWM.dutycycle * structPWM.slicetime * 1000.0);
        structPWM.req_on.tv_sec = (int) (usec / 1000000L);
        usec -= (long) structPWM.req_on.tv_sec * 1000000L;
        structPWM.req_on.tv_nsec = (int) usec * 1000;
        usec = (long) ((100.0 - structPWM.dutycycle) * structPWM.slicetime * 1000.0);
        structPWM.req_off.tv_sec = (int) (usec / 1000000L);
        usec -= (long) structPWM.req_off.tv_sec * 1000000L;
        structPWM.req_off.tv_nsec = (int) usec * 1000;
    }

    public void full_sleep(TimeSpec req) {
        try {
            //pthread.sleep(req.tv_sec, req.tv_nsec);
            pthread.sleep(req.tv_sec);
        } catch (InterruptedException ex) {
            Logger.getLogger(SoftPWM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        while (structPWM.running) {
            if (structPWM.dutycycle > 0.0f) {
                try {
                    structPWM.gPIOPin.setValue(true);
                } catch (IOException ex) {
                    Logger.getLogger(SoftPWM.class.getName()).log(Level.SEVERE, null, ex);
                }
                full_sleep(structPWM.req_on);
            }
            if (structPWM.dutycycle < 100.0f) {
                try {
                    structPWM.gPIOPin.setValue(false);
                } catch (IOException ex) {
                    Logger.getLogger(SoftPWM.class.getName()).log(Level.SEVERE, null, ex);
                }
                full_sleep(structPWM.req_off);
            }
        }
        try {
            // clean up
            structPWM.gPIOPin.setValue(true);
        } catch (IOException ex) {
            Logger.getLogger(SoftPWM.class.getName()).log(Level.SEVERE, null, ex);
        }
         pthread.interrupt();
    }
    
    private StructPWM newStructPWM(int gpio) {
        StructPWM new_pwm = new StructPWM();
        new_pwm.gpio = gpio;
        new_pwm.running = false;
        new_pwm.next = null;
        // default to 1 kHz frequency, dutycycle 0.0
        new_pwm.freq = 1000.0f;
        new_pwm.dutycycle = 0.0f;
        new_pwm.basetime = 1.0f; // 1 ms
        new_pwm.slicetime = 0.01f; // 0.01 ms

        new_pwm.req_off = new TimeSpec();
        new_pwm.req_on = new TimeSpec();
        
        GPIOPinConfig gPIOPinConfig = new GPIOPinConfig(0,
                gpio,
                GPIOPinConfig.DIR_OUTPUT_ONLY,
                GPIOPinConfig.MODE_OUTPUT_PUSH_PULL,
                GPIOPinConfig.TRIGGER_RISING_EDGE,
                false);

        try {
            new_pwm.gPIOPin = DeviceManager.open(gPIOPinConfig);
        } catch (IOException ex) {
            Logger.getLogger(SoftPWM.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.structPWM = new_pwm;
        calculate_times();
        return new_pwm;
    }

    public void pwm_set_duty_cycle(int gpio, float dutycycle) {

        if (dutycycle < 0.0 || dutycycle > 100.0) {
            // btc fixme - error
            return;
        }
        structPWM.dutycycle = dutycycle;
        calculate_times();
    }

    public void pwm_set_frequency(int gpio, float freq) {
        if (freq <= 0.0) // to avoid divide by zero
        {
            // btc fixme - error
            return;
        }
        structPWM.basetime = 1000.0f / freq; // calculated in ms
        structPWM.slicetime = structPWM.basetime / 100.0f;
        calculate_times();

    }

    public void pwm_start(int gpio) {

        pthread = new Thread(this);
        structPWM.running = true;
        pthread.start();
    }

    public void pwm_stop(int gpio) {
        if (structPWM != null) {
            structPWM.running = false;
        }
    }

}
