var objectreciever = require ('./objectreciever.js');

var user = {
	getUsers: function(req, res) {
		var query = "SELECT * FROM user;";
		objectreciever.getObject(req, res, query, "user");
	},
	getUserByUsername: function(req, res){
		if (!isNaN(req.params.username)) {
			var query = "SELECT * FROM user WHERE username = "+req.params.username+";";
			objectreciever.getObject(req, res, query, "user");
		}else{
		res.json({error: "naam moet numeriek zijn."});
		}
	},
	createUser: function(req, res){
		var object = { 
			username: req.body.username || '',
			password: req.body.password || ''
		};
		objectreciever.createObject(req, res, object, "user");
	},
	updateUserByUsername: function(req, res){
		var object = {
			columnName: "username",
			columnItem: req.params.username
		};
		console.log(req.body.password);
		//set only values we want to update..
		if(req.body.productId) 			object['productId'] = req.body.productId;
		if(req.body.password) 			object['password'] = req.body.password;

		objectreciever.updateObject(req, res, object, "user");
	}
};

module.exports = user;