import api

n = api.Api()
n.getAll('user')
n.updateData('user', '1', {'username': 'gerret', 'pass': 'woord'})