import paramiko
from time import strftime, sleep
import os

class Hadoop:

	def __init__(self, host='', username='', password=''):
		self.ssh = paramiko.SSHClient()
		self.ssh.set_missing_host_key_policy(
			paramiko.AutoAddPolicy())
		self.ssh.connect(host, username=username,password=password)
		
	def runCommand(self, command):
		stdin, stdout, stderr = self.ssh.exec_command(command)
		stdin.close()
		return {'success': stdout.read().splitlines(), 'error': stderr.read().splitlines()}
	
	def uploadFile(self, localPath, remotePath):
		ftp = self.ssh.open_sftp()
		ftp.put(localPath, remotePath)
		ftp.close()
		
	def uploadFileInInDirectory(self, localPath, remotePath):
		self.uploadFile(localPath, '/hadoopData/Average/in/' + remotePath)
		
	def downloadFile(self, remotePath, localPath):
		ftp = self.ssh.open_sftp()
		ftp.get(remotePath, localPath)
		ftp.close()
	
	def downloadFileFromOutDirectory(self, remotePath, localPath):
		self.downloadFile('/hadoopData/Average/out/' + remotePath, localPath)
		
	def createUploadWaitDownloadCatRemove(self, values, uniqueName):
		name = strftime("Y%Ym%md%dh%Hm%Ms%S" + uniqueName)
		file = open(name, 'w')
		for value in values:
			file.write(str(value)+'\n')
		file.close()
		
		self.uploadFileInInDirectory(name, name)
		while(self.fileExist('/hadoopData/Average/out/' + name) == False):
			sleep(0.01)
		self.downloadFileFromOutDirectory(name, name)
		
		f = open(name, "r")
		text = f.read()
		f.close()
		
		self.runCommand('rm /hadoopData/Average/out/' + name)
		os.remove(name)
		
		return float(text)
		
	def fileExist(self, remotePath):
		output = self.runCommand('ls ' + remotePath)
		return len(output['error']) == 0
	
	def close(self):
		self.ssh.close()