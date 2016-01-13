import requests

class Api:

	def __init__(self, url='http://85.144.219.90:1337/api'):
		self.authCode = None
		self.url = url
		self.modules = ['user', 'product', 'preferences', 'sensorData']
		
	def __getJsonResponse(self, request):
		request.raise_for_status()
		return request.json()

	def login(self, username, password):
		try:
			request = requests.post(self.url + '/login', data={'username': username, 'password': password})
			request.raise_for_status()
			json = request.json()
			self.authCode = json['token']
			
		except ValueError:
			raise
		
		return True
	
	def getAll(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def getById(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def newData(self, module, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.post(self.url + '/' + module, data=data, headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def updateData(self, module, id, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.put(self.url + '/' + module + '/' + str(id), data=data, headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	def deleteAllData(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
	
	def deleteData(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
