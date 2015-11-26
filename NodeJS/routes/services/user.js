var objectreciever = require ('./objectreciever.js');

var user = {
	getUsers: function(req, res) {
		var query = "SELECT * FROM user;";
		objectreciever.getObject(req, res, query, "user");
	},
	getUserById: function(req, res){
		if (!isNaN(req.params.id)) {
			var query = "SELECT * FROM user WHERE ID = "+req.params.id+";";
			objectreciever.getObject(req, res, query, "user");
		}else{
		res.json({error: "id moet numeriek zijn."});
		}
	},
	createUser: function(req, res){
		var object = { 
			name: req.body.name || '',
			passw: req.body.passw || ''
		};
		objectreciever.createObject(req, res, object, "user");
	},
	updateUserById: function(req, res){
		var object = {
			iD: req.params.id,
		};
		
		//set only values we want to update..
		if(req.body.name) 						object['name'] = req.body.name;
		if(req.body.passw) 						object['passw'] = req.body.passw;

		objectreciever.updateObject(req, res, object, "user");
	}
};

module.exports = user;