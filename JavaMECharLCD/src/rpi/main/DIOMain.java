
package rpi.main;

import com.adafruit.examples.lcd.ExampleCharLCD;
import com.adafruit.examples.lcd.char_lcd;
import com.adafruit.examples.lcd.char_lcd_mcp;
import com.adafruit.examples.lcd.char_lcd_plate;
import com.adafruit.examples.lcd.char_lcd_rgb;
import com.adafruit.examples.lcd.char_lcd_rgb_pwm;
import java.io.IOException;

/**
 *
 * @author andresvelezperez
 */
public class DIOMain {

    public static void main(String args[]) throws IOException, InterruptedException {
        
        ExampleCharLCD exampleCharLCD = null;
        
        //exampleCharLCD = new char_lcd();

        //exampleCharLCD = new char_lcd_rgb();

        //exampleCharLCD = new char_lcd_rgb_pwm();

        //exampleCharLCD = new char_lcd_mcp();
        
        exampleCharLCD = new char_lcd_plate();
        
        exampleCharLCD.init();
        exampleCharLCD.stop();
    }

}
