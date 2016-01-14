import collections

class Sensor:

	def __init__(self, maxDataLength, apiName, apiTresholdName, cmdCode, percentageFactor):
		self.sensorData = collections.deque(maxlen=maxDataLength)
		
		self.preferences = []
		self.apiName = apiName
		self.apiTresholdName = apiTresholdName
		self.cmdCode = cmdCode
		self.percentageFactor = percentageFactor
		
	def clearPreferences(self):
		self.preferences[:] = []
	
	def addPreference(self, dataDictionary):
		self.preferences.append(dataDictionary[self.apiTresholdName])
		
	def getPreferenceAverage(self):
		total = sum(self.preferences)
		return total / float(len(self.preferences))
		
	def addSensorData(self, data):
		self.sensorData.append(data)
	
	def getDataAverage(self):
		total = sum(self.sensorData)
		return total / float(len(self.sensorData))
	
	def getDataDifference(self):
		return self.getPreferenceAverage() - self.getDataAverage()
	
	def getChangePercentage(self, dataDifference):
		return dataDifference * self.percentageFactor
	
	def getSpeedAngle(self, percentage=None):
		if(percentage == None):
			percentage = self.getChangePercentage(self.getDataDifference())
		procent = max(-100, min(percentage, 100))
		return int((procent / 100 * 90) + 90)