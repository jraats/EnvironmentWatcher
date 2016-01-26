import paramiko
from time import strftime, sleep
import os

## The class Hadoop. This makes a connection to the Hadoop cluster
class Hadoop:

	## The constructor
	#  @param self The object pointer.
	#  @param host The host to connect to. This is the main Hadoop controller
	#  @param username The username that runs hadoop
	#  @param password The password for the given user
	def __init__(self, host='', username='', password=''):
		self.ssh = paramiko.SSHClient()
		self.ssh.set_missing_host_key_policy(
			paramiko.AutoAddPolicy())
		self.ssh.connect(host, username=username,password=password)

		
	## Run a shell command on the Hadoop cluster
	#  @param self The object pointer.
	#  @param command The command to run
	#  @return The success lines and error lines in a dictionary
	def runCommand(self, command):
		stdin, stdout, stderr = self.ssh.exec_command(command)
		stdin.close()
		return {'success': stdout.read().splitlines(), 'error': stderr.read().splitlines()}
	
	## Upload a file to the Hadoop cluster
	#  @param self The object pointer.
	#  @param localPath The local path (absolute)
	#  @param remotePath The remote path (absolute)
	def uploadFile(self, localPath, remotePath):
		ftp = self.ssh.open_sftp()
		ftp.put(localPath, remotePath)
		ftp.close()
		
	## Upload a file to the Hadoop cluster relative to the average in directory	
	#  @see Hadoop::uploadFile
	def uploadFileInInDirectory(self, localPath, remotePath):
		self.uploadFile(localPath, '/hadoopData/Average/in/' + remotePath)
	
	## Download a file from the Hadoop cluster
	#  @param self The object pointer.
	#  @param remotePath The remote path (absolute)	
	#  @param localPath The local path (absolute)
	def downloadFile(self, remotePath, localPath):
		ftp = self.ssh.open_sftp()
		ftp.get(remotePath, localPath)
		ftp.close()
	
	## Download a file from the Hadoop cluster relative to the average out directory	
	#  @see Hadoop::downloadFile
	def downloadFileFromOutDirectory(self, remotePath, localPath):
		self.downloadFile('/hadoopData/Average/out/' + remotePath, localPath)
	
	## Calculate the average from the values in values by sending it to Hadoop
	#  @param self The object pointer.
	#  @param values The values that needs to be calculated
	#  @param uniqueName An unique name
	#  @return the calculated average in float
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
		
	## Return true if the remothe path exists
	#  @param self The object pointer.
	#  @param remotePath The remote path to check
	#  @return true if file exists
	def fileExist(self, remotePath):
		output = self.runCommand('ls ' + remotePath)
		return len(output['error']) == 0

	## Close the connection and release the resources
	#  @param self The object pointer.
	def close(self):
		self.ssh.close()