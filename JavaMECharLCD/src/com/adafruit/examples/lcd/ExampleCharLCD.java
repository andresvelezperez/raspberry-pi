
package com.adafruit.examples.lcd;

import java.io.IOException;

/**
 *
 * @author Andres Velez Perez
 */
public interface ExampleCharLCD {

    public void init() throws IOException, InterruptedException;
    
    public void stop() throws IOException, InterruptedException;
    
}
