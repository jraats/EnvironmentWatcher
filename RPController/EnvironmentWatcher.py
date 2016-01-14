import api
import i2c
import sensor
from time import gmtime, strftime
import datetime

class EnvironmentWatcher:

	def __init__(self, settings):
		self.debug = settings['debug']			
		self.productId = settings['productId']
		self.sensors_slave_addr = settings['hardware']['sensors_slave_addr']
		self.arduino_slave_addr = settings['hardware']['arduino_slave_addr']
		self.sendSensorDataDuration = settings['sendSensorDataDuration']
		self.lastSendSensorData = datetime.datetime.now()
		
		self.api = api.Api(settings['API']['host'])
		self.api.login(settings['API']['username'],settings['API']['password'])
		
		try:
			self.loadProduct(self.productId)
		except:
			raise
		
		self.i2c = i2c.I2c()
		self.sensors = []
		self.initializeSensors(settings)
		
		print('started at')
		print(self.lastSendSensorData)
	
	def initializeSensors(self, settings):
		for dataSensor in settings['sensors']:
			data = settings['sensors'][dataSensor]
			self.sensors.append(
				sensor.Sensor(
					data['datalength'], data['apiName'], data['apiTresholdName'],
					data['cmdCode'], data['percentageFactor']))

	def loadProduct(self, id):
		product = self.api.getById('product', id)
		if(len(product['product']) == 0):
			raise ValueError("Product not found")
		else:
			self.product = product['product'][0]
		
	def updatePreferenceData(self):
		preferences = self.api.getPreferencesByProduct(self.productId)
		preferences = preferences['preferences']

		for sensor in self.sensors:
			sensor.clearPreferences()

		for preference in preferences:
			for sensor in self.sensors:
				sensor.addPreference(preference)
	
	def process(self):
		self.updatePreferenceData()
		
		apiData = {
				'time': strftime("%Y-%m-%dT%H:%M:%S"),
				'productId': self.productId}
		
		for sensor in self.sensors:
			sensor.addSensorData(self.i2c.readSensorTemperature())
			
			sensorOutput = sensor.getDataAverage()
			apiData[sensor.apiName] = sensorOutput
			
			percentage = sensor.getChangePercentage(sensor.getPreferenceAverage() - sensorOutput)
			print(sensor.apiName)
			print(percentage)
			self.i2c.writeRegisterByte(self.arduino_slave_addr,sensor.cmdCode, sensor.getSpeedAngle(percentage))
		
		print(apiData)
		if((datetime.datetime.now() - self.lastSendSensorData).total_seconds() >= self.sendSensorDataDuration):
			self.lastSendSensorData = datetime.datetime.now()
			self.api.newData('sensorData', apiData)
			
	def exit(self):
		for sensor in self.sensors:
			self.i2c.writeRegisterByte(self.arduino_slave_addr,sensor.cmdCode, sensor.getSpeedAngle(0))