import smbus
from random import uniform
import time

class I2c:

	def __init__(self):
		self.bus = smbus.SMBus(1)
	
	def writeByte(self, address, value):
		self.bus.write_byte(address, value)
		time.sleep(0.001)
		
	def writeRegisterByte(self, address, register, value):
		self.bus.write_byte_data(address, register, value)
		time.sleep(0.001)

	def readByte(self, address):
		data = self.bus.read_byte(address)
		time.sleep(0.001)
		return data
		
	def readRegisterByte(self, address, register):
		data = self.bus.read_byte_data(address, register)
		time.sleep(0.001)
		return data

	def readSensorTemperature(self):
		#return self.readByte(0x01)
		return 17 + uniform(0, 3)
		
	def readSensorLight(self):
		#return self.readByte(0x01)
		return 16 + uniform(0, 2)
	
		