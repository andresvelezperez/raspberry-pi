
package rpi.io;

/**
 *
 * @author Andres Velez Perez
 */
public interface PWM {

    void ChangeDutyCycle(float dutycycle);

    void ChangeFrequency(float frequency);

    void start(float dutycycle);

    void stop();
    
}
