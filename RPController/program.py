import EnvironmentWatcher
import ast
import time
import datetime

settings = []
with open('settings','r') as inf:
    for line in inf:
        settings.append(ast.literal_eval(line)) 

sleepDuration = settings[0]['retrieveSensorDataDuration']		
try:
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
except:
	watcher.exit()
	raise
watcher.exit()
	
