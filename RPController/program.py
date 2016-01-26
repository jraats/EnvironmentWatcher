import EnvironmentWatcher
import ast
import time
import datetime

#convert the settings file to a dictionary
settings = []
with open('settings','r') as inf:
    for line in inf:
        settings.append(ast.literal_eval(line)) 

#get the sleep duration
sleepDuration = settings[0]['retrieveSensorDataDuration']		
watcher = None
try:
	#initialize the watcher
	watcher = EnvironmentWatcher.EnvironmentWatcher(settings[0])
	
	print('Current product:')
	print('Room name:' + watcher.product['roomName'])
	print('Location:' + watcher.product['location'])

	while True:
		start = datetime.datetime.now()
		watcher.process()
		
		sleepAmount = sleepDuration - (datetime.datetime.now() - start).total_seconds()
		if(sleepAmount > 0):
			time.sleep(sleepAmount)
#		break
finally:
	if(watcher != None):
		watcher.exit()
	
