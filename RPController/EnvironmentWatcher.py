import api
import i2c
import sensor
import hadoop
from time import gmtime, strftime
import datetime
from threading import Thread

class EnvironmentWatcher:

	def __init__(self, settings):
		self.debug = settings['debug']			
		self.productId = settings['productId']
		self.sensors_slave_addr = settings['hardware']['sensors_slave_addr']
		self.arduino_slave_addr = settings['hardware']['arduino_slave_addr']
		self.sendSensorDataDuration = settings['sendSensorDataDuration']
		self.useHadoop = settings['useHadoop']
		self.lastSendSensorData = datetime.datetime.now()
		
		self.api = api.Api(settings['API']['host'],settings['API']['username'],settings['API']['password'])
		self.api.login()
		
		try:
			self.loadProduct(self.productId)
		except:
			raise
		
		self.i2c = i2c.I2c()
		if(self.useHadoop):
			self.hadoop = hadoop.Hadoop(settings['hadoop']['host'],settings['hadoop']['username'],settings['hadoop']['password'])
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
			if(self.useHadoop == 0):
				apiData[sensor.apiName] = sensorOutput
			
			percentage = sensor.getChangePercentage(sensor.getPreferenceAverage() - sensorOutput)
			print(sensor.apiName)
			print(percentage)
			self.i2c.writeRegisterByte(self.arduino_slave_addr,sensor.cmdCode, sensor.getSpeedAngle(percentage))
		
		if((datetime.datetime.now() - self.lastSendSensorData).total_seconds() >= self.sendSensorDataDuration):
			self.lastSendSensorData = datetime.datetime.now()
			if(self.useHadoop == 0):
				print("send api data")
				print(apiData)
				self.api.newData('sensorData', apiData)
			else:
				thread = Thread(target = self.runHadoopAverageCommands, args=(apiData, ))
				thread.start()
				
	def runHadoopAverageCommands(self, apiData):
		print("started the hadoop thread!")
		threads = []
		for sensor in self.sensors:
			thread = Thread(target = self.processAverage, args=(apiData, sensor, ))
			thread.start()
			threads.append(thread)
		
		for thread in threads:
			thread.join()
		print("all threads finished")
		print("data:")
		print(apiData)
		self.api.newData('sensorData', apiData)
		print("Complete hadoop thread finished")
		
	def processAverage(self, apiData, sensor):
		print("started thread get data for sensor " + sensor.apiName)
		output = self.hadoop.createUploadWaitDownloadCatRemove(sensor.sensorData, sensor.apiName)
		apiData[sensor.apiName] = output
		print("thread for " + sensor.apiName + " finished!")
	
	def exit(self):
		for sensor in self.sensors:
			self.i2c.writeRegisterByte(self.arduino_slave_addr,sensor.cmdCode, sensor.getSpeedAngle(0))
		
		if(self.useHadoop):
			self.hadoop.close()