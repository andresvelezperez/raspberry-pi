
package rpi.main;

import com.adafruit.examples.lcd.ExampleCharLCD;
import com.adafruit.examples.lcd.char_lcd;
import com.adafruit.examples.lcd.char_lcd_mcp;
import com.adafruit.examples.lcd.char_lcd_rgb;
import com.adafruit.examples.lcd.char_lcd_rgb_pwm;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Andres Velez Perez
 */
public class Main extends MIDlet {
    
    
    ExampleCharLCD exampleCharLCD = null;
       
    char_lcd_rgb_pwm lcd_rgb_pwm;
    @Override
    public void startApp() {
        try {

            //exampleCharLCD = new char_lcd();
            
            //exampleCharLCD = new char_lcd_rgb();
            
            //exampleCharLCD = new char_lcd_rgb_pwm();
            
            exampleCharLCD = new char_lcd_mcp();

            
            exampleCharLCD.init();


        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        
        if(exampleCharLCD != null){
            try {
                exampleCharLCD.stop();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
