import EnvironmentWatcher
import ast
import time

settings = []
with open('settings.txt','r') as inf:
    for line in inf:
        settings.append(ast.literal_eval(line)) 

watcher = EnvironmentWatcher.EnvironmentWatcher(settings[0])
	
print('Current product:')
print('Room name:' + watcher.product['roomName'])
print('Location:' + watcher.product['location'])

while True:
	watcher.process()
	time.sleep(1)
	
