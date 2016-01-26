import requests

## The class Api. This class will communicate with a RESTful API
class Api:

	## The constructor
	#  @param self The object pointer.
	#  @param username The url to connect to
	#  @param username The username
	#  @param password The password
	def __init__(self, url='', username='', password=''):
		self.authCode = None
		self.url = url
		self.username = username
		self.password = password
		self.modules = ['user', 'product', 'preferences', 'sensorData']
	
	## Create a json response based on a request
	#  @param self The object pointer.
	#  @param request The request to create the response for
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def __getJsonResponse(self, request):
		request.raise_for_status()
		return request.json()

	## Login and receive a token. This token is used for the other requests
	#  @param self The object pointer.
	def login(self):
		try:
			request = requests.post(self.url + '/login', data={'username': self.username, 'password': self.password})
			request.raise_for_status()
			json = request.json()
			self.authCode = json['token']
			
		except ValueError:
			raise
		
		return True
	
	## Get all the values for the given module
	#  @param self The object pointer.
	#  @param module The module to get the values from
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def getAll(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				self.login()
				request = request.get(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	## Receive the value for the given module and id
	#  @param self The object pointer.
	#  @param module The module to get the values from
	#  @param id The unique id
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def getById(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.get(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				self.login()
				request = requests.get(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
	## Receive all the preferences for the given product id
	#  @param self The object pointer.
	#  @param id The product id
	#  @return The preferences
	#  @throw Exception if the statuscode is not 200
	def getPreferencesByProduct(self, id):
		try:
			request = requests.get(self.url + '/preferences/getByProductID/' + str(id), headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				self.login()
				request = requests.get(self.url + '/preferences/getByProductID/' + str(id), headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
		
		except ValueError:
			raise
			
	## Insert a new record
	#  @param self The object pointer.
	#  @param module The module to insert the data in
	#  @param data A json object with all the data
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def newData(self, module, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.post(self.url + '/' + module, data=data, headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				self.login()
				request = requests.post(self.url + '/' + module, data=data, headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
	
	## Update an existing record
	#  @param self The object pointer.
	#  @param module The module to insert the data in
	#  @param id The id to update
	#  @param data A json object with all the data	
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def updateData(self, module, id, data):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.put(self.url + '/' + module + '/' + str(id), data=data, headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				self.login()
				request = requests.put(self.url + '/' + module + '/' + str(id), data=data, headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise
			
	## Remove all the records of a given module
	#  @param self The object pointer.
	#  @param module The module to remove the data	
	#  @return The json response
	#  @throw Exception if the statuscode is not 200	
	def deleteAllData(self, module):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				request = requests.delete(self.url + '/' + module, headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
	
	## Remove one record of a given module
	#  @param self The object pointer.
	#  @param module The module to remove the data
	#  @param id The id to remove
	#  @return The json response
	#  @throw Exception if the statuscode is not 200
	def deleteData(self, module, id):
		if not module in self.modules:
			raise ValueError("module '" + module + "' is not found")
		
		try:
			request = requests.delete(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
			if(request.status_code == 401):
				request = requests.delete(self.url + '/' + module + '/' + str(id), headers={'X-Access-Token': self.authCode})
				
			return self.__getJsonResponse(request)
			
		except ValueError:
			raise		
