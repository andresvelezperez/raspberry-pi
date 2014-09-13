//# Copyright (c) 2014 Adafruit Industries
//# Author: Tony DiCola
//# Based on Adafruit_I2C.py created by Kevin Townsend.
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

package com.adafruit.i2c;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 * """Class for communicating with an I2C device using the smbus library. Allows
 * reading and writing 8-bit, 16-bit, and byte array values to registers on the
 * device."""
 * 
 * https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/I2C.py
 *
 * @author Administrador
 */
public class Device {

    private int serialClock = 3400000;             // default clock 3.4MHz Max clock 
    private final int _address;
    private final I2CDevice _bus;
    private ByteBuffer command;
    private ByteBuffer byteToRead;
    private final int registrySize = 1;
    static final Logger logger = Logger.getLogger(Device.class.getName());

    public Device(int address, int busnum) throws IOException {
        //"""Create an instance of the I2C device at the specified address on the
        //specified I2C bus number."""     
        I2CDeviceConfig config = new I2CDeviceConfig(busnum, address, I2CDeviceConfig.ADDR_SIZE_7, serialClock);

        this._address = address;
        this._bus = (I2CDevice) DeviceManager.open(config);

        command = ByteBuffer.allocateDirect(registrySize);
        byteToRead = ByteBuffer.allocateDirect(1);

        logger.log(Level.ALL, String.format("Adafruit_I2C.Device.Bus.%s.Address.%s", busnum, address));
    }

    public void writeRaw8(int value) throws IOException {
        //"""Write an 8-bit value on the bus (without register)."""
        value = value & 0xFF;
        this._bus.write(value);
        logger.log(Level.ALL, String.format("Wrote %s", value));
    }

    public void write8(int register, int value) throws IOException {
        //"""Write an 8-bit value to the specified register."""
        value = value & 0xFF;
        command.clear();
        command.put((byte) value);
        command.rewind();
        this._bus.write(register, registrySize, command);
        logger.log(Level.ALL, String.format("Wrote %s to register %s", value, register));
    }

    public void write16(int register, int value) throws IOException {
        //"""Write a 16-bit value to the specified register."""
        value = value & 0xFFFF;
        command.clear();
        command.putShort((short) value);
        command.rewind();
        this._bus.write(register, registrySize, command);
        logger.log(Level.ALL, String.format("Wrote %s to register pair %s, %s", value, register, register + 1));
    }

    public void writeList(int register, byte[] data) throws IOException {
        //"""Write bytes to the specified register."""
        command.clear();
        command.put(data);
        command.rewind();
        this._bus.write(register, registrySize, command);
        logger.log(Level.ALL, String.format("Wrote to register %s: %s", register, data));
    }

    public byte[] readList(int register, int length) throws IOException {
        //"""Read a length number of bytes from the specified register. Results
        //will be returned as a bytearray."""
        ByteBuffer read = ByteBuffer.allocateDirect(length);
        read.clear();
        this._bus.read(register, registrySize, read);
        read.rewind();
        byte[] results = new byte[length];
        read.get(results);
        logger.log(Level.ALL, String.format("Read the following from register %s: %s", register, results));
        return results;
    }

    public int readRaw8() throws IOException {
        //"""Read an 8-bit value on the bus (without register)."""
        int result = this._bus.read() & 0xFF;
        logger.log(Level.ALL, String.format("Read %s", result));
        return result;
    }

    public int readU8(int register) throws IOException {
        //"""Read an unsigned byte from the specified register."""
        byteToRead = ByteBuffer.allocateDirect(1);
        byteToRead.clear();
        this._bus.read(register, registrySize, byteToRead);
        byteToRead.rewind();
        int result = byteToRead.get() & 0xFF;
        logger.log(Level.ALL, String.format("Read %s from register %s", result, register));
        return result;
    }

    public int readS8(int register) throws IOException {
        //"""Read a signed byte from the specified register."""
        int result = this.readU8(register);
        if (result > 127) {
            result -= 256;
        }
        return result;
    }

    public int readU16(int register) throws IOException {
        return readU16(register, true);
    }

    public int readU16(int register, boolean little_endian) throws IOException {
        //"""Read an unsigned 16-bit value from the specified register, with the
        //specified endianness (default little endian, or least significant byte
        //first)."""
        byteToRead = ByteBuffer.allocateDirect(2);
        byteToRead.clear();
        this._bus.read(register, registrySize, byteToRead);
        byteToRead.rewind();
        int result = byteToRead.getInt() & 0xFFFF;
        logger.log(Level.ALL, String.format("Read %s from register pair %s, %s", result, register, register + 1));
        //# Swap bytes if using big endian because read_word_data assumes little
        //# endian on ARM (little endian) systems.
        if (!little_endian) {
            result = ((result << 8) & 0xFF00) + (result >> 8);
        }
        return result;
    }

    public int readS16(int register) throws IOException {
        return readS16(register, true);
    }

    public int readS16(int register, boolean little_endian) throws IOException {
        //"""Read a signed 16-bit value from the specified register, with the
        //specified endianness (default little endian, or least significant byte
        //first)."""

        int result = this.readU16(register, little_endian);

        if (result > 32767) {
            result -= 65536;
        }
        return result;
    }

    public int readU16LE(int register) throws IOException {
        //"""Read an unsigned 16-bit value from the specified register, in little
        //endian byte order."""
        boolean little_endian = true;
        return this.readU16(register, little_endian);
    }

    public int readU16BE(int register) throws IOException {
        //"""Read an unsigned 16-bit value from the specified register, in big
        //endian byte order."""
        boolean little_endian = false;
        return this.readU16(register, little_endian);
    }

    public int readS16LE(int register) throws IOException {
        //"""Read a signed 16-bit value from the specified register, in little
        //endian byte order."""
        boolean little_endian = true;
        return this.readS16(register, little_endian);
    }

    public int readS16BE(int register) throws IOException {
        //"""Read a signed 16-bit value from the specified register, in big
        //endian byte order."""
        boolean little_endian = false;
        return this.readS16(register, little_endian);
    }

}
