var objectreciever = require ('./objectreciever.js');
var util		= require('util');

var user = {
	getUsers: function(req, res) {
		var query = "SELECT * FROM user;";
		objectreciever.getObject(req, res, query, "user");
	},
	getUserByUsername: function(req, res){
		if (!objectreciever.CheckForInjection(req.params.username)) {
			var query = "SELECT * FROM user WHERE username = '"+req.params.username+"';";
			objectreciever.getObject(req, res, query, "user");
		}else{
		res.json({error: "space has been detected"});
		}
	},
	createUser: function(req, res){
		var object = { 
			username: req.body.username || '',
			password: req.body.password || '',
			productId: ''
		};
		objectreciever.createObject(req, res, object, "user");
	},
	updateUserByUsername: function(req, res){
		if(!objectreciever.CheckForInjection(req.params.username)){
			var object = {
				columnName: "username",
				columnItem: req.params.username
			};
			
			console.log(req.body);
			//set only values we want to update..
			if(typeof req.body.productId != 'undefined') 			object['productId'] = req.body.productId;
			if(typeof req.body.password != 'undefined') 			object['password'] = req.body.password;

		
			objectreciever.updateObject(req, res, object, "user");
		}
		else{
			res.json({error: "space has been detected"});
		}
	},
	deleteUserByUsername: function(req, res){
		if(!objectreciever.CheckForInjection(req.params.username)){
			var object = {
			columnName: req.body.columnName,
			//name of the person
			columnItem: req.params.username
			}
			
			objectreciever.DeleteObject(req, res, object, "user");
		}
		else{
			res.json({error: "space has been detected"});
		}
	}
};

module.exports = user;