import collections

## The class Sensor. This contains all the sensor information
class Sensor:

	## The constructor
	#  @param self The object pointer.
	#  @param maxDataLength The number of samples to use for the average algorithm
	#  @param apiName The name of the sensor in the API. The database column name
	#  @param apiTresholdName The name of the sensor in the API. The treshold name
	#  @param cmdCode The command or register code that is used to rotate the motor
	#  @param percentageFactor The factor that is used to calculate the percentage. This value is multiplied by the difference of the preference average and sensor average
	def __init__(self, maxDataLength, apiName, apiTresholdName, cmdCode, percentageFactor):
		self.sensorData = collections.deque(maxlen=maxDataLength)
		
		self.preferences = []
		self.apiName = apiName
		self.apiTresholdName = apiTresholdName
		self.cmdCode = cmdCode
		self.percentageFactor = percentageFactor
		
	## Remove all the preferences
	#  @param self The object pointer.
	def clearPreferences(self):
		self.preferences[:] = []
	
	## Add a preference
	#  @param self The object pointer.
	#  @param dataDictionary The dictionary that contains the preference
	def addPreference(self, dataDictionary):
		self.preferences.append(dataDictionary[self.apiTresholdName])
	
	## Get the average of the preferences
	#  @param self The object pointer.
	def getPreferenceAverage(self):
		total = sum(self.preferences)
		return total / float(len(self.preferences))
		
	## add the sensor data
	#  @param self The object pointer.	
	#  @param data The data to add
	def addSensorData(self, data):
		self.sensorData.append(data)

	## Get the average of the data
	#  @param self The object pointer.		
	def getDataAverage(self):
		total = sum(self.sensorData)
		return total / float(len(self.sensorData))

	## Get the difference between the average of the preference and the average of the data
	#  @param self The object pointer.			
	def getDataDifference(self):
		return self.getPreferenceAverage() - self.getDataAverage()
	
	## Get the percentage of how many the motor needs to be changed
	#  @param self The object pointer.				
	#  @param dataDifference the difference between the average of the preference and the average of the data. \see Sensor::getDataDifference
	def getChangePercentage(self, dataDifference):
		return dataDifference * self.percentageFactor
	
	## Get the value that is used to rotate the motors
	#  @param self The object pointer.				
	#  @param percentage The percentage of the motor rotation. \see Sensor::getChangePercentage
	def getSpeedAngle(self, percentage=None):
		if(percentage == None):
			percentage = self.getChangePercentage(self.getDataDifference())
		procent = max(-100, min(percentage, 100))
		return int((procent / 100 * 90) + 90)