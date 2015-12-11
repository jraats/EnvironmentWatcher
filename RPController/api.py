import requests

class Api:

	def __init__(self, url='http://mywebdatabase.joostraats.nl/api'):
		self.authCode = None
		self.url = url
		self.modules = ['user', 'product', 'preferences', 'sensorData']
		
	def __getJsonResponse(self, request):
		request.raise_for_status()
		return request.json()

	def login(self, username, password):
		try:
			request = requests.get(self.url + '/login')
			request.raise_for_status()
			json = request.json()
			self.authCode = json['code']
			
		except ValueError:
			raise
		
		return True
	
	def getAll(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def getById(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module + '/' + id)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def newData(self, module, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.post(self.url + '/' + module, data=data)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def updateData(self, module, id, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.put(self.url + '/' + module + '/' + id, data=data)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def deleteAllData(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
	
	def deleteData(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module + '/' + id)
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
