import smbus
from random import uniform
import time

## The class I2c. Used for communicating over I2c
class I2c:

	## The constructor
	#  @param self The object pointer.
	def __init__(self):
		self.bus = smbus.SMBus(1)
	
	## Write a byte over i2c
	#  @param self The object pointer.
	#  @param address The address to write to
	#  @param value the byte to send
	def writeByte(self, address, value):
		self.bus.write_byte(address, value)
		time.sleep(0.001)
	
	## Write a byte in a register over i2c
	#  @param self The object pointer.
	#  @param address The address to write to
	#  @param register The register to put the byte in
	#  @param value the byte to send	
	def writeRegisterByte(self, address, register, value):
		self.bus.write_byte_data(address, register, value)
		time.sleep(0.001)

	## Read a byte over i2c
	#  @param self The object pointer.
	#  @param address The address to read from
	def readByte(self, address):
		data = self.bus.read_byte(address)
		time.sleep(0.001)
		return data
	
	## Read a byte from a register over i2c
	#  @param self The object pointer.
	#  @param address The address to read from	
	#  @param register
	def readRegisterByte(self, address, register):
		data = self.bus.read_byte_data(address, register)
		time.sleep(0.001)
		return data

	## Read the sensor temperature
	#  @param self The object pointer.
	def readSensorTemperature(self):
		#return self.readByte(0x01)
		return 17 + uniform(0, 3)
	
	## Read the light temperature
	#  @param self The object pointer.	
	def readSensorLight(self):
		#return self.readByte(0x01)
		return 16 + uniform(0, 2)
	
		