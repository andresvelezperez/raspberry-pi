raspberry-pi
============

Librerias, ejemplos y apps para ser ejecutadas en Raspberry Pi.

####Proyecto JavaMECharLCD


Es una implementacion en Java Micro Edition Embedded para mostrar texto en un "character lcd" usando Raspberry Pi como controlador. El codigo se basa en una implementacion de [adarfruit](http://www.adafruit.com) en python para controlar character LCD 16x2. El proyecto puede ejecutar los ejemplos de python de adafruit:
>
 - char_lcd.py 
 - char_lcd_rgb.py
 - char_lcd_rgb_pwm.py
 
En la clase rpi.main.Main.java, solo es comentar y descomentar las lineas para activar cada ejemplo.
 
```

          exampleCharLCD = new char_lcd();
            
          //exampleCharLCD = new char_lcd_rgb();
            
          //exampleCharLCD = new char_lcd_rgb_pwm();

```
Nota: el ejemplo char_lcd_rgb_pwm, no funciona tan bien como char_lcd_rgb_pwm.py

Para ejecutar el proyecto se necesita Jdk 1.8, Netbeans 8, Java ME Embedded SDK 8 y porsupuesto un Raspberry Pi.

Para ver como se instala el ambiente de desarrollo ver: [Instalacion Java ME Embedded](http://andrexweb.blogspot.com/2014/09/instalacion-java-me-embedded.html).
>
**Materiales:**
 - [RGB backlight positive LCD 16x2 + extras](http://www.adafruit.com/products/398)
 - Protoboard
 - WireJumpers
 - Raspberry Pi
>
**Tutoriales:**
 - [Character LCD with Raspberry Pi](https://learn.adafruit.com/character-lcd-with-raspberry-pi-or-beaglebone-black)
>
**Codigo Fuente Original (en python):**
 - [Adafruit_Python_CharLCD](https://github.com/adafruit/Adafruit_Python_CharLCD)
 - [Adafruit_Python_GPIO](https://github.com/adafruit/Adafruit_Python_GPIO)

Nota: El proyecto fue probado en un Raspberry Pi Model B+


______________________________


Raspberry Pi is a trademark of the Raspberry Pi Foundation, http://www.raspberrypi.org

Adafruit, http://www.adafruit.com

Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners. http://www.oracle.com
