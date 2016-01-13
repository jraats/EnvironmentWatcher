import api
import i2c
import collections

class EnvironmentWatcher:

	def __init__(self, settings):
		self.settings = settings
		self.debug = settings['debug']
		if(self.debug):
			print('Start EnvironmentWatcher')
			
		self.api = api.Api(settings['API']['host'])
		self.api.login(settings['API']['username'],settings['API']['password'])
		
		if(self.debug):
			print('Login successfull')
			
		self.loadProduct()
#		self.product = {'roomName': 'test', 'location': 'ergens'}
		
		self.i2c = i2c.I2c()
		self.data = {}
		self.data['temperature'] = collections.deque(maxlen=20)
		self.data['light'] = collections.deque(maxlen=20)

	def loadProduct(self):
		product = self.api.getById('product', self.settings['productId'])
		if(len(product['product']) == 0):
			if(self.debug):
				print('Product not found!')
				sys.exit(1)
		else:
			self.product = product['product'][0]
		
	def getPreferenceData(self):
		if(self.debug):
			print('Get all preferences for product')
		users = self.api.getAll('user')
		foundPreferences = []
		for user in users['user']:
			if user['productId'] == self.settings['productId']:
				preference = self.api.getById('preferences', user['username'])
				if(len(preference['preferences']) != 0):
					foundPreferences.append(preference['preferences'][0])
				else:
					if(self.debug):
						print('Preference of ' + user['username'] + ' not found. Skip!')

		#TEMP
#		foundPreferences.append({'lightTreshold': 16, 'temperatureTreshold': 20})
#		foundPreferences.append({'lightTreshold': 17, 'temperatureTreshold': 21})
		#caluclate treshold light and temprature
		avgLightTreshold = 0.0
		avgTemperatureTreshold = 0.0
		for preference in foundPreferences:
			avgLightTreshold += preference['lightTreshold']
			avgTemperatureTreshold += preference['temperatureTreshold']

		avgLightTreshold /= len(foundPreferences)
		avgTemperatureTreshold /= len(foundPreferences)
		return {'light': avgLightTreshold, 'temperature': avgTemperatureTreshold}

	def process(self):
		prefdata = self.getPreferenceData()
		print('light treshold')
		print(prefdata['light'])
		print('temp treshold')
		print(prefdata['temperature'])

		self.data['light'].append(self.i2c.readSensorLight())
		self.data['temperature'].append(self.i2c.readSensorTemperature())
		self.data['light'].append(self.i2c.readSensorLight())
		self.data['temperature'].append(self.i2c.readSensorTemperature())
		
		print('data light')
		print(self.data['light'])
		
		print('data temperature')
		print(self.data['temperature'])
		
		avgLight = self.getDataAverage(self.data['light'])
		avgTemperature = self.getDataAverage(self.data['temperature'])
		
		print('avg light')
		print(avgLight)
		
		print('avg temperature')
		print(avgTemperature)
		
		diffLight = avgLight - prefdata['light']
		diffTemperature = avgTemperature - prefdata['temperature']
		
		print('diff light')
		print(diffLight)
		
		print('diff temperature')
		print(diffTemperature)
		
		temperaturePercentage = diffTemperature * 50
		lightPercentage = diffLight * 25
		
		print('percent light')
		print(lightPercentage)
		
		print('percent temperature')
		print(temperaturePercentage)
		
		print('light speed angle')
		print(self.getSpeedAngle(lightPercentage))
		
		self.i2c.setTemperatureServo(self.getSpeedAngle(temperaturePercentage))
		self.i2c.setLightServo(self.getSpeedAngle(lightPercentage))
		
	def getDataAverage(self, data):
		total = sum(data)
		return total / float(len(data))
		
	def getSpeedAngle(self, procent):
		procent = max(-100, min(procent, 100))
		return int((procent / 100 * 90) + 90)
	